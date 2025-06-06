package org.zero.common.core.util;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import feign.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.WebApplicationType;
import org.springframework.core.annotation.AnnotationUtils;
import org.zero.common.core.support.context.spring.SpringUtils;
import org.zero.common.core.support.converter.ConverterComposite;
import org.zero.common.core.util.hutool.core.bean.BeanUtil;
import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.lang.ObjectUtil;
import org.zero.common.core.util.java.reflect.FieldUtil;
import org.zero.common.core.util.java.reflect.MethodUtil;
import org.zero.common.core.util.java.util.ListUtil;
import org.zero.common.data.constant.StringPool;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.zero.common.core.util.hutool.core.bean.BeanUtil.DEFAULT_REGEX;
import static org.zero.common.core.util.hutool.core.bean.BeanUtil.DEFAULT_REGEX_TEMPLATE;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/3
 */
public class BeanMap {
    public static final String DEFAULT_NAME = StringPool.DOLLAR;
    public static final String DEFAULT_LEVEL_SEPARATOR = StringPool.DOT;
    public static final String DEFAULT_MULTIVARIABLE_SEPARATOR = StringPool.COMMA;

    protected Collection<ObjectConfig> objectConfigs = new ArrayList<>();
    protected boolean append;

    protected BeanMap() {
    }

    protected BeanMap(ObjectConfig... objectConfigs) {
        Collections.addAll(this.objectConfigs, objectConfigs);
    }

    protected BeanMap(Collection<ObjectConfig> objectConfigs) {
        this.objectConfigs = objectConfigs;
    }

    public static BeanMap of() {
        return new BeanMap();
    }

    public static BeanMap of(Collection<ObjectConfig> objectConfigs) {
        return new BeanMap(objectConfigs);
    }

    public static BeanMap of(ObjectConfig... objectConfigs) {
        return new BeanMap(objectConfigs);
    }

    public BeanMap objectConfig(ObjectConfig objectConfig) {
        this.objectConfigs.add(objectConfig);
        return this;
    }

    public BeanMap objectConfigs(Collection<ObjectConfig> objectConfigs) {
        this.objectConfigs.addAll(objectConfigs);
        return this;
    }

    public BeanMap objectConfigs(ObjectConfig... objectConfigs) {
        Collections.addAll(this.objectConfigs, objectConfigs);
        return this;
    }

    public BeanMap append() {
        return this.append(true);
    }

    public BeanMap append(boolean append) {
        this.append = append;
        return this;
    }

    public Map<String, Object> toMap() {
        return this.toMap(new LinkedHashMap<>());
    }

    public Map<String, Object> toMap(Supplier<Map<String, Object>> mapSupplier) {
        return this.toMap(mapSupplier.get());
    }

    public Map<String, Object> toMap(Map<String, Object> map) {
        for (ObjectConfig objectConfig : objectConfigs) {
            // 组装成树
            ObjectTree root = new ObjectTree(objectConfig);
            this.tree(root);
            System.out.println(root);
        }
        return map;
    }

    protected void tree(ObjectTree objectTree) {
        ObjectConfig objectConfig = objectTree.objectConfig;
        Object object = objectConfig.object;
        // null
        if (Objects.isNull(object)) {
            return;
        }
        // 指定的 bean
        if (BeanUtil.isBean(object, objectConfig.beanJudgmentRegex)) {
            this.treeWithBean(object, objectTree);
            return;
        }
        // map
        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            this.treeWithMap(map, objectTree);
            return;
        }
        // array、iterable、iterator、enumeration
        if (ObjectUtil.isMultivariable(object)) {
            Object[] array = ArrayUtil.of(object);
            this.treeWithArray(array, objectTree);
        }
    }

    protected void treeWithBean(Object bean, ObjectTree objectTree) {
        ObjectConfig objectConfig = objectTree.objectConfig;
        this.createPropertyInfos(bean).forEach(propertyInfo -> {
            String name = this.getPropertyName(propertyInfo, objectConfig.supportFeignAnnotation);
            Object value = this.getPropertyValue(propertyInfo);
            ObjectTree child = objectTree.child(objectConfig.with(value, name), propertyInfo);
            if (this.needDeep(value, objectConfig.beanJudgmentRegex)) {
                this.tree(child);
            }
        });
    }

    protected void treeWithMap(Map<?, ?> map, ObjectTree objectTree) {
        ObjectConfig objectConfig = objectTree.objectConfig;
        map.forEach((k, v) -> {
            ObjectTree child = objectTree.child(objectConfig.with(v, Objects.toString(k)));
            if (this.needDeep(v, objectConfig.beanJudgmentRegex)) {
                this.tree(child);
            }
        });
    }

    protected void treeWithArray(Object[] array, ObjectTree objectTree) {
        ObjectConfig objectConfig = objectTree.objectConfig;
        for (int i = 0; i < array.length; i++) {
            String name = CharSequenceUtil.format("[{}]", i);
            ObjectTree child = objectTree.child(objectConfig.with(array[i], name));
            if (this.needDeep(array[i], objectConfig.beanJudgmentRegex)) {
                this.tree(child);
            }
        }
    }

    protected boolean needDeep(Object object, String judgmentRegex) {
        return BeanUtil.isBean(object, judgmentRegex) || object instanceof Map || ObjectUtil.isMultivariable(object);
    }

    protected boolean needDeep(Class<?> clazz, String judgmentRegex) {
        return ClassUtil.isBean(clazz, judgmentRegex) || Map.class.isAssignableFrom(clazz) || ClassUtil.isMultivariable(clazz);
    }

    protected Collection<PropertyInfo> createPropertyInfos(Object obj) {
        Class<?> clazz = obj.getClass();
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(clazz);
        return Arrays.stream(propertyDescriptors)
                .filter(propertyDescriptor -> !propertyDescriptor.getName().equals("class"))
                .map(propertyDescriptor -> {
                    String name = propertyDescriptor.getName();
                    Field field = FieldUtil.getFieldByName(clazz, name);
                    return new PropertyInfo(obj, field, propertyDescriptor);
                })
                .collect(Collectors.toList());
    }

    protected String getPropertyName(PropertyInfo propertyInfo, boolean supportFeignAnnotation) {
        if (supportFeignAnnotation) {
            Opt<String> paramValueOpt = Opt.ofNullable(propertyInfo.field)
                    .map(field -> AnnotationUtils.findAnnotation(field, Param.class))
                    .or(() -> Opt.ofNullable(propertyInfo.propertyDescriptor)
                            .map(PropertyDescriptor::getReadMethod)
                            .map(method -> AnnotationUtils.findAnnotation(method, Param.class)))
                    .map(Param::value);
            if (paramValueOpt.isPresent()) {
                return paramValueOpt.get();
            }
        }
        return propertyInfo.field.getName();
    }

    protected Object getPropertyValue(PropertyInfo propertyInfo) {
        return Opt.ofNullable(propertyInfo.propertyDescriptor)
                .map(PropertyDescriptor::getReadMethod)
                .map(method -> MethodUtil.invoke(method, propertyInfo.bean))
                .or(() -> Opt.ofNullable(propertyInfo.field)
                        .map(field -> FieldUtil.getFieldValue(field, propertyInfo.bean)))
                .orElse(null);
    }

    // protected String formatNum(Field field, Object numObj) {
    //     if (Objects.isNull(numObj)) {
    //         return null;
    //     }
    //     String pattern = this.getNumberPattern(field);
    //     if (CharSequenceUtil.isBlank(pattern)) {
    //         return numObj.toString();
    //     }
    //     return NumberUtil.decimalFormat(pattern, numObj);
    // }
    //
    // protected String formatDataTime(Field field, Object dataTimeObj) {
    //     if (Objects.isNull(dataTimeObj)) {
    //         return null;
    //     }
    //     if (dataTimeObj instanceof Date) {
    //         String pattern = this.getDatetimePattern(field);
    //         return DateUtil.format((Date) dataTimeObj, pattern);
    //     }
    //     if (dataTimeObj instanceof Calendar) {
    //         String pattern = this.getDatetimePattern(field);
    //         return DateUtil.format(((Calendar) dataTimeObj).getTime(), pattern);
    //     }
    //     if (dataTimeObj instanceof TemporalAccessor) {
    //         String pattern;
    //         if (dataTimeObj instanceof LocalDate) {
    //             pattern = this.getDatePattern(field);
    //         } else if (dataTimeObj instanceof LocalTime) {
    //             pattern = this.getTimePattern(field);
    //         } else {
    //             pattern = this.getDatetimePattern(field);
    //         }
    //         return TemporalAccessorUtil.format((TemporalAccessor) dataTimeObj, pattern);
    //     }
    //     throw new UtilException(String.format("Not datetime type, cannot be formatted: %s", dataTimeObj.getClass()));
    // }

    // protected String getNumberPattern(AnnotatedElement annotatedElement) {
    //     if (CharSequenceUtil.isNotBlank(numberPattern)) {
    //         return numberPattern;
    //     }
    //     if (Objects.nonNull(annotatedElement)) {
    //         if (supportSpringAnnotation) {
    //             NumberFormat numberFormat = AnnotationUtils.findAnnotation(annotatedElement, NumberFormat.class);
    //             if (Objects.nonNull(numberFormat)) {
    //                 return numberFormat.pattern();
    //             }
    //         }
    //     }
    //     return null;
    // }
    //
    // protected String getDatePattern(AnnotatedElement annotatedElement) {
    //     if (CharSequenceUtil.isNotBlank(datePattern)) {
    //         return datePattern;
    //     }
    //     if (Objects.nonNull(annotatedElement)) {
    //         if (supportSpringAnnotation) {
    //             DateTimeFormat dateTimeFormat = AnnotationUtils.findAnnotation(annotatedElement, DateTimeFormat.class);
    //             if (Objects.nonNull(dateTimeFormat)) {
    //                 return dateTimeFormat.pattern();
    //             }
    //         }
    //     }
    //     WebApplicationType webApplicationType = this.getWebApplicationType();
    //     if (WebApplicationType.SERVLET == webApplicationType) {
    //         String pattern = SpringUtils.getProperty("spring.mvc.format.date");
    //         if (CharSequenceUtil.isNotBlank(pattern)) {
    //             return pattern;
    //         }
    //     } else if (WebApplicationType.REACTIVE == webApplicationType) {
    //         String pattern = SpringUtils.getProperty("spring.webflux.format.date");
    //         if (CharSequenceUtil.isNotBlank(pattern)) {
    //             return pattern;
    //         }
    //     }
    //     return DatePattern.NORM_DATE_PATTERN;
    // }
    //
    // protected String getTimePattern(AnnotatedElement annotatedElement) {
    //     if (CharSequenceUtil.isNotBlank(timePattern)) {
    //         return timePattern;
    //     }
    //     if (Objects.nonNull(annotatedElement)) {
    //         if (supportSpringAnnotation) {
    //             DateTimeFormat dateTimeFormat = AnnotationUtils.findAnnotation(annotatedElement, DateTimeFormat.class);
    //             if (Objects.nonNull(dateTimeFormat)) {
    //                 return dateTimeFormat.pattern();
    //             }
    //         }
    //     }
    //     WebApplicationType webApplicationType = this.getWebApplicationType();
    //     if (WebApplicationType.SERVLET == webApplicationType) {
    //         String pattern = SpringUtils.getProperty("spring.mvc.format.time");
    //         if (CharSequenceUtil.isNotBlank(pattern)) {
    //             return pattern;
    //         }
    //     } else if (WebApplicationType.REACTIVE == webApplicationType) {
    //         String pattern = SpringUtils.getProperty("spring.webflux.format.time");
    //         if (CharSequenceUtil.isNotBlank(pattern)) {
    //             return pattern;
    //         }
    //     }
    //     return DatePattern.NORM_TIME_PATTERN;
    // }
    //
    // protected String getDatetimePattern(AnnotatedElement annotatedElement) {
    //     if (CharSequenceUtil.isNotBlank(datetimePattern)) {
    //         return datetimePattern;
    //     }
    //     if (Objects.nonNull(annotatedElement)) {
    //         if (supportSpringAnnotation) {
    //             DateTimeFormat dateTimeFormat = AnnotationUtils.findAnnotation(annotatedElement, DateTimeFormat.class);
    //             if (Objects.nonNull(dateTimeFormat)) {
    //                 return dateTimeFormat.pattern();
    //             }
    //         }
    //     }
    //     WebApplicationType webApplicationType = this.getWebApplicationType();
    //     if (WebApplicationType.SERVLET == webApplicationType) {
    //         String pattern = SpringUtils.getProperty("spring.mvc.format.date-time");
    //         if (CharSequenceUtil.isNotBlank(pattern)) {
    //             return pattern;
    //         }
    //     } else if (WebApplicationType.REACTIVE == webApplicationType) {
    //         String pattern = SpringUtils.getProperty("spring.webflux.format.date-time");
    //         if (CharSequenceUtil.isNotBlank(pattern)) {
    //             return pattern;
    //         }
    //     }
    //     return DatePattern.NORM_DATETIME_PATTERN;
    // }

    protected WebApplicationType getWebApplicationType() {
        WebApplicationType applicationType = SpringUtils.getProperty("spring.main.web-application-type", WebApplicationType.class);
        if (Objects.nonNull(applicationType)) {
            return applicationType;
        }
        Method method = ReflectUtil.getMethodByName(WebApplicationType.class, "deduceFromClasspath");
        return ReflectUtil.invokeStatic(method);
    }


    protected void appendToMap(Map<String, Object> map, Map<String, Object> otherMap) {
        otherMap.forEach((k, v) -> appendToMap(map, k, v));
    }

    protected void appendToMap(Map<String, Object> map, String key, Object value) {
        map.compute(key, (k, v) -> {
            if (Objects.isNull(v)) {
                if (ObjectUtil.isMultivariable(value)) {
                    Object[] array = org.zero.common.core.util.java.lang.ArrayUtil.of(value);
                    return ListUtil.of(array);
                }
                return value;
            }
            Object[] array = org.zero.common.core.util.java.lang.ArrayUtil.of(value);
            List<Object> list = ListUtil.of(array);
            if (ObjectUtil.isMultivariable(v)) {
                Object[] oldArray = org.zero.common.core.util.java.lang.ArrayUtil.of(v);
                list.addAll(0, Arrays.asList(oldArray));
            } else {
                list.add(0, v);
            }
            return list;
        });
    }

    public static class ObjectConfig {
        protected Object object;
        protected String name = DEFAULT_NAME;
        protected String beanJudgmentRegex = DEFAULT_REGEX;
        protected String levelSeparator = DEFAULT_LEVEL_SEPARATOR;
        protected boolean ignoreNull = true;
        protected String numberPattern;
        protected String datePattern;
        protected String timePattern;
        protected String datetimePattern;
        protected boolean supportFeignAnnotation = true;
        protected boolean supportSpringAnnotation = true;
        protected ConverterComposite converterComposite = ConverterComposite.getInstance();

        public static ObjectConfig of(Object object) {
            return new ObjectConfig(object);
        }

        public static ObjectConfig of(Object object, String name) {
            return new ObjectConfig(object, name);
        }

        protected ObjectConfig(Object object) {
            this.object = object;
        }

        protected ObjectConfig(Object object, String name) {
            this.object = object;
            this.name = name;
        }

        private ObjectConfig(Object object, String name,
                             String beanJudgmentRegex, String levelSeparator, boolean ignoreNull,
                             String numberPattern, String datePattern, String timePattern, String datetimePattern,
                             boolean supportFeignAnnotation, boolean supportSpringAnnotation,
                             ConverterComposite converterComposite) {
            this.object = object;
            this.name = name;
            this.beanJudgmentRegex = beanJudgmentRegex;
            this.levelSeparator = levelSeparator;
            this.ignoreNull = ignoreNull;
            this.numberPattern = numberPattern;
            this.datePattern = datePattern;
            this.timePattern = timePattern;
            this.datetimePattern = datetimePattern;
            this.supportFeignAnnotation = supportFeignAnnotation;
            this.supportSpringAnnotation = supportSpringAnnotation;
            this.converterComposite = converterComposite;
        }

        public ObjectConfig name(String name) {
            this.name = name;
            return this;
        }

        public ObjectConfig beanJudgmentRegex(String beanJudgmentRegex) {
            this.beanJudgmentRegex = beanJudgmentRegex;
            return this;
        }

        public ObjectConfig beanPackageLevelName(String beanPackageLevelName) {
            String regex = String.format(DEFAULT_REGEX_TEMPLATE, beanPackageLevelName);
            return this.beanJudgmentRegex(regex);
        }

        public ObjectConfig beanPackageLevelNames(String... beanPackageLevelNames) {
            String beanPackageLevelNamesJoined = String.join(StringPool.OR, beanPackageLevelNames);
            String regex = String.format(DEFAULT_REGEX_TEMPLATE, beanPackageLevelNamesJoined);
            return this.beanJudgmentRegex(regex);
        }

        public ObjectConfig levelSeparator(String levelSeparator) {
            this.levelSeparator = levelSeparator;
            return this;
        }

        public ObjectConfig ignoreNull() {
            return this.ignoreNull(true);
        }

        public ObjectConfig ignoreNull(boolean ignoreNull) {
            this.ignoreNull = ignoreNull;
            return this;
        }

        public ObjectConfig numberPattern(String numberPattern) {
            this.numberPattern = numberPattern;
            return this;
        }

        public ObjectConfig dateFormat(String dateFormat) {
            this.datePattern = dateFormat;
            return this;
        }

        public ObjectConfig timePattern(String timePattern) {
            this.timePattern = timePattern;
            return this;
        }

        public ObjectConfig datetimePattern(String datetimePattern) {
            this.datetimePattern = datetimePattern;
            return this;
        }

        public ObjectConfig supportFeignAnnotation() {
            return this.supportFeignAnnotation(true);
        }

        public ObjectConfig supportFeignAnnotation(boolean supportFeignAnnotation) {
            this.supportFeignAnnotation = supportFeignAnnotation;
            return this;
        }

        public ObjectConfig supportSpringAnnotation() {
            return this.supportSpringAnnotation(true);
        }

        public ObjectConfig supportSpringAnnotation(boolean supportSpringAnnotation) {
            this.supportSpringAnnotation = supportSpringAnnotation;
            return this;
        }

        public ObjectConfig with(Object object, String name) {
            return new ObjectConfig(object, name,
                    this.beanJudgmentRegex, this.levelSeparator, this.ignoreNull,
                    this.numberPattern, this.datePattern, this.timePattern, this.datetimePattern,
                    this.supportFeignAnnotation, this.supportSpringAnnotation,
                    this.converterComposite);
        }
    }

    protected static class ObjectTree {
        protected ObjectTree parent;
        protected ObjectConfig objectConfig;
        protected PropertyInfo propertyInfo;
        protected Collection<ObjectTree> children = new ArrayList<>();

        public ObjectTree() {
        }

        public ObjectTree(ObjectConfig objectConfig) {
            this.objectConfig = objectConfig;
        }

        public ObjectTree(ObjectConfig objectConfig, PropertyInfo propertyInfo) {
            this.objectConfig = objectConfig;
            this.propertyInfo = propertyInfo;
        }

        public ObjectTree(ObjectTree parent, ObjectConfig objectConfig) {
            this.parent = parent;
            this.objectConfig = objectConfig;
        }

        public ObjectTree(ObjectTree parent, ObjectConfig objectConfig, PropertyInfo propertyInfo) {
            this.parent = parent;
            this.objectConfig = objectConfig;
            this.propertyInfo = propertyInfo;
        }

        public ObjectTree child(ObjectConfig objectConfig) {
            ObjectTree child = new ObjectTree(this, objectConfig);
            this.children.add(child);
            return child;
        }

        public ObjectTree child(ObjectConfig objectConfig, PropertyInfo propertyInfo) {
            ObjectTree child = new ObjectTree(this, objectConfig, propertyInfo);
            this.children.add(child);
            return child;
        }

        public ObjectTree children(ObjectConfig... objectConfigs) {
            ObjectTree[] children = Arrays.stream(objectConfigs)
                    .map(ObjectTree::new)
                    .toArray(ObjectTree[]::new);
            return this.children(children);
        }

        public ObjectTree children(ObjectConfig[] objectConfigs, PropertyInfo[] propertyInfos) {
            int length = Math.min(objectConfigs.length, propertyInfos.length);
            for (int i = 0; i < length; i++) {
                this.child(objectConfigs[i], propertyInfos[i]);
            }
            return this;
        }

        public ObjectTree children(ObjectTree... children) {
            Collections.addAll(this.children, children);
            return this;
        }
    }

    protected static class PropertyInfo {
        protected final Object bean;
        protected final Field field;
        protected final PropertyDescriptor propertyDescriptor;

        public PropertyInfo(Object bean, Field field, PropertyDescriptor propertyDescriptor) {
            this.bean = bean;
            this.field = field;
            this.propertyDescriptor = propertyDescriptor;
        }
    }
}
