package com.bk.karam.concurrent;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @autor
 */
@Component
public class DefaultHashCodeConcurrencyControlKeyProvider implements IConcurrencyControlKeyProvider {

    @Override
    public Serializable provide(ConcurrencyControlKeyGenerationEvent event) {
        return new HashCodeBuilder()
                .append(event.getArgs())
                .toHashCode();
    }
}
