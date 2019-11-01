package com.bk.karam.factory.cache.annotation;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class HashCodeCacheKeyProvider implements ICacheKeyProvider {

    @Override
    public Serializable generate(CacheKeyGenerationEvent event) {
        return new HashCodeBuilder()
                .append(event.getArgs())
                .toHashCode();
    }
}
