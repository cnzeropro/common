package org.zero.common.core.extension.java.lang;

import org.zero.common.core.extension.java.util.function.ThrowThrowableSupplier;
import org.zero.common.core.util.java.lang.ThrowableUtil;
import org.zero.common.core.util.java.lang.reflect.FieldUtil;
import org.zero.common.core.util.java.lang.reflect.MethodUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

/**
 * 通用构建者
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Data
 * @NoArgsConstructor
 * @AllArgsConstructor
 * public class TestPojo {
 *     private Integer id;
 *     private String name;
 *
 *     public static Builder builder() {
 *         return new Builder();
 *     }
 *
 *     public static class Builder extends BaseBuilder<Builder, TestPojo> {
 *         private Integer id;
 *         private String name;
 *
 *         public Builder id(Integer id) {
 *             this.id = id;
 *             return this;
 *         }
 *
 *         public Builder name(String name) {
 *             this.name = name;
 *             return this;
 *         }
 *     }
 * }
 * }</pre>
 * <pre>{@code
 * TestPojo testPojo = TestPojo.builder().id(1).name("zero").build();
 * }</pre>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public abstract class BaseBuilder<B extends BaseBuilder<B, A>, A> {
    /**
     * 实例化对象
     * <p>
     * 通过当前 {@code Builder} 对象类的 {@code enclosing class}（直接包含类）实例化对象。<br>
     * 实例化对象尝试使用两种方式：<br>
     * 1. 先尝试通过全参构造器实例化对象 <br>
     * 2. 再尝试通过 setter 方法实例化对象
     */
    @SuppressWarnings("unchecked")
    protected A instance() {
        Class<?> clazz = this.getClass();
        Class<?> enclosingClass = clazz.getEnclosingClass();
        if (Objects.isNull(enclosingClass)) {
            throw new RuntimeException(String.format("%s must have enclosing class", clazz));
        }
        Collection<Field> fields = FieldUtil.list(clazz, false);
        return ThrowableUtil.tryReturnNew((ThrowThrowableSupplier<A>) () -> {
                    Class<?>[] parameterTypes = fields.stream()
                            .map(Field::getType)
                            .toArray(Class[]::new);
                    Constructor<?> constructor = enclosingClass.getConstructor(parameterTypes);
                    Object[] initArgs = fields.stream()
                            .map(field -> FieldUtil.getValue(field, this))
                            .toArray(Object[]::new);
                    // 使用全参构造器
                    return (A) constructor.newInstance(initArgs);
                },
                (Function<Throwable, A>) ignored -> ThrowableUtil.sneakyThrow((ThrowThrowableSupplier<A>) () -> {
                            Constructor<?> constructor = enclosingClass.getConstructor();
                            A obj = (A) constructor.newInstance();
                            // 使用 setter 方法
                            fields.forEach(field -> {
                                Object fieldValue = FieldUtil.getValue(field, this);
                                MethodUtil.GetterOrSetterQuerier.create(MethodUtil.GetterOrSetterQuerier.Mode.SETTER)
                                        .targetClass(enclosingClass)
                                        .field(field)
                                        .queryOpt()
                                        .ifPresent(method -> MethodUtil.invoke(method, obj, fieldValue));
                            });
                            return obj;
                        })
                        .get());
    }

    protected void validate(A obj) {
    }

    public A build() {
        A obj = this.instance();
        this.validate(obj);
        return obj;
    }
}
