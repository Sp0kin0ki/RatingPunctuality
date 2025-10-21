package com.rating.punctuality.rating_punctuality.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancellationsDistribution {
    private String airline;
    private int cancellations;
}
