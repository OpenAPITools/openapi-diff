package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.DiffContext;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
public final class CacheKey {
    private final String left;
    private final String right;
    private final DiffContext context;

    public CacheKey(final String left, final String right, final DiffContext context) {
        this.left = left;
        this.right = right;
        this.context = context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CacheKey cacheKey = (CacheKey) o;

        return new EqualsBuilder()
                .append(left, cacheKey.left)
                .append(right, cacheKey.right)
                .append(context, cacheKey.context)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(left)
                .append(right)
                .append(context)
                .toHashCode();
    }
}
