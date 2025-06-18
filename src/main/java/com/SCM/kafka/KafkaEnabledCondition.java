package com.SCM.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class KafkaEnabledCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        boolean kafkaEnabled = env.getProperty("kafka.enabled", Boolean.class, false);
        return kafkaEnabled ? ConditionOutcome.match() : ConditionOutcome.noMatch("Kafka is disabled");
    }
}


