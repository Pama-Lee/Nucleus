package cn.devspace.nucleus.Manager.Annotation;

import cn.devspace.nucleus.Manager.MyInterfaceScanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({MyInterfaceScanRegistrar.class})
public @interface InterfaceScan {
    String[] value() default {};
}