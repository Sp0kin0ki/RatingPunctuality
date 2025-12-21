package com.rating.punctuality.rating_punctuality.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelayRuleDto {
    private String rule;
    private Double support;
    private Double confidence;
    private Double lift;
}
