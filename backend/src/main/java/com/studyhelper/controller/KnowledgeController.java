package com.studyhelper.controller;

import com.studyhelper.common.Result;
import com.studyhelper.dto.ChapterTreeVO;
import com.studyhelper.dto.KnowledgePointVO;
import com.studyhelper.dto.PageDTO;
import com.studyhelper.dto.SubjectVO;
import com.studyhelper.service.KnowledgeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {
    
    private final KnowledgeService knowledgeService;
    
    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }
    
    /**
     * 获取学科列表
     */
    @GetMapping("/subjects")
    public Result<List<SubjectVO>> getSubjects(@RequestParam(required = false) Integer grade) {
        List<SubjectVO> subjects = knowledgeService.getSubjects(grade);
        return Result.success(subjects);
    }
    
    /**
     * 获取章节树
     */
    @GetMapping("/chapters")
    public Result<List<ChapterTreeVO>> getChapters(@RequestParam Long subjectId,
                                                   @RequestParam(required = false) Integer grade) {
        List<ChapterTreeVO> chapters = knowledgeService.getChapterTree(subjectId, grade);
        return Result.success(chapters);
    }
    
    /**
     * 获取知识点列表
     */
    @GetMapping("/points")
    public Result<PageDTO<KnowledgePointVO>> getKnowledgePoints(
            @RequestParam(required = false) Long chapterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageDTO<KnowledgePointVO> result = knowledgeService.getKnowledgePoints(chapterId, subjectId, page, pageSize);
        return Result.success(result);
    }
    
    /**
     * 获取知识点详情
     */
    @GetMapping("/points/{id}")
    public Result<KnowledgePointVO> getKnowledgePointDetail(@PathVariable Long id) {
        // 增加浏览次数
        knowledgeService.incrementViewCount(id);
        // 实际应该返回详情，这里简化处理
        return Result.success(null);
    }
}
