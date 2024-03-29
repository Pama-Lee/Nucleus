package cn.devspace.nucleus.Manager.Annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//路由注解
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Router {
    @AliasFor(attribute = "URI")
    String value() default "";

    @AliasFor(attribute = "value")
    String URI() default "";

    boolean isUpload() default false;
}
