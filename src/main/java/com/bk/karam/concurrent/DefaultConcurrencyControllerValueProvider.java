package com.bk.karam.concurrent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @autor
 */
@Component
public class DefaultConcurrencyControllerValueProvider implements IConcurrencyControlValueProvider {
    @Override
    public Serializable provide(ConcurrencyControlValueGenerationEvent event) {
        return ToStringBuilder.reflectionToString(event.getArgs(), ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
