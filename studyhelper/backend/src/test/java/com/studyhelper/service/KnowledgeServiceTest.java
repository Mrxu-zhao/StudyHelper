package com.studyhelper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyhelper.dto.ChapterTreeVO;
import com.studyhelper.dto.KnowledgePointVO;
import com.studyhelper.dto.PageDTO;
import com.studyhelper.dto.SubjectVO;
import com.studyhelper.entity.Chapter;
import com.studyhelper.entity.KnowledgePoint;
import com.studyhelper.entity.Subject;
import com.studyhelper.mapper.ChapterMapper;
import com.studyhelper.mapper.KnowledgePointMapper;
import com.studyhelper.mapper.SubjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private ChapterMapper chapterMapper;

    @Mock
    private KnowledgePointMapper knowledgePointMapper;

    @InjectMocks
    private KnowledgeService knowledgeService;

    private Subject testSubject;
    private Chapter testChapter;
    private KnowledgePoint testKnowledgePoint;

    @BeforeEach
    void setUp() {
        testSubject = new Subject();
        testSubject.setId(1L);
        testSubject.setName("数学");
        testSubject.setCode("MATH");
        testSubject.setGradeLevel(1);
        testSubject.setIconUrl("http://example.com/math.png");
        testSubject.setIsActive(1);
        testSubject.setSortOrder(1);

        testChapter = new Chapter();
        testChapter.setId(1L);
        testChapter.setSubjectId(1L);
        testChapter.setName("第一章 函数");
        testChapter.setParentId(0L);
        testChapter.setLevel(1);
        testChapter.setOrderNum(1);
        testChapter.setKnowledgeCount(10);

        testKnowledgePoint = new KnowledgePoint();
        testKnowledgePoint.setId(1L);
        testKnowledgePoint.setChapterId(1L);
        testKnowledgePoint.setTitle("函数的基本概念");
        testKnowledgePoint.setContent("函数是数学中的基本概念...");
        testKnowledgePoint.setImportance(5);
        testKnowledgePoint.setDifficulty(3);
        testKnowledgePoint.setFrequency("high");
        testKnowledgePoint.setTags("函数,基础");
        testKnowledgePoint.setViewCount(100);
        testKnowledgePoint.setIsActive(1);
    }

    // ==================== getSubjects Tests ====================

    @Test
    void getSubjects_Success_WithGrade() {
        // Given
        List<Subject> subjects = Arrays.asList(testSubject);
        when(subjectMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(subjects);
        when(chapterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);
        when(knowledgePointMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(50L);

        // When
        List<SubjectVO> result = knowledgeService.getSubjects(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("数学", result.get(0).getSubjectName());
        assertEquals("MATH", result.get(0).getSubjectCode());
        assertEquals(5, result.get(0).getChapterCount());
        assertEquals(50, result.get(0).getKnowledgeCount());
    }

    @Test
    void getSubjects_Success_WithoutGrade() {
        // Given
        List<Subject> subjects = Arrays.asList(testSubject);
        when(subjectMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(subjects);
        when(chapterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);
        when(knowledgePointMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(30L);

        // When
        List<SubjectVO> result = knowledgeService.getSubjects(null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSubjects_EmptyList() {
        // Given
        when(subjectMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        // When
        List<SubjectVO> result = knowledgeService.getSubjects(1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getChapterTree Tests ====================

    @Test
    void getChapterTree_Success_SingleLevel() {
        // Given - root chapters have parentId = 0L
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setSubjectId(1L);
        chapter.setName("第一章 函数");
        chapter.setParentId(0L);
        chapter.setLevel(1);
        chapter.setOrderNum(1);
        chapter.setKnowledgeCount(10);

        List<Chapter> chapters = Collections.singletonList(chapter);
        when(chapterMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(chapters);

        // When
        List<ChapterTreeVO> result = knowledgeService.getChapterTree(1L, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("第一章 函数", result.get(0).getChapterName());
        assertEquals(1, result.get(0).getLevel());
        assertEquals(10, result.get(0).getKnowledgeCount());
        assertTrue(result.get(0).getChildren().isEmpty());
    }

    @Test
    void getChapterTree_Success_MultiLevel() {
        // Given - root chapters have parentId = 0L
        Chapter parentChapter = new Chapter();
        parentChapter.setId(1L);
        parentChapter.setSubjectId(1L);
        parentChapter.setName("第一章 函数");
        parentChapter.setParentId(0L);
        parentChapter.setLevel(1);
        parentChapter.setOrderNum(1);
        parentChapter.setKnowledgeCount(10);

        Chapter childChapter = new Chapter();
        childChapter.setId(2L);
        childChapter.setSubjectId(1L);
        childChapter.setName("1.1 函数的概念");
        childChapter.setParentId(1L);
        childChapter.setLevel(2);
        childChapter.setOrderNum(1);
        childChapter.setKnowledgeCount(5);

        List<Chapter> chapters = Arrays.asList(parentChapter, childChapter);
        when(chapterMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(chapters);

        // When
        List<ChapterTreeVO> result = knowledgeService.getChapterTree(1L, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("第一章 函数", result.get(0).getChapterName());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("1.1 函数的概念", result.get(0).getChildren().get(0).getChapterName());
    }

    @Test
    void getChapterTree_EmptyList() {
        // Given
        when(chapterMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        // When
        List<ChapterTreeVO> result = knowledgeService.getChapterTree(1L, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getKnowledgePoints Tests ====================

    @Test
    void getKnowledgePoints_Success_WithChapterId() {
        // Given
        Page<KnowledgePoint> resultPage = new Page<>();
        resultPage.setRecords(Arrays.asList(testKnowledgePoint));
        resultPage.setTotal(1);
        resultPage.setCurrent(1);
        resultPage.setSize(10);

        when(knowledgePointMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<KnowledgePointVO> result = knowledgeService.getKnowledgePoints(1L, null, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("函数的基本概念", result.getList().get(0).getTitle());
        assertEquals("高频", result.getList().get(0).getExamFrequency());
    }

    @Test
    void getKnowledgePoints_Success_WithSubjectId() {
        // Given
        Page<KnowledgePoint> resultPage = new Page<>();
        resultPage.setRecords(Arrays.asList(testKnowledgePoint));
        resultPage.setTotal(1);
        resultPage.setCurrent(1);
        resultPage.setSize(10);

        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setSubjectId(1L);

        when(chapterMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(chapter));
        when(knowledgePointMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<KnowledgePointVO> result = knowledgeService.getKnowledgePoints(null, 1L, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void getKnowledgePoints_Success_NullPagination() {
        // Given
        Page<KnowledgePoint> resultPage = new Page<>();
        resultPage.setRecords(Arrays.asList(testKnowledgePoint));
        resultPage.setTotal(1);
        resultPage.setCurrent(1);
        resultPage.setSize(10);

        when(knowledgePointMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<KnowledgePointVO> result = knowledgeService.getKnowledgePoints(1L, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void getKnowledgePoints_EmptyList() {
        // Given
        Page<KnowledgePoint> resultPage = new Page<>();
        resultPage.setRecords(Collections.emptyList());
        resultPage.setTotal(0);
        resultPage.setCurrent(1);
        resultPage.setSize(10);

        when(knowledgePointMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<KnowledgePointVO> result = knowledgeService.getKnowledgePoints(1L, null, 1, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
        assertEquals(0, result.getTotal());
    }

    // ==================== incrementViewCount Tests ====================

    @Test
    void incrementViewCount_Success() {
        // Given
        when(knowledgePointMapper.selectById(1L)).thenReturn(testKnowledgePoint);

        // When
        knowledgeService.incrementViewCount(1L);

        // Then
        verify(knowledgePointMapper, times(1)).updateById(any(KnowledgePoint.class));
    }

    @Test
    void incrementViewCount_NotFound() {
        // Given
        when(knowledgePointMapper.selectById(999L)).thenReturn(null);

        // When
        knowledgeService.incrementViewCount(999L);

        // Then
        verify(knowledgePointMapper, never()).updateById(any());
    }

    // ==================== Frequency Text Tests ====================

    @Test
    void getFrequencyText_High() {
        // Given
        Page<KnowledgePoint> resultPage = new Page<>();
        testKnowledgePoint.setFrequency("high");
        resultPage.setRecords(Arrays.asList(testKnowledgePoint));
        resultPage.setTotal(1);

        when(knowledgePointMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<KnowledgePointVO> result = knowledgeService.getKnowledgePoints(1L, null, 1, 10);

        // Then
        assertEquals("高频", result.getList().get(0).getExamFrequency());
    }

    @Test
    void getFrequencyText_Low() {
        // Given
        Page<KnowledgePoint> resultPage = new Page<>();
        testKnowledgePoint.setFrequency("low");
        resultPage.setRecords(Arrays.asList(testKnowledgePoint));
        resultPage.setTotal(1);

        when(knowledgePointMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<KnowledgePointVO> result = knowledgeService.getKnowledgePoints(1L, null, 1, 10);

        // Then
        assertEquals("低频", result.getList().get(0).getExamFrequency());
    }

    @Test
    void getFrequencyText_Null() {
        // Given
        Page<KnowledgePoint> resultPage = new Page<>();
        testKnowledgePoint.setFrequency(null);
        resultPage.setRecords(Arrays.asList(testKnowledgePoint));
        resultPage.setTotal(1);

        when(knowledgePointMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(resultPage);

        // When
        PageDTO<KnowledgePointVO> result = knowledgeService.getKnowledgePoints(1L, null, 1, 10);

        // Then
        assertEquals("中频", result.getList().get(0).getExamFrequency());
    }
}
