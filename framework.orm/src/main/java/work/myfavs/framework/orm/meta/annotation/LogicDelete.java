package work.myfavs.framework.orm.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 逻辑删除字段标记
 * 逻辑删除字段需与主键字段类型、长度一致
 * 在执行删除操作时，会把逻辑删除标记字段的值更新与主键一致
 *
 * <p>Created by tanqimin on 2023/11/01.
 */
@java.lang.annotation.Inherited
@java.lang.annotation.Target({ElementType.FIELD})
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface LogicDelete {}
