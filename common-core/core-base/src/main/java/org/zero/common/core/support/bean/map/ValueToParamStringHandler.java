package org.zero.common.core.support.bean.map;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import org.springframework.boot.WebApplicationType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.zero.common.core.support.context.spring.SpringUtils;
import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.ObjectUtil;
import org.zero.common.data.constant.StringPool;
import org.zero.common.data.exception.UtilException;

import java.lang.reflect.AnnotatedElement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
public class ValueToParamStringHandler extends MapValueHandler<String> {
    public static final String DEFAULT_MULTIVARIABLE_SEPARATOR = StringPool.COMMA;
    protected final String nullString;
    protected final String multivariableSeparator;

	public ValueToParamStringHandler() {
		this(false);
	}
    public ValueToParamStringHandler(boolean append) {
        this(true, append);
    }

    public ValueToParamStringHandler(boolean ignoreNull, boolean append) {
        this(ignoreNull, append, StringPool.EMPTY, DEFAULT_MULTIVARIABLE_SEPARATOR);
    }

    public ValueToParamStringHandler(boolean append, String nullString) {
        this(false, append, nullString, DEFAULT_MULTIVARIABLE_SEPARATOR);
    }

    public ValueToParamStringHandler(boolean append, String nullString, String multivariableSeparator) {
        this(false, append, nullString, multivariableSeparator);
    }

    public ValueToParamStringHandler(boolean ignoreNull, boolean append, String nullString, String multivariableSeparator) {
        super(ignoreNull, append);
        this.nullString = nullString;
        this.multivariableSeparator = multivariableSeparator;
    }

    @Override
    public String convert(Object object, BeanProperty beanProperty) {
        if (ObjectUtil.isNull(object)) {
            if (ignoreNull) {
                return null;
            }
            return nullString;
        }
        if (ObjectUtil.isMultivariable(object)) {
            Object[] objects = ArrayUtil.of(object);
            return this.join(objects, obj -> this.convert(obj, beanProperty));
        }
        if (ObjectUtil.isNumber(object)) {
            return this.formatNum(object, beanProperty);
        }
        if (ObjectUtil.isDatetime(object)) {
            return this.formatDataTime(object, beanProperty);
        }
        return Objects.toString(object);
    }

    protected String join(Object[] objects, Function<Object, String> mapper) {
        Stream<String> stream = Arrays.stream(objects).map(mapper);
        stream = ignoreNull ? stream.filter(Objects::nonNull) : stream;
        return stream.collect(Collectors.joining(DEFAULT_MULTIVARIABLE_SEPARATOR));
    }

    @Override
    public void addToMap(ObjectTree objectTree, Map<String, String> map, String value) {
        String key = objectTree.key;
        if (!append) {
            map.put(key, value);
            return;
        }
        map.compute(key, (k, v) -> {
            if (Objects.isNull(v)) {
                return value;
            }
            return v + DEFAULT_MULTIVARIABLE_SEPARATOR + value;
        });
    }

    protected String formatNum(Object numObj, BeanProperty beanProperty) {
        if (Objects.isNull(numObj)) {
            return null;
        }
        String pattern = this.getNumberPattern(beanProperty);
        if (CharSequenceUtil.isBlank(pattern)) {
            return numObj.toString();
        }
        return NumberUtil.decimalFormat(pattern, numObj);
    }

    protected String getNumberPattern(BeanProperty beanProperty) {
        if (Objects.nonNull(beanProperty)) {
            AnnotatedElement annotatedElement = beanProperty.getField();
            if (Objects.isNull(annotatedElement)) {
                annotatedElement = beanProperty.getGetterMethod();
            }
            if (Objects.nonNull(annotatedElement)) {
                NumberFormat numberFormat = AnnotationUtils.findAnnotation(annotatedElement, NumberFormat.class);
                if (Objects.nonNull(numberFormat)) {
                    return numberFormat.pattern();
                }
            }
        }
        return null;
    }

    protected String formatDataTime(Object dataTimeObj, BeanProperty beanProperty) {
        if (Objects.isNull(dataTimeObj)) {
            return null;
        }
        if (dataTimeObj instanceof Date) {
            String pattern = this.getDatetimePattern(beanProperty);
            return DateUtil.format((Date) dataTimeObj, pattern);
        }
        if (dataTimeObj instanceof Calendar) {
            String pattern = this.getDatetimePattern(beanProperty);
            return DateUtil.format(((Calendar) dataTimeObj).getTime(), pattern);
        }
        if (dataTimeObj instanceof TemporalAccessor) {
            String pattern;
            if (dataTimeObj instanceof LocalDate) {
                pattern = this.getDatePattern(beanProperty);
            } else if (dataTimeObj instanceof LocalTime) {
                pattern = this.getTimePattern(beanProperty);
            } else {
                pattern = this.getDatetimePattern(beanProperty);
            }
            return TemporalAccessorUtil.format((TemporalAccessor) dataTimeObj, pattern);
        }
        throw new UtilException(String.format("Not datetime type, cannot be formatted: %s", dataTimeObj.getClass()));
    }

    protected String getDatePattern(BeanProperty beanProperty) {
        if (Objects.nonNull(beanProperty)) {
            AnnotatedElement annotatedElement = beanProperty.getField();
            if (Objects.isNull(annotatedElement)) {
                annotatedElement = beanProperty.getGetterMethod();
            }
            if (Objects.nonNull(annotatedElement)) {
                DateTimeFormat dateTimeFormat = AnnotationUtils.findAnnotation(annotatedElement, DateTimeFormat.class);
                if (Objects.nonNull(dateTimeFormat)) {
                    return dateTimeFormat.pattern();
                }
            }
        }
        WebApplicationType webApplicationType = this.getWebApplicationType();
        if (WebApplicationType.SERVLET == webApplicationType) {
            String pattern = SpringUtils.getProperty("spring.mvc.format.date");
            if (CharSequenceUtil.isNotBlank(pattern)) {
                return pattern;
            }
        } else if (WebApplicationType.REACTIVE == webApplicationType) {
            String pattern = SpringUtils.getProperty("spring.webflux.format.date");
            if (CharSequenceUtil.isNotBlank(pattern)) {
                return pattern;
            }
        }
        return DatePattern.NORM_DATE_PATTERN;
    }

    protected String getTimePattern(BeanProperty beanProperty) {
        if (Objects.nonNull(beanProperty)) {
            AnnotatedElement annotatedElement = beanProperty.getField();
            if (Objects.isNull(annotatedElement)) {
                annotatedElement = beanProperty.getGetterMethod();
            }
            if (Objects.nonNull(annotatedElement)) {
                DateTimeFormat dateTimeFormat = AnnotationUtils.findAnnotation(annotatedElement, DateTimeFormat.class);
                if (Objects.nonNull(dateTimeFormat)) {
                    return dateTimeFormat.pattern();
                }
            }
        }
        WebApplicationType webApplicationType = this.getWebApplicationType();
        if (WebApplicationType.SERVLET == webApplicationType) {
            String pattern = SpringUtils.getProperty("spring.mvc.format.time");
            if (CharSequenceUtil.isNotBlank(pattern)) {
                return pattern;
            }
        } else if (WebApplicationType.REACTIVE == webApplicationType) {
            String pattern = SpringUtils.getProperty("spring.webflux.format.time");
            if (CharSequenceUtil.isNotBlank(pattern)) {
                return pattern;
            }
        }
        return DatePattern.NORM_TIME_PATTERN;
    }

    protected String getDatetimePattern(BeanProperty beanProperty) {
        if (Objects.nonNull(beanProperty)) {
            AnnotatedElement annotatedElement = beanProperty.getField();
            if (Objects.isNull(annotatedElement)) {
                annotatedElement = beanProperty.getGetterMethod();
            }
            if (Objects.nonNull(annotatedElement)) {
                DateTimeFormat dateTimeFormat = AnnotationUtils.findAnnotation(annotatedElement, DateTimeFormat.class);
                if (Objects.nonNull(dateTimeFormat)) {
                    return dateTimeFormat.pattern();
                }
            }
        }
        WebApplicationType webApplicationType = this.getWebApplicationType();
        if (WebApplicationType.SERVLET == webApplicationType) {
            String pattern = SpringUtils.getProperty("spring.mvc.format.date-time");
            if (CharSequenceUtil.isNotBlank(pattern)) {
                return pattern;
            }
        } else if (WebApplicationType.REACTIVE == webApplicationType) {
            String pattern = SpringUtils.getProperty("spring.webflux.format.date-time");
            if (CharSequenceUtil.isNotBlank(pattern)) {
                return pattern;
            }
        }
        return DatePattern.NORM_DATETIME_PATTERN;
    }

    protected WebApplicationType getWebApplicationType() {
        return SpringUtils.getWebApplicationType();
    }
}
