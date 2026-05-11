package com.studyhelper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyhelper.common.Constants;
import com.studyhelper.dto.ExamResultVO;
import com.studyhelper.dto.PageDTO;
import com.studyhelper.dto.PaperVO;
import com.studyhelper.entity.*;
import com.studyhelper.mapper.*;
import com.studyhelper.utils.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaperService {
    
    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;
    private final ExamRecordMapper examRecordMapper;
    private final SubjectMapper subjectMapper;
    private final WrongQuestionMapper wrongQuestionMapper;
    
    public PaperService(PaperMapper paperMapper, PaperQuestionMapper paperQuestionMapper,
                       QuestionMapper questionMapper, ExamRecordMapper examRecordMapper,
                       SubjectMapper subjectMapper, WrongQuestionMapper wrongQuestionMapper) {
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
        this.examRecordMapper = examRecordMapper;
        this.subjectMapper = subjectMapper;
        this.wrongQuestionMapper = wrongQuestionMapper;
    }
    
    public PageDTO<PaperVO> getPapers(Long subjectId, Integer gradeLevel, Integer page, Integer pageSize) {
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        
        Page<Paper> pageParam = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Paper::getIsPublished, 1);
        
        if (subjectId != null) {
            wrapper.eq(Paper::getSubjectId, subjectId);
        }
        if (gradeLevel != null) {
            wrapper.eq(Paper::getGradeLevel, gradeLevel);
        }
        
        wrapper.orderByDesc(Paper::getCreatedAt);
        
        Page<Paper> result = paperMapper.selectPage(pageParam, wrapper);
        
        List<PaperVO> voList = result.getRecords().stream()
                .map(this::toPaperVO)
                .collect(Collectors.toList());
        
        return PageDTO.of(voList, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }
    
    public PaperVO getPaperById(Long paperId) {
        Paper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }
        return toPaperVO(paper);
    }
    
    public List<Question> getPaperQuestions(Long paperId) {
        LambdaQueryWrapper<PaperQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperQuestion::getPaperId, paperId)
                .orderByAsc(PaperQuestion::getOrderNum);
        
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(wrapper);
        
        if (paperQuestions.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> questionIds = paperQuestions.stream()
                .map(PaperQuestion::getQuestionId)
                .collect(Collectors.toList());
        
        return questionMapper.selectBatchIds(questionIds);
    }
    
    @Transactional
    public Long createPaper(Long userId, String name, Long subjectId, Integer gradeLevel,
                           List<Long> questionIds, String description) {
        Paper paper = new Paper();
        paper.setName(name);
        paper.setSubjectId(subjectId);
        paper.setGradeLevel(gradeLevel);
        paper.setSourceType(1); // 手动选题
        paper.setCreatedBy(userId);
        paper.setDescription(description);
        paper.setIsPublished(0);
        paper.setCreatedAt(LocalDateTime.now());
        paper.setUpdatedAt(LocalDateTime.now());
        
        // 计算总分和平均难度
        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        int totalScore = questions.stream().mapToInt(Question::getScore).sum();
        double avgDifficulty = questions.stream()
                .mapToInt(Question::getDifficulty)
                .average()
                .orElse(0);
        
        paper.setTotalScore(totalScore);
        paper.setQuestionCount(questions.size());
        paper.setDifficultyAvg(BigDecimal.valueOf(avgDifficulty));
        
        paperMapper.insert(paper);
        
        // 添加题目关联
        for (int i = 0; i < questionIds.size(); i++) {
            Long qId = questionIds.get(i);
            Question q = questionMapper.selectById(qId);
            
            PaperQuestion pq = new PaperQuestion();
            pq.setPaperId(paper.getId());
            pq.setQuestionId(qId);
            pq.setOrderNum(i + 1);
            pq.setScore(q != null ? q.getScore() : 5);
            pq.setIsRequired(1);
            
            paperQuestionMapper.insert(pq);
            
            // 更新题目使用次数
            if (q != null) {
                q.setUsageCount(q.getUsageCount() + 1);
                questionMapper.updateById(q);
            }
        }
        
        return paper.getId();
    }
    
    @Transactional
    public Long generatePaper(Long userId, Long subjectId, Integer gradeLevel, Integer totalScore,
                             Map<Integer, Integer> questionTypeDistribution) {
        // 智能组卷逻辑
        List<Question> selectedQuestions = new ArrayList<>();
        int remainingScore = totalScore;
        
        for (Map.Entry<Integer, Integer> entry : questionTypeDistribution.entrySet()) {
            Integer type = entry.getKey();
            Integer count = entry.getValue();
            
            LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Question::getSubjectId, subjectId)
                    .eq(Question::getIsActive, 1);
            
            if (gradeLevel != null) {
                wrapper.apply("chapter_id IN (SELECT id FROM chapters WHERE subject_id = " + subjectId + ")");
            }
            
            List<Question> questions = questionMapper.selectList(wrapper);
            
            // 按难度排序，随机选择
            questions.sort(Comparator.comparing(Question::getDifficulty));
            int selectCount = Math.min(count, questions.size());
            
            for (int i = 0; i < selectCount && remainingScore > 0; i++) {
                Question q = questions.get(i);
                selectedQuestions.add(q);
                remainingScore -= q.getScore();
            }
        }
        
        // 创建试卷
        String name = "智能组卷-" + System.currentTimeMillis();
        return createPaper(userId, name, subjectId, gradeLevel,
                selectedQuestions.stream().map(Question::getId).collect(Collectors.toList()), null);
    }
    
    @Transactional
    public Long startExam(Long userId, Long paperId) {
        Paper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }
        
        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setPaperId(paperId);
        record.setTotalScore(paper.getTotalScore());
        record.setIsCompleted(0);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        examRecordMapper.insert(record);
        
        return record.getId();
    }
    
    @Transactional
    public ExamResultVO submitExam(Long userId, Long examRecordId, String answersJson, Integer duration) {
        ExamRecord record = examRecordMapper.selectById(examRecordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new RuntimeException("考试记录不存在或无权限");
        }
        
        Paper paper = paperMapper.selectById(record.getPaperId());
        List<Question> questions = getPaperQuestions(record.getPaperId());
        
        // 计算得分
        int totalScore = 0;
        Map<String, String> answers = JsonUtils.fromJson(answersJson, 
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        
        List<WrongQuestion> wrongQuestions = new ArrayList<>();
        List<Long> wrongQuestionIds = new ArrayList<>();
        
        for (Question q : questions) {
            String userAnswer = answers.get(String.valueOf(q.getId()));
            boolean isCorrect = q.getAnswer().equals(userAnswer);
            
            if (isCorrect) {
                totalScore += q.getScore();
                // 更新题目正确次数
                q.setCorrectCount(q.getCorrectCount() + 1);
                q.setUsageCount(q.getUsageCount() + 1);
                questionMapper.updateById(q);
            } else {
                wrongQuestionIds.add(q.getId());
                // 添加到错题本
                WrongQuestion wq = new WrongQuestion();
                wq.setUserId(userId);
                wq.setQuestionId(q.getId());
                wq.setPaperId(record.getPaperId());
                wq.setExamRecordId(examRecordId);
                wq.setUserAnswer(userAnswer);
                wq.setCorrectAnswer(q.getAnswer());
                wq.setIsReviewed(0);
                wq.setReviewCount(0);
                wq.setCreatedAt(LocalDateTime.now());
                wq.setUpdatedAt(LocalDateTime.now());
                wrongQuestions.add(wq);
            }
        }
        
        // 批量保存错题
        if (!wrongQuestions.isEmpty()) {
            // 先检查是否已存在
            for (WrongQuestion wq : wrongQuestions) {
                LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(WrongQuestion::getUserId, userId)
                        .eq(WrongQuestion::getQuestionId, wq.getQuestionId());
                if (wrongQuestionMapper.selectCount(wrapper) == 0) {
                    wrongQuestionMapper.insert(wq);
                }
            }
        }
        
        // 更新考试记录
        record.setObtainedScore(totalScore);
        record.setScoreRate(BigDecimal.valueOf((double) totalScore / record.getTotalScore() * 100));
        record.setDuration(duration);
        record.setAnswersJson(answersJson);
        record.setIsCompleted(1);
        record.setUpdatedAt(LocalDateTime.now());
        
        examRecordMapper.updateById(record);
        
        // 构建结果VO
        ExamResultVO result = new ExamResultVO();
        result.setRecordId(examRecordId);
        result.setTotalScore(record.getTotalScore());
        result.setObtainedScore(totalScore);
        result.setScoreRate(record.getScoreRate());
        result.setCorrectCount(questions.size() - wrongQuestionIds.size());
        result.setWrongCount(wrongQuestionIds.size());
        result.setWrongQuestionIds(wrongQuestionIds);
        
        return result;
    }
    
    public List<ExamRecord> getExamRecords(Long userId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getUserId, userId)
                .orderByDesc(ExamRecord::getCreatedAt);
        
        return examRecordMapper.selectList(wrapper);
    }
    
    public PaperVO toPaperVO(Paper paper) {
        PaperVO vo = new PaperVO();
        vo.setPaperId(paper.getId());
        vo.setName(paper.getName());
        vo.setSubjectId(paper.getSubjectId());
        vo.setGradeLevel(paper.getGradeLevel());
        vo.setTotalScore(paper.getTotalScore());
        vo.setTotalTime(paper.getTotalTime());
        vo.setQuestionCount(paper.getQuestionCount());
        vo.setDifficultyAvg(paper.getDifficultyAvg() != null ? 
                paper.getDifficultyAvg().intValue() : null);
        vo.setSourceType(paper.getSourceType());
        vo.setDescription(paper.getDescription());
        vo.setIsPublished(paper.getIsPublished());
        vo.setCreatedAt(paper.getCreatedAt() != null ? paper.getCreatedAt().toString() : null);
        
        // 获取学科名称
        Subject subject = subjectMapper.selectById(paper.getSubjectId());
        if (subject != null) {
            vo.setSubjectName(subject.getName());
        }
        
        return vo;
    }
}
