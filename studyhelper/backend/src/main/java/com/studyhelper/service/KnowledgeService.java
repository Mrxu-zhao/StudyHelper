package com.studyhelper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyhelper.dto.*;
import com.studyhelper.entity.Chapter;
import com.studyhelper.entity.KnowledgePoint;
import com.studyhelper.entity.Subject;
import com.studyhelper.mapper.ChapterMapper;
import com.studyhelper.mapper.KnowledgePointMapper;
import com.studyhelper.mapper.SubjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeService {
    
    private final SubjectMapper subjectMapper;
    private final ChapterMapper chapterMapper;
    private final KnowledgePointMapper knowledgePointMapper;
    
    public KnowledgeService(SubjectMapper subjectMapper, ChapterMapper chapterMapper, 
                            KnowledgePointMapper knowledgePointMapper) {
        this.subjectMapper = subjectMapper;
        this.chapterMapper = chapterMapper;
        this.knowledgePointMapper = knowledgePointMapper;
    }
    
    public List<SubjectVO> getSubjects(Integer grade) {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subject::getIsActive, 1)
                .orderByAsc(Subject::getSortOrder);
        
        if (grade != null) {
            wrapper.and(w -> w.eq(Subject::getGradeLevel, grade).or().eq(Subject::getGradeLevel, 4));
        }
        
        List<Subject> subjects = subjectMapper.selectList(wrapper);
        
        return subjects.stream().map(subject -> {
            SubjectVO vo = new SubjectVO();
            vo.setSubjectId(subject.getId());
            vo.setSubjectName(subject.getName());
            vo.setSubjectCode(subject.getCode());
            vo.setIcon(subject.getIconUrl());
            vo.setGradeLevel(subject.getGradeLevel());
            
            // 统计章节数和知识点数
            LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
            chapterWrapper.eq(Chapter::getSubjectId, subject.getId());
            long chapterCount = chapterMapper.selectCount(chapterWrapper);
            vo.setChapterCount((int) chapterCount);
            
            LambdaQueryWrapper<KnowledgePoint> kpWrapper = new LambdaQueryWrapper<>();
            kpWrapper.inSql(KnowledgePoint::getChapterId, 
                    "SELECT id FROM chapters WHERE subject_id = " + subject.getId());
            long knowledgeCount = knowledgePointMapper.selectCount(kpWrapper);
            vo.setKnowledgeCount((int) knowledgeCount);
            
            return vo;
        }).collect(Collectors.toList());
    }
    
    public List<ChapterTreeVO> getChapterTree(Long subjectId, Integer grade) {
        // 查询所有章节
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getSubjectId, subjectId)
                .orderByAsc(Chapter::getOrderNum);
        
        List<Chapter> allChapters = chapterMapper.selectList(wrapper);
        
        // 构建树形结构
        return buildChapterTree(allChapters, 0L);
    }
    
    private List<ChapterTreeVO> buildChapterTree(List<Chapter> chapters, Long parentId) {
        List<ChapterTreeVO> result = new ArrayList<>();
        
        for (Chapter chapter : chapters) {
            if ((parentId == null && chapter.getParentId() == null) || 
                (parentId != null && parentId.equals(chapter.getParentId()))) {
                ChapterTreeVO vo = new ChapterTreeVO();
                vo.setChapterId(chapter.getId());
                vo.setChapterName(chapter.getName());
                vo.setLevel(chapter.getLevel());
                vo.setParentId(chapter.getParentId());
                vo.setKnowledgeCount(chapter.getKnowledgeCount());
                
                // 递归加载子章节
                vo.setChildren(buildChapterTree(chapters, chapter.getId()));
                
                result.add(vo);
            }
        }
        
        return result;
    }
    
    public PageDTO<KnowledgePointVO> getKnowledgePoints(Long chapterId, Long subjectId, 
                                                         Integer page, Integer pageSize) {
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        
        Page<KnowledgePoint> pageParam = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<KnowledgePoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgePoint::getIsActive, 1);
        
        if (chapterId != null) {
            wrapper.eq(KnowledgePoint::getChapterId, chapterId);
        }
        
        if (subjectId != null) {
            // 通过章节关联查询
            LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
            chapterWrapper.eq(Chapter::getSubjectId, subjectId);
            List<Chapter> chapters = chapterMapper.selectList(chapterWrapper);
            List<Long> chapterIds = chapters.stream().map(Chapter::getId).collect(Collectors.toList());
            
            if (!chapterIds.isEmpty()) {
                wrapper.in(KnowledgePoint::getChapterId, chapterIds);
            }
        }
        
        wrapper.orderByDesc(KnowledgePoint::getImportance);
        
        Page<KnowledgePoint> result = knowledgePointMapper.selectPage(pageParam, wrapper);
        
        List<KnowledgePointVO> voList = result.getRecords().stream().map(kp -> {
            KnowledgePointVO vo = new KnowledgePointVO();
            vo.setKnowledgeId(kp.getId());
            vo.setChapterId(kp.getChapterId());
            vo.setTitle(kp.getTitle());
            vo.setImportanceLevel(kp.getImportance());
            vo.setDifficultyLevel(kp.getDifficulty());
            vo.setExamFrequency(getFrequencyText(kp.getFrequency()));
            vo.setContent(kp.getContent());
            vo.setTags(kp.getTags());
            vo.setViewCount(kp.getViewCount());
            return vo;
        }).collect(Collectors.toList());
        
        return PageDTO.of(voList, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }
    
    private String getFrequencyText(String frequency) {
        if (frequency == null) return "中频";
        return switch (frequency) {
            case "high" -> "高频";
            case "low" -> "低频";
            default -> "中频";
        };
    }
    
    public void incrementViewCount(Long knowledgeId) {
        // 实际应该使用update语句
        KnowledgePoint kp = knowledgePointMapper.selectById(knowledgeId);
        if (kp != null) {
            kp.setViewCount(kp.getViewCount() + 1);
            knowledgePointMapper.updateById(kp);
        }
    }
    
    public KnowledgePointVO getKnowledgePointDetail(Long knowledgeId) {
        KnowledgePoint kp = knowledgePointMapper.selectById(knowledgeId);
        if (kp == null) {
            return null;
        }
        KnowledgePointVO vo = new KnowledgePointVO();
        vo.setKnowledgeId(kp.getId());
        vo.setChapterId(kp.getChapterId());
        vo.setTitle(kp.getTitle());
        vo.setImportanceLevel(kp.getImportance());
        vo.setDifficultyLevel(kp.getDifficulty());
        vo.setExamFrequency(getFrequencyText(kp.getFrequency()));
        vo.setContent(kp.getContent());
        vo.setTags(kp.getTags());
        vo.setViewCount(kp.getViewCount());
        return vo;
    }
}
