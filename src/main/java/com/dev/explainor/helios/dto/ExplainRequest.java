package com.dev.explainor.helios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExplainRequest {
    
    @NotBlank(message = "Prompt cannot be empty")
    private String prompt;

}
