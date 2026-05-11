package com.studyhelper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyhelper.common.Constants;
import com.studyhelper.dto.*;
import com.studyhelper.entity.DailyTask;
import com.studyhelper.entity.StudyPlan;
import com.studyhelper.mapper.DailyTaskMapper;
import com.studyhelper.mapper.StudyPlanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private StudyPlanMapper studyPlanMapper;

    @Mock
    private DailyTaskMapper dailyTaskMapper;

    @InjectMocks
    private PlanService planService;

    private StudyPlan testPlan;
    private DailyTask testTask;
    private CreatePlanDTO testCreateDto;
    private TaskDTO testTaskDto;

    @BeforeEach
    void setUp() {
        testPlan = new StudyPlan();
        testPlan.setId(1L);
        testPlan.setUserId(1L);
        testPlan.setName("数学学习计划");
        testPlan.setType(1);
        testPlan.setTargetDate(LocalDate.now().plusDays(30));
        testPlan.setTotalDays(30);
        testPlan.setCompletedDays(0);
        testPlan.setStatus(Constants.PLAN_STATUS_ACTIVE);
        testPlan.setDescription("提高数学成绩");
        testPlan.setCreatedAt(LocalDateTime.now());
        testPlan.setUpdatedAt(LocalDateTime.now());

        testTask = new DailyTask();
        testTask.setId(1L);
        testTask.setPlanId(1L);
        testTask.setTaskDate(LocalDate.now());
        testTask.setSubjectId(1L);
        testTask.setChapterId(1L);
        testTask.setTargetCount(10);
        testTask.setCompletedCount(0);
        testTask.setStatus(Constants.TASK_STATUS_PENDING);
        testTask.setEstimatedTime(60);
        testTask.setActualTime(0);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        testTaskDto = new TaskDTO();
        testTaskDto.setTaskDate(LocalDate.now());
        testTaskDto.setSubjectId(1L);
        testTaskDto.setChapterId(1L);
        testTaskDto.setKnowledgeIds(Arrays.asList(1L, 2L, 3L));
        testTaskDto.setTargetCount(10);
        testTaskDto.setEstimatedTime(60);
        testTaskDto.setNotes("完成函数章节");

        testCreateDto = new CreatePlanDTO();
        testCreateDto.setName("数学学习计划");
        testCreateDto.setType(1);
        testCreateDto.setTargetDate(LocalDate.now().plusDays(30));
        testCreateDto.setDescription("提高数学成绩");
        testCreateDto.setTasks(Arrays.asList(testTaskDto));
    }

    // ==================== createPlan Tests ====================

    @Test
    void createPlan_Success_WithTasks() {
        // Given
        doAnswer(invocation -> {
            StudyPlan plan = invocation.getArgument(0);
            plan.setId(1L);
            return null;
        }).when(studyPlanMapper).insert(any(StudyPlan.class));

        doAnswer(invocation -> {
            DailyTask task = invocation.getArgument(0);
            task.setId(1L);
            return null;
        }).when(dailyTaskMapper).insert(any(DailyTask.class));

        // When
        PlanVO result = planService.createPlan(1L, testCreateDto);

        // Then
        assertNotNull(result);
        assertEquals("数学学习计划", result.getName());
        assertEquals(1, result.getTotalDays());
        assertEquals("进行中", result.getStatusName());
        verify(studyPlanMapper, times(1)).insert(any(StudyPlan.class));
        verify(dailyTaskMapper, times(1)).insert(any(DailyTask.class));
    }

    @Test
    void createPlan_Success_WithoutTasks() {
        // Given
        CreatePlanDTO emptyDto = new CreatePlanDTO();
        emptyDto.setName("空计划");
        emptyDto.setType(1);

        doAnswer(invocation -> {
            StudyPlan plan = invocation.getArgument(0);
            plan.setId(1L);
            return null;
        }).when(studyPlanMapper).insert(any(StudyPlan.class));

        // When
        PlanVO result = planService.createPlan(1L, emptyDto);

        // Then
        assertNotNull(result);
        assertEquals("空计划", result.getName());
        assertEquals(0, result.getTotalDays());
        verify(studyPlanMapper, times(1)).insert(any(StudyPlan.class));
        verify(dailyTaskMapper, never()).insert(any(DailyTask.class));
    }

    @Test
    void createPlan_DefaultType() {
        // Given
        CreatePlanDTO dtoNoType = new CreatePlanDTO();
        dtoNoType.setName("计划无类型");

        doAnswer(invocation -> {
            StudyPlan plan = invocation.getArgument(0);
            plan.setId(1L);
            return null;
        }).when(studyPlanMapper).insert(any(StudyPlan.class));

        // When
        PlanVO result = planService.createPlan(1L, dtoNoType);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getType()); // Default type should be 1
    }

    // ==================== getPlans Tests ====================

    @Test
    void getPlans_Success() {
        // Given
        Page<StudyPlan> pageParam = new Page<>(1, 10);
        Page<StudyPlan> resultPage = new Page<>();
        resultPage.setRecords(Arrays.asList(testPlan));
        resultPage.setTotal(1);
        resultPage.setCurrent(1);
        resultPage.setSize(10);

        when(studyPlanMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<PlanVO> result = planService.getPlans(1L, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("数学学习计划", result.getList().get(0).getName());
        assertEquals("进行中", result.getList().get(0).getStatusName());
    }

    @Test
    void getPlans_EmptyList() {
        // Given
        Page<StudyPlan> resultPage = new Page<>();
        resultPage.setRecords(Collections.emptyList());
        resultPage.setTotal(0);
        resultPage.setCurrent(1);
        resultPage.setSize(10);

        when(studyPlanMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<PlanVO> result = planService.getPlans(1L, 1, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    void getPlans_NullPagination() {
        // Given
        Page<StudyPlan> resultPage = new Page<>();
        resultPage.setRecords(Arrays.asList(testPlan));
        resultPage.setTotal(1);
        resultPage.setCurrent(1);
        resultPage.setSize(10);

        when(studyPlanMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<PlanVO> result = planService.getPlans(1L, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    // ==================== getPlanById Tests ====================

    @Test
    void getPlanById_Success() {
        // Given
        when(studyPlanMapper.selectById(1L)).thenReturn(testPlan);

        // When
        PlanVO result = planService.getPlanById(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getPlanId());
        assertEquals("数学学习计划", result.getName());
    }

    @Test
    void getPlanById_NotFound_ThrowsException() {
        // Given
        when(studyPlanMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            planService.getPlanById(999L, 1L);
        });
        assertEquals("计划不存在或无权限访问", exception.getMessage());
    }

    @Test
    void getPlanById_NoPermission_ThrowsException() {
        // Given
        when(studyPlanMapper.selectById(1L)).thenReturn(testPlan);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            planService.getPlanById(1L, 999L); // Different user
        });
        assertEquals("计划不存在或无权限访问", exception.getMessage());
    }

    // ==================== getDailyTasks Tests ====================

    @Test
    void getDailyTasks_Success_WithDate() {
        // Given
        when(studyPlanMapper.selectById(1L)).thenReturn(testPlan);
        when(dailyTaskMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(testTask));

        // When
        List<DailyTask> result = planService.getDailyTasks(1L, 1L, LocalDate.now());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getDailyTasks_Success_WithoutDate() {
        // Given
        when(studyPlanMapper.selectById(1L)).thenReturn(testPlan);
        when(dailyTaskMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(testTask));

        // When
        List<DailyTask> result = planService.getDailyTasks(1L, 1L, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getDailyTasks_PlanNotFound_ThrowsException() {
        // Given
        when(studyPlanMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            planService.getDailyTasks(999L, 1L, null);
        });
        assertEquals("计划不存在或无权限访问", exception.getMessage());
    }

    // ==================== executeTask Tests ====================

    @Test
    void executeTask_Success_Complete() {
        // Given
        testTask.setTargetCount(10);
        testTask.setCompletedCount(10);

        when(dailyTaskMapper.selectById(1L)).thenReturn(testTask);
        when(studyPlanMapper.selectById(1L)).thenReturn(testPlan);
        when(dailyTaskMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(30L);

        // When
        planService.executeTask(1L, 1L, 60, 10);

        // Then
        verify(dailyTaskMapper, times(1)).updateById(any(DailyTask.class));
        verify(studyPlanMapper, times(1)).updateById(any(StudyPlan.class));
    }

    @Test
    void executeTask_Success_InProgress() {
        // Given
        testTask.setTargetCount(10);
        testTask.setCompletedCount(5);

        when(dailyTaskMapper.selectById(1L)).thenReturn(testTask);
        when(studyPlanMapper.selectById(1L)).thenReturn(testPlan);
        when(dailyTaskMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

        // When
        planService.executeTask(1L, 1L, 30, 5);

        // Then
        verify(dailyTaskMapper, times(1)).updateById(any(DailyTask.class));
        verify(studyPlanMapper, times(1)).updateById(any(StudyPlan.class));
    }

    @Test
    void executeTask_TaskNotFound_ThrowsException() {
        // Given
        when(dailyTaskMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            planService.executeTask(999L, 1L, 60, 5);
        });
        assertEquals("任务不存在", exception.getMessage());
    }

    @Test
    void executeTask_NoPermission_ThrowsException() {
        // Given
        when(dailyTaskMapper.selectById(1L)).thenReturn(testTask);
        when(studyPlanMapper.selectById(1L)).thenReturn(testPlan);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            planService.executeTask(1L, 999L, 60, 5); // Different user
        });
        assertEquals("无权限操作此任务", exception.getMessage());
    }

    // ==================== updatePlanStatus Tests ====================

    @Test
    void updatePlanStatus_Complete() {
        // Given
        when(studyPlanMapper.selectById(1L)).thenReturn(testPlan);

        // When
        planService.updatePlanStatus(1L, 1L, Constants.PLAN_STATUS_COMPLETED);

        // Then
        verify(studyPlanMapper, times(1)).updateById(any(StudyPlan.class));
    }

    @Test
    void updatePlanStatus_NotFound_ThrowsException() {
        // Given
        when(studyPlanMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            planService.updatePlanStatus(999L, 1L, Constants.PLAN_STATUS_COMPLETED);
        });
        assertEquals("计划不存在或无权限访问", exception.getMessage());
    }

    // ==================== deletePlan Tests ====================

    @Test
    void deletePlan_Success() {
        // Given
        when(studyPlanMapper.selectById(1L)).thenReturn(testPlan);

        // When
        planService.deletePlan(1L, 1L);

        // Then
        verify(dailyTaskMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(studyPlanMapper, times(1)).deleteById(1L);
    }

    @Test
    void deletePlan_NotFound_ThrowsException() {
        // Given
        when(studyPlanMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            planService.deletePlan(999L, 1L);
        });
        assertEquals("计划不存在或无权限访问", exception.getMessage());
    }

    // ==================== Status Name Tests ====================

    @Test
    void getStatusName_AllStatuses() {
        // Test all status names
        Page<StudyPlan> resultPage = new Page<>();
        StudyPlan draftPlan = new StudyPlan();
        draftPlan.setId(1L);
        draftPlan.setUserId(1L);
        draftPlan.setName("草稿计划");
        draftPlan.setStatus(0);
        draftPlan.setCreatedAt(LocalDateTime.now());

        StudyPlan completedPlan = new StudyPlan();
        completedPlan.setId(2L);
        completedPlan.setUserId(1L);
        completedPlan.setName("已完成计划");
        completedPlan.setStatus(2);
        completedPlan.setCompletedDays(10);
        completedPlan.setTotalDays(10);
        completedPlan.setCreatedAt(LocalDateTime.now());

        StudyPlan abandonedPlan = new StudyPlan();
        abandonedPlan.setId(3L);
        abandonedPlan.setUserId(1L);
        abandonedPlan.setName("已放弃计划");
        abandonedPlan.setStatus(3);
        abandonedPlan.setCreatedAt(LocalDateTime.now());

        resultPage.setRecords(Arrays.asList(draftPlan, completedPlan, abandonedPlan));
        resultPage.setTotal(3);

        when(studyPlanMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<PlanVO> result = planService.getPlans(1L, 1, 10);

        // Then
        assertEquals("草稿", result.getList().get(0).getStatusName());
        assertEquals("已完成", result.getList().get(1).getStatusName());
        assertEquals("已放弃", result.getList().get(2).getStatusName());
    }
}
