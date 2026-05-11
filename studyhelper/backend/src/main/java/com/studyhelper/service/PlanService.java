package com.studyhelper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyhelper.common.Constants;
import com.studyhelper.dto.*;
import com.studyhelper.entity.DailyTask;
import com.studyhelper.entity.StudyPlan;
import com.studyhelper.mapper.DailyTaskMapper;
import com.studyhelper.mapper.StudyPlanMapper;
import com.studyhelper.utils.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanService {
    
    private final StudyPlanMapper studyPlanMapper;
    private final DailyTaskMapper dailyTaskMapper;
    
    public PlanService(StudyPlanMapper studyPlanMapper, DailyTaskMapper dailyTaskMapper) {
        this.studyPlanMapper = studyPlanMapper;
        this.dailyTaskMapper = dailyTaskMapper;
    }
    
    @Transactional
    public PlanVO createPlan(Long userId, CreatePlanDTO dto) {
        // 创建学习计划
        StudyPlan plan = new StudyPlan();
        plan.setUserId(userId);
        plan.setName(dto.getName());
        plan.setType(dto.getType() != null ? dto.getType() : 1);
        plan.setTargetDate(dto.getTargetDate());
        plan.setTotalDays(dto.getTasks() != null ? dto.getTasks().size() : 0);
        plan.setCompletedDays(0);
        plan.setStatus(Constants.PLAN_STATUS_ACTIVE);
        plan.setDescription(dto.getDescription());
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        
        studyPlanMapper.insert(plan);
        
        // 创建每日任务
        if (dto.getTasks() != null && !dto.getTasks().isEmpty()) {
            for (int i = 0; i < dto.getTasks().size(); i++) {
                TaskDTO taskDTO = dto.getTasks().get(i);
                DailyTask task = new DailyTask();
                task.setPlanId(plan.getId());
                task.setTaskDate(taskDTO.getTaskDate());
                task.setSubjectId(taskDTO.getSubjectId());
                task.setChapterId(taskDTO.getChapterId());
                task.setKnowledgeIds(taskDTO.getKnowledgeIds() != null ? 
                        JsonUtils.toJson(taskDTO.getKnowledgeIds()) : null);
                task.setTargetCount(taskDTO.getTargetCount());
                task.setCompletedCount(0);
                task.setStatus(Constants.TASK_STATUS_PENDING);
                task.setEstimatedTime(taskDTO.getEstimatedTime());
                task.setActualTime(0);
                task.setNotes(taskDTO.getNotes());
                task.setCreatedAt(LocalDateTime.now());
                task.setUpdatedAt(LocalDateTime.now());
                
                dailyTaskMapper.insert(task);
            }
        }
        
        return toPlanVO(plan);
    }
    
    public PageDTO<PlanVO> getPlans(Long userId, Integer page, Integer pageSize) {
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        
        Page<StudyPlan> pageParam = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<StudyPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyPlan::getUserId, userId)
                .orderByDesc(StudyPlan::getCreatedAt);
        
        Page<StudyPlan> result = studyPlanMapper.selectPage(pageParam, wrapper);
        
        List<PlanVO> voList = result.getRecords().stream()
                .map(this::toPlanVO)
                .collect(Collectors.toList());
        
        return PageDTO.of(voList, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }
    
    public PlanVO getPlanById(Long planId, Long userId) {
        StudyPlan plan = studyPlanMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("计划不存在或无权限访问");
        }
        return toPlanVO(plan);
    }
    
    @Transactional
    public void updatePlan(Long planId, Long userId, CreatePlanDTO dto) {
        StudyPlan plan = studyPlanMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("计划不存在或无权限访问");
        }
        
        if (dto.getName() != null) {
            plan.setName(dto.getName());
        }
        if (dto.getType() != null) {
            plan.setType(dto.getType());
        }
        if (dto.getTargetDate() != null) {
            plan.setTargetDate(dto.getTargetDate());
        }
        if (dto.getDescription() != null) {
            plan.setDescription(dto.getDescription());
        }
        plan.setUpdatedAt(LocalDateTime.now());
        
        studyPlanMapper.updateById(plan);
    }
    
    @Transactional
    public void deletePlan(Long planId, Long userId) {
        StudyPlan plan = studyPlanMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("计划不存在或无权限访问");
        }
        
        // 删除关联任务
        LambdaQueryWrapper<DailyTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(DailyTask::getPlanId, planId);
        dailyTaskMapper.delete(taskWrapper);
        
        // 删除计划
        studyPlanMapper.deleteById(planId);
    }
    
    @Transactional
    public void updatePlanStatus(Long planId, Long userId, Integer status) {
        StudyPlan plan = studyPlanMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("计划不存在或无权限访问");
        }
        
        plan.setStatus(status);
        
        // 如果完成，计算完成天数
        if (status == Constants.PLAN_STATUS_COMPLETED) {
            plan.setCompletedDays(plan.getTotalDays());
        }
        
        plan.setUpdatedAt(LocalDateTime.now());
        studyPlanMapper.updateById(plan);
    }
    
    public List<DailyTask> getDailyTasks(Long planId, Long userId, LocalDate date) {
        StudyPlan plan = studyPlanMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("计划不存在或无权限访问");
        }
        
        LambdaQueryWrapper<DailyTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyTask::getPlanId, planId);
        
        if (date != null) {
            wrapper.eq(DailyTask::getTaskDate, date);
        }
        
        wrapper.orderByAsc(DailyTask::getTaskDate);
        
        return dailyTaskMapper.selectList(wrapper);
    }
    
    @Transactional
    public void executeTask(Long taskId, Long userId, Integer actualTime, Integer completedCount) {
        DailyTask task = dailyTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        StudyPlan plan = studyPlanMapper.selectById(task.getPlanId());
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此任务");
        }
        
        // 更新任务
        if (actualTime != null) {
            task.setActualTime(actualTime);
        }
        if (completedCount != null) {
            task.setCompletedCount(completedCount);
        }
        
        // 判断任务是否完成
        if (task.getCompletedCount() >= task.getTargetCount()) {
            task.setStatus(Constants.TASK_STATUS_COMPLETED);
        } else if (task.getCompletedCount() > 0) {
            task.setStatus(Constants.TASK_STATUS_IN_PROGRESS);
        }
        
        task.setUpdatedAt(LocalDateTime.now());
        dailyTaskMapper.updateById(task);
        
        // 更新计划完成进度
        updatePlanProgress(plan);
    }
    
    private void updatePlanProgress(StudyPlan plan) {
        LambdaQueryWrapper<DailyTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyTask::getPlanId, plan.getId())
                .eq(DailyTask::getStatus, Constants.TASK_STATUS_COMPLETED);
        
        long completedCount = dailyTaskMapper.selectCount(wrapper);
        plan.setCompletedDays((int) completedCount);
        
        if (completedCount >= plan.getTotalDays()) {
            plan.setStatus(Constants.PLAN_STATUS_COMPLETED);
        }
        
        plan.setUpdatedAt(LocalDateTime.now());
        studyPlanMapper.updateById(plan);
    }
    
    private PlanVO toPlanVO(StudyPlan plan) {
        PlanVO vo = new PlanVO();
        vo.setPlanId(plan.getId());
        vo.setName(plan.getName());
        vo.setType(plan.getType());
        vo.setTargetDate(plan.getTargetDate() != null ? plan.getTargetDate().toString() : null);
        vo.setTotalDays(plan.getTotalDays());
        vo.setCompletedDays(plan.getCompletedDays());
        vo.setStatus(plan.getStatus());
        vo.setStatusName(getStatusName(plan.getStatus()));
        vo.setDescription(plan.getDescription());
        vo.setCreatedAt(plan.getCreatedAt() != null ? plan.getCreatedAt().toString() : null);
        return vo;
    }
    
    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "草稿";
            case 1 -> "进行中";
            case 2 -> "已完成";
            case 3 -> "已放弃";
            default -> "未知";
        };
    }
    
    public List<DailyTask> getTodayTasks(Long userId) {
        LocalDate today = LocalDate.now();
        
        // 获取用户的所有进行中计划
        LambdaQueryWrapper<StudyPlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(StudyPlan::getUserId, userId)
                .eq(StudyPlan::getStatus, Constants.PLAN_STATUS_ACTIVE);
        List<StudyPlan> activePlans = studyPlanMapper.selectList(planWrapper);
        
        if (activePlans.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> planIds = activePlans.stream().map(StudyPlan::getId).collect(Collectors.toList());
        
        // 查询今日任务
        LambdaQueryWrapper<DailyTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.in(DailyTask::getPlanId, planIds)
                .eq(DailyTask::getTaskDate, today)
                .orderByAsc(DailyTask::getCreatedAt);
        
        return dailyTaskMapper.selectList(taskWrapper);
    }
}
