package net.bioclipse.core;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Recorded {
    
//    String documentation() default "This method has no documentation.";
}
