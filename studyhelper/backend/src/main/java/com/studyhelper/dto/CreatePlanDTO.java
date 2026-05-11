package com.studyhelper.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePlanDTO {
    @NotBlank(message = "计划名称不能为空")
    private String name;
    
    private Integer type;
    private LocalDate targetDate;
    private String description;
    private List<TaskDTO> tasks;
}
