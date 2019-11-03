package com.bk.karam.lock.cachebase;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @autor
 */
public class CacheEntryWrapper<T extends Serializable, K extends Serializable> {

    private final T target;

    private final Function<T, Object> transform;

    private final Function<T, Integer> versionResolver;

    CacheEntryWrapper(T target, Function<T, Object> transform, Function<T, Integer> versionResolver) {
        this.target = target;
        this.transform = transform;
        this.versionResolver = versionResolver;
    }

    @SuppressWarnings("unchecked")
    public K get(K defaultValue) {
        K value = null;
        if (null != target) {
            value = (K) transform.apply(target);
        }
        if (null == value) {
            value = defaultValue;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public K get() {
        K value = null;
        if (null != target) {
            value = (K) transform.apply(target);
        }
        return value;
    }

    public int getVersion() {
        return Optional.fromNullable(versionResolver.apply(target)).or(0);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("target", target)
                .toString();
    }
}
