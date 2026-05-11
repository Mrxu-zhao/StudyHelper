package com.studyhelper.controller;

import com.studyhelper.common.Result;
import com.studyhelper.dto.CreatePlanDTO;
import com.studyhelper.dto.PageDTO;
import com.studyhelper.dto.PlanVO;
import com.studyhelper.entity.DailyTask;
import com.studyhelper.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plan")
public class PlanController {
    
    private final PlanService planService;
    
    public PlanController(PlanService planService) {
        this.planService = planService;
    }
    
    /**
     * 创建学习计划
     */
    @PostMapping
    public Result<PlanVO> createPlan(HttpServletRequest request, @Valid @RequestBody CreatePlanDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        PlanVO vo = planService.createPlan(userId, dto);
        return Result.success("计划创建成功", vo);
    }
    
    /**
     * 获取学习计划列表
     */
    @GetMapping
    public Result<PageDTO<PlanVO>> getPlans(HttpServletRequest request,
                                             @RequestParam(required = false, defaultValue = "1") Integer page,
                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        PageDTO<PlanVO> result = planService.getPlans(userId, page, pageSize);
        return Result.success(result);
    }
    
    /**
     * 获取学习计划详情
     */
    @GetMapping("/{id}")
    public Result<PlanVO> getPlanById(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        PlanVO vo = planService.getPlanById(id, userId);
        return Result.success(vo);
    }
    
    /**
     * 更新学习计划
     */
    @PutMapping("/{id}")
    public Result<Void> updatePlan(HttpServletRequest request, @PathVariable Long id,
                                   @RequestBody CreatePlanDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        planService.updatePlan(id, userId, dto);
        return Result.success("计划更新成功", null);
    }
    
    /**
     * 删除学习计划
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePlan(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        planService.deletePlan(id, userId);
        return Result.success("计划删除成功", null);
    }
    
    /**
     * 更新计划状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updatePlanStatus(HttpServletRequest request, @PathVariable Long id,
                                         @RequestBody Map<String, Integer> params) {
        Long userId = (Long) request.getAttribute("userId");
        Integer status = params.get("status");
        planService.updatePlanStatus(id, userId, status);
        return Result.success("状态更新成功", null);
    }
    
    /**
     * 获取每日任务列表
     */
    @GetMapping("/{id}/tasks")
    public Result<List<DailyTask>> getDailyTasks(HttpServletRequest request, @PathVariable Long id,
                                                 @RequestParam(required = false) 
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long userId = (Long) request.getAttribute("userId");
        List<DailyTask> tasks = planService.getDailyTasks(id, userId, date);
        return Result.success(tasks);
    }
    
    /**
     * 执行任务
     */
    @PostMapping("/tasks/{taskId}/execute")
    public Result<Void> executeTask(HttpServletRequest request, @PathVariable Long taskId,
                                    @RequestBody Map<String, Integer> params) {
        Long userId = (Long) request.getAttribute("userId");
        Integer actualTime = params.get("actualTime");
        Integer completedCount = params.get("completedCount");
        planService.executeTask(taskId, userId, actualTime, completedCount);
        return Result.success("任务执行成功", null);
    }
}
