package com.studyhelper.controller;

import com.studyhelper.common.Result;
import com.studyhelper.dto.ExamResultVO;
import com.studyhelper.dto.PageDTO;
import com.studyhelper.dto.PaperVO;
import com.studyhelper.entity.ExamRecord;
import com.studyhelper.entity.Question;
import com.studyhelper.service.PaperService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/papers")
public class PaperController {
    
    private final PaperService paperService;
    
    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }
    
    /**
     * 获取试卷列表
     */
    @GetMapping
    public Result<PageDTO<PaperVO>> getPapers(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Integer gradeLevel,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageDTO<PaperVO> result = paperService.getPapers(subjectId, gradeLevel, page, pageSize);
        return Result.success(result);
    }
    
    /**
     * 获取试卷详情
     */
    @GetMapping("/{id}")
    public Result<PaperVO> getPaperById(@PathVariable Long id) {
        PaperVO vo = paperService.getPaperById(id);
        return Result.success(vo);
    }
    
    /**
     * 获取试卷题目
     */
    @GetMapping("/{id}/questions")
    public Result<List<Question>> getPaperQuestions(@PathVariable Long id) {
        List<Question> questions = paperService.getPaperQuestions(id);
        return Result.success(questions);
    }
    
    /**
     * 创建试卷（手动选题）
     */
    @PostMapping
    public Result<Map<String, Long>> createPaper(HttpServletRequest request,
                                                 @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        String name = (String) params.get("name");
        Long subjectId = ((Number) params.get("subjectId")).longValue();
        Integer gradeLevel = params.get("gradeLevel") != null ? ((Number) params.get("gradeLevel")).intValue() : null;
        @SuppressWarnings("unchecked")
        List<Long> questionIds = (List<Long>) params.get("questionIds");
        String description = (String) params.get("description");
        
        Long paperId = paperService.createPaper(userId, name, subjectId, gradeLevel, questionIds, description);
        return Result.success("试卷创建成功", Map.of("paperId", paperId));
    }
    
    /**
     * 智能组卷
     */
    @PostMapping("/generate")
    public Result<Map<String, Long>> generatePaper(HttpServletRequest request,
                                                    @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long subjectId = ((Number) params.get("subjectId")).longValue();
        Integer gradeLevel = params.get("gradeLevel") != null ? ((Number) params.get("gradeLevel")).intValue() : null;
        Integer totalScore = ((Number) params.get("totalScore")).intValue();
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> questionTypeDistribution = (Map<Integer, Integer>) params.get("questionTypeDistribution");
        
        Long paperId = paperService.generatePaper(userId, subjectId, gradeLevel, totalScore, questionTypeDistribution);
        return Result.success("智能组卷成功", Map.of("paperId", paperId));
    }
    
    /**
     * 开始考试
     */
    @PostMapping("/{paperId}/exam")
    public Result<Map<String, Long>> startExam(HttpServletRequest request, @PathVariable Long paperId) {
        Long userId = (Long) request.getAttribute("userId");
        Long examRecordId = paperService.startExam(userId, paperId);
        return Result.success("开始考试", Map.of("examRecordId", examRecordId));
    }
    
    /**
     * 提交考试
     */
    @PostMapping("/exam/{examRecordId}/submit")
    public Result<ExamResultVO> submitExam(HttpServletRequest request, @PathVariable Long examRecordId,
                                   @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        String answersJson = (String) params.get("answersJson");
        Integer duration = params.get("duration") != null ? ((Number) params.get("duration")).intValue() : 0;
        ExamResultVO result = paperService.submitExam(userId, examRecordId, answersJson, duration);
        return Result.success("提交成功", result);
    }
    
    /**
     * 获取考试记录
     */
    @GetMapping("/exam/records")
    public Result<List<ExamRecord>> getExamRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ExamRecord> records = paperService.getExamRecords(userId);
        return Result.success(records);
    }
}
