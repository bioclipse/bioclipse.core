package net.bioclipse.core.util;

public interface Function<S, T> {
    public S eval(T arg);
}
