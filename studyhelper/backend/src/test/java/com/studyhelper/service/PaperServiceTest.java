package com.studyhelper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyhelper.common.Constants;
import com.studyhelper.dto.ExamResultVO;
import com.studyhelper.dto.PageDTO;
import com.studyhelper.dto.PaperVO;
import com.studyhelper.entity.*;
import com.studyhelper.mapper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaperServiceTest {

    @Mock
    private PaperMapper paperMapper;

    @Mock
    private PaperQuestionMapper paperQuestionMapper;

    @Mock
    private QuestionMapper questionMapper;

    @Mock
    private ExamRecordMapper examRecordMapper;

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private WrongQuestionMapper wrongQuestionMapper;

    @InjectMocks
    private PaperService paperService;

    private Paper testPaper;
    private Question testQuestion;
    private ExamRecord testExamRecord;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        testPaper = new Paper();
        testPaper.setId(1L);
        testPaper.setName("期末数学试卷");
        testPaper.setSubjectId(1L);
        testPaper.setGradeLevel(1);
        testPaper.setTotalScore(100);
        testPaper.setTotalTime(120);
        testPaper.setQuestionCount(20);
        testPaper.setDifficultyAvg(BigDecimal.valueOf(3.5));
        testPaper.setSourceType(1);
        testPaper.setCreatedBy(1L);
        testPaper.setIsPublished(1);
        testPaper.setCreatedAt(LocalDateTime.now());
        testPaper.setUpdatedAt(LocalDateTime.now());

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setSubjectId(1L);
        testQuestion.setChapterId(1L);
        testQuestion.setType(1);
        testQuestion.setDifficulty(3);
        testQuestion.setScore(5);
        testQuestion.setContent("什么是函数？");
        testQuestion.setOptions("[{\"A\":\"选项A\"},{\"B\":\"选项B\"}]");
        testQuestion.setAnswer("A");
        testQuestion.setAnalysis("函数是...");
        testQuestion.setUsageCount(0);
        testQuestion.setCorrectCount(0);
        testQuestion.setIsActive(1);

        testExamRecord = new ExamRecord();
        testExamRecord.setId(1L);
        testExamRecord.setUserId(1L);
        testExamRecord.setPaperId(1L);
        testExamRecord.setTotalScore(100);
        testExamRecord.setIsCompleted(0);
        testExamRecord.setCreatedAt(LocalDateTime.now());
        testExamRecord.setUpdatedAt(LocalDateTime.now());

        testSubject = new Subject();
        testSubject.setId(1L);
        testSubject.setName("数学");
        testSubject.setCode("MATH");
    }

    // ==================== getPapers Tests ====================

    @Test
    void getPapers_Success() {
        // Given
        Page<Paper> pageParam = new Page<>(1, 10);
        Page<Paper> resultPage = new Page<>();
        resultPage.setRecords(Arrays.asList(testPaper));
        resultPage.setTotal(1);
        resultPage.setCurrent(1);
        resultPage.setSize(10);

        when(paperMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);
        when(subjectMapper.selectById(1L)).thenReturn(testSubject);

        // When
        PageDTO<PaperVO> result = paperService.getPapers(1L, 1, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("期末数学试卷", result.getList().get(0).getName());
        assertEquals("数学", result.getList().get(0).getSubjectName());
    }

    @Test
    void getPapers_WithoutFilters() {
        // Given
        Page<Paper> resultPage = new Page<>();
        resultPage.setRecords(Arrays.asList(testPaper));
        resultPage.setTotal(1);
        resultPage.setCurrent(1);
        resultPage.setSize(10);

        when(paperMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);
        when(subjectMapper.selectById(1L)).thenReturn(testSubject);

        // When
        PageDTO<PaperVO> result = paperService.getPapers(null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void getPapers_EmptyList() {
        // Given
        Page<Paper> resultPage = new Page<>();
        resultPage.setRecords(Collections.emptyList());
        resultPage.setTotal(0);
        resultPage.setCurrent(1);
        resultPage.setSize(10);

        when(paperMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<PaperVO> result = paperService.getPapers(1L, 1, 1, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    // ==================== getPaperById Tests ====================

    @Test
    void getPaperById_Success() {
        // Given
        when(paperMapper.selectById(1L)).thenReturn(testPaper);
        when(subjectMapper.selectById(1L)).thenReturn(testSubject);

        // When
        PaperVO result = paperService.getPaperById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getPaperId());
        assertEquals("期末数学试卷", result.getName());
        assertEquals(100, result.getTotalScore());
    }

    @Test
    void getPaperById_NotFound_ThrowsException() {
        // Given
        when(paperMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paperService.getPaperById(999L);
        });
        assertEquals("试卷不存在", exception.getMessage());
    }

    // ==================== getPaperQuestions Tests ====================

    @Test
    void getPaperQuestions_Success() {
        // Given
        PaperQuestion pq = new PaperQuestion();
        pq.setPaperId(1L);
        pq.setQuestionId(1L);
        pq.setOrderNum(1);
        pq.setScore(5);

        when(paperQuestionMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(pq));
        when(questionMapper.selectBatchIds(anyList())).thenReturn(Arrays.asList(testQuestion));

        // When
        List<Question> result = paperService.getPaperQuestions(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("什么是函数？", result.get(0).getContent());
    }

    @Test
    void getPaperQuestions_EmptyList() {
        // Given
        when(paperQuestionMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // When
        List<Question> result = paperService.getPaperQuestions(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== createPaper Tests ====================

    @Test
    void createPaper_Success() {
        // Given
        List<Long> questionIds = Arrays.asList(1L, 2L);

        Question q1 = new Question();
        q1.setId(1L);
        q1.setScore(5);
        q1.setDifficulty(3);
        q1.setUsageCount(0);

        Question q2 = new Question();
        q2.setId(2L);
        q2.setScore(10);
        q2.setDifficulty(4);
        q2.setUsageCount(0);

        when(questionMapper.selectBatchIds(questionIds)).thenReturn(Arrays.asList(q1, q2));
        when(questionMapper.selectById(1L)).thenReturn(q1);
        when(questionMapper.selectById(2L)).thenReturn(q2);

        doAnswer(invocation -> {
            Paper paper = invocation.getArgument(0);
            paper.setId(1L);
            return null;
        }).when(paperMapper).insert(any(Paper.class));

        // When
        Long result = paperService.createPaper(1L, "自定义试卷", 1L, 1, questionIds, "自定义描述");

        // Then
        assertNotNull(result);
        assertEquals(1L, result);
        verify(paperMapper, times(1)).insert(any(Paper.class));
        verify(paperQuestionMapper, times(2)).insert(any(PaperQuestion.class));
    }

    // ==================== generatePaper Tests ====================

    @Test
    void generatePaper_Success() {
        // Given
        Map<Integer, Integer> distribution = new HashMap<>();
        distribution.put(1, 5); // 5道选择题

        List<Question> questions = Arrays.asList(testQuestion);
        when(questionMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(questions);

        Question q = new Question();
        q.setId(1L);
        q.setScore(5);
        q.setDifficulty(3);
        q.setUsageCount(0);

        when(questionMapper.selectBatchIds(anyList())).thenReturn(Arrays.asList(q));
        when(questionMapper.selectById(1L)).thenReturn(q);

        doAnswer(invocation -> {
            Paper paper = invocation.getArgument(0);
            paper.setId(2L);
            return null;
        }).when(paperMapper).insert(any(Paper.class));

        // When
        Long result = paperService.generatePaper(1L, 1L, 1, 100, distribution);

        // Then
        assertNotNull(result);
        assertEquals(2L, result);
    }

    // ==================== startExam Tests ====================

    @Test
    void startExam_Success() {
        // Given
        when(paperMapper.selectById(1L)).thenReturn(testPaper);

        doAnswer(invocation -> {
            ExamRecord record = invocation.getArgument(0);
            record.setId(1L);
            return null;
        }).when(examRecordMapper).insert(any(ExamRecord.class));

        // When
        Long result = paperService.startExam(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result);
        verify(examRecordMapper, times(1)).insert(any(ExamRecord.class));
    }

    @Test
    void startExam_PaperNotFound_ThrowsException() {
        // Given
        when(paperMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paperService.startExam(1L, 999L);
        });
        assertEquals("试卷不存在", exception.getMessage());
    }

    // ==================== submitExam Tests ====================

    @Test
    void submitExam_AllCorrect() {
        // Given
        testExamRecord.setIsCompleted(0);
        when(examRecordMapper.selectById(1L)).thenReturn(testExamRecord);
        when(paperMapper.selectById(1L)).thenReturn(testPaper);

        PaperQuestion pq = new PaperQuestion();
        pq.setQuestionId(1L);
        pq.setScore(5);
        when(paperQuestionMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(pq));
        when(questionMapper.selectBatchIds(anyList())).thenReturn(Arrays.asList(testQuestion));

        // Correct answer
        String answersJson = "{\"1\":\"A\"}";

        // When
        ExamResultVO result = paperService.submitExam(1L, 1L, answersJson, 60);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getRecordId());
        assertEquals(5, result.getObtainedScore());
        assertEquals(1, result.getCorrectCount());
        assertEquals(0, result.getWrongCount());
        verify(examRecordMapper, times(1)).updateById(any(ExamRecord.class));
        verify(questionMapper, times(1)).updateById(any(Question.class));
    }

    @Test
    void submitExam_WithWrongAnswers() {
        // Given
        testExamRecord.setIsCompleted(0);
        when(examRecordMapper.selectById(1L)).thenReturn(testExamRecord);
        when(paperMapper.selectById(1L)).thenReturn(testPaper);

        PaperQuestion pq = new PaperQuestion();
        pq.setQuestionId(1L);
        pq.setScore(5);
        when(paperQuestionMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(pq));
        when(questionMapper.selectBatchIds(anyList())).thenReturn(Arrays.asList(testQuestion));

        // Wrong answer
        String answersJson = "{\"1\":\"B\"}"; // Correct is A

        // When
        ExamResultVO result = paperService.submitExam(1L, 1L, answersJson, 60);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getRecordId());
        assertEquals(0, result.getObtainedScore());
        assertEquals(0, result.getCorrectCount());
        assertEquals(1, result.getWrongCount());
        assertTrue(result.getWrongQuestionIds().contains(1L));
        verify(examRecordMapper, times(1)).updateById(any(ExamRecord.class));
        verify(wrongQuestionMapper, times(1)).insert(any(WrongQuestion.class));
    }

    @Test
    void submitExam_RecordNotFound_ThrowsException() {
        // Given
        when(examRecordMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paperService.submitExam(1L, 999L, "{}", 60);
        });
        assertEquals("考试记录不存在或无权限", exception.getMessage());
    }

    @Test
    void submitExam_NoPermission_ThrowsException() {
        // Given
        when(examRecordMapper.selectById(1L)).thenReturn(testExamRecord);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paperService.submitExam(999L, 1L, "{}", 60); // Different user
        });
        assertEquals("考试记录不存在或无权限", exception.getMessage());
    }

    // ==================== getExamRecords Tests ====================

    @Test
    void getExamRecords_Success() {
        // Given
        when(examRecordMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(testExamRecord));

        // When
        List<ExamRecord> result = paperService.getExamRecords(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPaperId());
    }

    @Test
    void getExamRecords_EmptyList() {
        // Given
        when(examRecordMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // When
        List<ExamRecord> result = paperService.getExamRecords(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== toPaperVO Tests ====================

    @Test
    void toPaperVO_WithSubject() {
        // Given
        when(subjectMapper.selectById(1L)).thenReturn(testSubject);

        // When
        PaperVO result = paperService.toPaperVO(testPaper);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getPaperId());
        assertEquals("期末数学试卷", result.getName());
        assertEquals("数学", result.getSubjectName());
        assertEquals(100, result.getTotalScore());
        assertEquals(20, result.getQuestionCount());
        assertEquals(3, result.getDifficultyAvg()); // Converted from BigDecimal
    }

    @Test
    void toPaperVO_WithoutSubject() {
        // Given
        testPaper.setSubjectId(999L);
        when(subjectMapper.selectById(999L)).thenReturn(null);

        // When
        PaperVO result = paperService.toPaperVO(testPaper);

        // Then
        assertNotNull(result);
        assertNull(result.getSubjectName());
    }

    @Test
    void toPaperVO_NullDifficultyAvg() {
        // Given
        testPaper.setDifficultyAvg(null);
        when(subjectMapper.selectById(1L)).thenReturn(testSubject);

        // When
        PaperVO result = paperService.toPaperVO(testPaper);

        // Then
        assertNotNull(result);
        assertNull(result.getDifficultyAvg());
    }

    @Test
    void toPaperVO_NullCreatedAt() {
        // Given
        testPaper.setCreatedAt(null);
        when(subjectMapper.selectById(1L)).thenReturn(testSubject);

        // When
        PaperVO result = paperService.toPaperVO(testPaper);

        // Then
        assertNotNull(result);
        assertNull(result.getCreatedAt());
    }
}
