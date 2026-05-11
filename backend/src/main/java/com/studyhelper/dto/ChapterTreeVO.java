package com.studyhelper.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChapterTreeVO {
    private Long chapterId;
    private String chapterName;
    private Integer level;
    private Long parentId;
    private Integer knowledgeCount;
    private List<ChapterTreeVO> children;
}
