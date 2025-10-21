package com.rating.punctuality.rating_punctuality.model.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelayStats {
    private String delayCategory;
    private int count;
    private int avgDelaySeconds;
}
