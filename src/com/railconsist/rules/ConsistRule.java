package com.railconsist.rules;

import com.railconsist.model.TrainConsist;
import java.util.List;

/**
 * Functional interface for individual consist safety and marshalling validation rules.
 */
public interface ConsistRule {
    String getRuleCode();
    String getRuleName();
    List<RuleViolation> evaluate(TrainConsist consist);
}
