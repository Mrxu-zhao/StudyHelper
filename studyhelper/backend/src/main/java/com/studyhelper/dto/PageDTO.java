package com.studyhelper.dto;

import lombok.Data;

@Data
public class PageDTO<T> {
    private java.util.List<T> list;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    
    public static <T> PageDTO<T> of(java.util.List<T> list, Long total, Integer page, Integer pageSize) {
        PageDTO<T> dto = new PageDTO<>();
        dto.setList(list);
        dto.setTotal(total);
        dto.setPage(page);
        dto.setPageSize(pageSize);
        dto.setTotalPages((int) Math.ceil((double) total / pageSize));
        return dto;
    }
}
