package io.github.vitorcezli.idempotence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    String[] include() default "";
    String[] exclude() default "";
    String hash() default "";
    int ttl() default -1;
}
