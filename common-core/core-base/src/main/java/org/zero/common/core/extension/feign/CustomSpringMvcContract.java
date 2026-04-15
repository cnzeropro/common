package org.zero.common.core.extension.feign;

import feign.Body;
import feign.HeaderMap;
import feign.Headers;
import feign.MethodMetadata;
import feign.Param;
import feign.QueryMap;
import feign.Request;
import feign.RequestLine;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SpringCloud OpenFeign Contract（{@link SpringMvcContract}）只支持 SpringMVC 注解和少量 Feign 原生注解，
 * 而 Feign 默认的 Contract（{@link feign.Contract.Default}）只支持 Feign 原生注解，
 * 因此需要自定义 Contract 用于支持两种实现（也就是各自的注解都支持）。
 *
 * @author zero
 * @see SpringMvcContract
 * @see feign.Contract.Default
 * @since 2024/3/8
 */
public class CustomSpringMvcContract extends SpringMvcContract implements EnvironmentAware, InitializingBean {
    protected final List<GuardedAnnotationProcessor> classAnnotationProcessors = new ArrayList<>();
    protected final List<GuardedAnnotationProcessor> methodAnnotationProcessors = new ArrayList<>();
    protected final Map<Class<Annotation>, ParameterAnnotationProcessor<Annotation>> parameterAnnotationProcessors = new HashMap<>();

    protected Environment environment;

    @Override
    protected void processAnnotationOnClass(MethodMetadata data, Class<?> clz) {
        // 先支持 Feign 的注解，再支持 SpringMvc 的注解，以后者为主，防止前者覆盖后者
        this.supportFeignAnnotationOnClass(data, clz);
        super.processAnnotationOnClass(data, clz);
    }

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
        this.supportFeignAnnotationOnMethod(data, methodAnnotation, method);
        super.processAnnotationOnMethod(data, methodAnnotation, method);
    }


    @Override
    protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations, int paramIndex) {
        return this.supportFeignAnnotationOnParameter(data, annotations, paramIndex) && super.processAnnotationsOnParameter(data, annotations, paramIndex);
    }


    protected String resolve(String value) {
        return StringUtils.hasText(value) ? environment.resolvePlaceholders(value) : value;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }

    /* ***************************************** Copy from feign.Contract.Default ***************************************** */

    protected static final Pattern REQUEST_LINE_PATTERN = Pattern.compile("^([A-Z]+)[ ]*(.*)$");

    protected void init() {
        this.registerClassAnnotation(Headers.class, (header, data) -> {
            final String[] headersOnType = header.value();
            feign.Util.checkState(headersOnType.length > 0, "Headers annotation was empty on type %s.", data.configKey());
            String[] resolvedHeadersOnType = Arrays.stream(headersOnType).map(this::resolve).toArray(String[]::new);
            final Map<String, Collection<String>> headers = toMap(resolvedHeadersOnType);
            headers.putAll(data.template().headers());
            data.template().headers(null); // to clear
            data.template().headers(headers);
        });
        this.registerMethodAnnotation(RequestLine.class, (ann, data) -> {
            final String requestLine = ann.value();
            feign.Util.checkState(feign.Util.emptyToNull(requestLine) != null, "RequestLine annotation was empty on targetMethod %s.", data.configKey());
            final Matcher requestLineMatcher = REQUEST_LINE_PATTERN.matcher(requestLine);
            if (!requestLineMatcher.find()) {
                throw new IllegalStateException(String.format("RequestLine annotation didn't start with an HTTP verb on targetMethod %s", data.configKey()));
            } else {
                String method = requestLineMatcher.group(1);
                String resolvedMethod = this.resolve(method);
                data.template().method(Request.HttpMethod.valueOf(resolvedMethod));
                String uri = requestLineMatcher.group(2);
                String resolvedUri = this.resolve(uri);
                data.template().uri(resolvedUri);
            }
            data.template().decodeSlash(ann.decodeSlash());
            data.template().collectionFormat(ann.collectionFormat());
        });
        this.registerMethodAnnotation(Body.class, (ann, data) -> {
            final String body = ann.value();
            feign.Util.checkState(feign.Util.emptyToNull(body) != null, "Body annotation was empty on targetMethod %s.", data.configKey());
            String resolvedBody = this.resolve(body);
            if (resolvedBody.indexOf('{') == -1) {
                data.template().body(resolvedBody);
            } else {
                data.template().bodyTemplate(resolvedBody);
            }
        });
        this.registerMethodAnnotation(Headers.class, (header, data) -> {
            final String[] headersOnMethod = header.value();
            feign.Util.checkState(headersOnMethod.length > 0, "Headers annotation was empty on targetMethod %s.", data.configKey());
            String[] resolvedHeadersOnMethod = Arrays.stream(headersOnMethod).map(this::resolve).toArray(String[]::new);
            data.template().headers(toMap(resolvedHeadersOnMethod));
        });
        this.registerParameterAnnotation(Param.class, (paramAnnotation, data, paramIndex) -> {
            final String annotationName = paramAnnotation.value();
            final Parameter parameter = data.method().getParameters()[paramIndex];
            final String name;
            if (feign.Util.emptyToNull(annotationName) == null && parameter.isNamePresent()) {
                name = parameter.getName();
            } else {
                name = annotationName;
            }
            feign.Util.checkState(feign.Util.emptyToNull(name) != null, "Param annotation was empty on param %s.", paramIndex);
            nameParam(data, name, paramIndex);
            final Class<? extends Param.Expander> expander = paramAnnotation.expander();
            if (expander != Param.ToStringExpander.class) {
                data.indexToExpanderClass().put(paramIndex, expander);
            }
            if (!data.template().hasRequestVariable(name)) {
                data.formParams().add(name);
            }
        });
        this.registerParameterAnnotation(QueryMap.class, (queryMap, data, paramIndex) -> {
            feign.Util.checkState(data.queryMapIndex() == null, "QueryMap annotation was present on multiple parameters.");
            data.queryMapIndex(paramIndex);
			data.queryMapEncoded(queryMap.encoded());
        });
        this.registerParameterAnnotation(HeaderMap.class, (queryMap, data, paramIndex) -> {
            feign.Util.checkState(data.headerMapIndex() == null, "HeaderMap annotation was present on multiple parameters.");
            data.headerMapIndex(paramIndex);
        });
    }

    private Map<String, Collection<String>> toMap(String[] input) {
        final Map<String, Collection<String>> result = new LinkedHashMap<>(input.length);
        for (final String header : input) {
            final int colon = header.indexOf(':');
            final String name = header.substring(0, colon);
            result.computeIfAbsent(name, k -> new ArrayList<>(1));
            result.get(name).add(header.substring(colon + 1).trim());
        }
        return result;
    }

    /* ***************************************** Copy from feign.DeclarativeContract ***************************************** */

    protected void supportFeignAnnotationOnClass(MethodMetadata data, Class<?> clz) {
        final List<GuardedAnnotationProcessor> processors = Arrays.stream(clz.getAnnotations())
                .flatMap(annotation -> classAnnotationProcessors.stream()
                        .filter(processor -> processor.test(annotation)))
                .collect(Collectors.toList());

        if (!processors.isEmpty()) {
            Arrays.stream(clz.getAnnotations())
                    .forEach(annotation -> processors.stream()
                            .filter(processor -> processor.test(annotation))
                            .forEach(processor -> processor.process(annotation, data)));
        } else {
            if (clz.getAnnotations().length == 0) {
                data.addWarning(String.format("Class %s has no annotations, it may affect contract %s",
                        clz.getSimpleName(),
                        getClass().getSimpleName()));
            } else {
                data.addWarning(String.format("Class %s has annotations %s that are not used by contract %s",
                        clz.getSimpleName(),
                        Arrays.stream(clz.getAnnotations())
                                .map(annotation -> annotation.annotationType()
                                        .getSimpleName())
                                .collect(Collectors.toList()),
                        getClass().getSimpleName()));
            }
        }
    }

    protected void supportFeignAnnotationOnMethod(MethodMetadata data, Annotation annotation, Method method) {
        List<GuardedAnnotationProcessor> processors = methodAnnotationProcessors.stream()
                .filter(processor -> processor.test(annotation))
                .collect(Collectors.toList());

        if (!processors.isEmpty()) {
            processors.forEach(processor -> processor.process(annotation, data));
        } else {
            data.addWarning(String.format("Method %s has an annotation %s that is not used by contract %s",
                    method.getName(),
                    annotation.annotationType()
                            .getSimpleName(),
                    getClass().getSimpleName()));
        }
    }

    protected boolean supportFeignAnnotationOnParameter(MethodMetadata data, Annotation[] annotations, int paramIndex) {
        List<Annotation> matchingAnnotations = Arrays.stream(annotations)
                .filter(annotation -> parameterAnnotationProcessors.containsKey(annotation.annotationType()))
                .collect(Collectors.toList());

        if (!matchingAnnotations.isEmpty()) {
            matchingAnnotations.forEach(annotation -> parameterAnnotationProcessors.getOrDefault(annotation.annotationType(), ParameterAnnotationProcessor.DO_NOTHING)
                    .process(annotation, data, paramIndex));
        } else {
            final Parameter parameter = data.method().getParameters()[paramIndex];
            String parameterName = parameter.isNamePresent()
                    ? parameter.getName()
                    : parameter.getType().getSimpleName();
            if (annotations.length == 0) {
                data.addWarning(String.format("Parameter %s has no annotations, it may affect contract %s",
                        parameterName,
                        getClass().getSimpleName()));
            } else {
                data.addWarning(String.format("Parameter %s has annotations %s that are not used by contract %s",
                        parameterName,
                        Arrays.stream(annotations)
                                .map(annotation -> annotation.annotationType()
                                        .getSimpleName())
                                .collect(Collectors.toList()),
                        getClass().getSimpleName()));
            }
        }
        return true;
    }

    protected <E extends Annotation> void registerClassAnnotation(Class<E> annotationType, AnnotationProcessor<E> processor) {
        this.registerClassAnnotation(annotation -> annotation.annotationType().equals(annotationType), processor);
    }

    protected <E extends Annotation> void registerClassAnnotation(Predicate<E> predicate, AnnotationProcessor<E> processor) {
        this.classAnnotationProcessors.add(new GuardedAnnotationProcessor(predicate, processor));
    }

    protected <E extends Annotation> void registerMethodAnnotation(Class<E> annotationType, AnnotationProcessor<E> processor) {
        this.registerMethodAnnotation(annotation -> annotation.annotationType().equals(annotationType), processor);
    }

    protected <E extends Annotation> void registerMethodAnnotation(Predicate<E> predicate, AnnotationProcessor<E> processor) {
        this.methodAnnotationProcessors.add(new GuardedAnnotationProcessor(predicate, processor));
    }

    @SuppressWarnings({"unchecked"})
    protected <E extends Annotation> void registerParameterAnnotation(Class<E> annotation, ParameterAnnotationProcessor<E> processor) {
        this.parameterAnnotationProcessors.put((Class<Annotation>) annotation, (ParameterAnnotationProcessor<Annotation>) processor);
    }

    @FunctionalInterface
    protected interface AnnotationProcessor<E extends Annotation> {
        void process(E annotation, MethodMetadata metadata);
    }

    @FunctionalInterface
    protected interface ParameterAnnotationProcessor<E extends Annotation> {
        ParameterAnnotationProcessor<Annotation> DO_NOTHING = (ann, data, i) -> {
        };

        void process(E annotation, MethodMetadata metadata, int paramIndex);
    }

    protected static class GuardedAnnotationProcessor implements Predicate<Annotation>, AnnotationProcessor<Annotation> {
        private final Predicate<Annotation> predicate;
        private final AnnotationProcessor<Annotation> processor;

        @SuppressWarnings({"rawtypes", "unchecked"})
        private GuardedAnnotationProcessor(Predicate predicate, AnnotationProcessor processor) {
            this.predicate = predicate;
            this.processor = processor;
        }

        @Override
        public void process(Annotation annotation, MethodMetadata metadata) {
            processor.process(annotation, metadata);
        }

        @Override
        public boolean test(Annotation t) {
            return predicate.test(t);
        }
    }
}
