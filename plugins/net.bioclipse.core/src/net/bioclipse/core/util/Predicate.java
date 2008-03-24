package net.bioclipse.core.util;

import net.bioclipse.core.util.Function;

public interface Predicate<T> extends Function<Boolean, T> {
    public Boolean eval(T arg);
}
