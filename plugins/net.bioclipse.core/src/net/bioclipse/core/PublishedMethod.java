package net.bioclipse.core;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PublishedMethod {

    String params() default "";
    String methodSummary();
}
