package org.zero.common.core.util.java.reflect;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/10
 */
public class ShadowLambdaMeta implements LambdaMeta {
    private final SerializedLambda lambda;

    public ShadowLambdaMeta(Serializable serializable) {
        this.lambda = SerializedLambda.of(serializable);
    }

    @Override
    public String getImplMethodName() {
        return lambda.getImplMethodName();
    }

    @Override
    public String getImplClassName() {
        return lambda.getImplClass().replace("/", ".");
    }

    @SneakyThrows
    @Override
    public Class<?> getImplClass() {
        String implClassName = this.getImplClassName();
        return ClassLoaderUtil.loadClassOpt(implClassName, lambda.getCapturingClass().getClassLoader()).orElseThrow(() -> new ClassNotFoundException(implClassName));
    }

    @Override
    public String getInstantiatedClassName() {
        String instantiatedMethodType = lambda.getInstantiatedMethodType();
        String instantiatedType = instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(";"));
        return instantiatedType.replace("/", ".");
    }

    @SneakyThrows
    @Override
    public Class<?> getInstantiatedClass() {
        String instantiatedClassName = this.getInstantiatedClassName();
        return ClassLoaderUtil.loadClassOpt(instantiatedClassName, lambda.getCapturingClass().getClassLoader()).orElseThrow(() -> new ClassNotFoundException(instantiatedClassName));
    }

    @Override
    public String toString() {
        return String.format("%s::%s", this.getInstantiatedClassName(), this.getImplMethodName());
    }

    /**
     * 作为 {@link java.lang.invoke.SerializedLambda} 的镜像类
     *
     * @author Zero (cnzeropro@163.com)
     * @since 2025/3/10
     */
    @Getter
    public static class SerializedLambda implements Serializable {
        /**
         * 保证序列化版本号相同
         */
        private static final long serialVersionUID = 8025925345765570181L;

        /**
         * The capturing class.
         */
        private Class<?> capturingClass;
        /**
         * The functional interface class.
         */
        private String functionalInterfaceClass;
        /**
         * The functional interface targetMethod name.
         */
        private String functionalInterfaceMethodName;
        /**
         * The functional interface targetMethod signature.
         */
        private String functionalInterfaceMethodSignature;
        /**
         * The implementation class.
         */
        private String implClass;
        /**
         * The implementation targetMethod name.
         */
        private String implMethodName;
        /**
         * The implementation targetMethod signature.
         */
        private String implMethodSignature;
        /**
         * The implementation targetMethod kind.
         */
        private int implMethodKind;
        /**
         * The instantiated targetMethod type.
         */
        private String instantiatedMethodType;
        /**
         * The captured arguments.
         */
        @SuppressWarnings("serial") // Not statically typed as Serializable
        private Object[] capturedArgs;

        @SneakyThrows
        public static SerializedLambda of(Serializable serializable) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(serializable);
                objectOutputStream.flush();
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())) {
                    @Override
                    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                        Class<?> clazz = super.resolveClass(desc);
                        // 将 java.lang.invoke.SerializedLambda 当作本类进行处理
                        return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
                    }
                }) {
                    // 反序列化
                    return (SerializedLambda) objectInputStream.readObject();
                }
            }
        }
    }
}
