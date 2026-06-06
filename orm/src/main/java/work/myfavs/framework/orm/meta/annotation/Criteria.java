package work.myfavs.framework.orm.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Inherited
@java.lang.annotation.Target({ElementType.FIELD})
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface Criteria {

  Criterion[] value();
}
