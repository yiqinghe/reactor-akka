package monoServer.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.FIELD})
@Documented
public @interface AkkaFeignClient {
    public String value() default "";
}
