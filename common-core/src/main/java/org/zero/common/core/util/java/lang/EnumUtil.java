package org.zero.common.core.util.java.lang;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.zero.common.core.util.java.reflect.FieldUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/9/1 17:02
 */
@UtilityClass
public class EnumUtil {

    /**
     * 获取指定名称的枚举对象
     */
    public <E extends Enum<E>> E getEnumByName(Class<E> enumType, String name) {
        for (E enumObj : enumType.getEnumConstants()) {
            if (enumObj.name().equalsIgnoreCase(name)) {
                return enumObj;
            }
        }
        return null;
    }

    /**
     * 获取含有指定属性值的枚举对象，默认只取第一个
     */
    public <E extends Enum<E>> E getEnum(Class<E> enumType, Object val) {
        Field[] fields = enumType.getDeclaredFields();
        for (E enumObj : enumType.getEnumConstants()) {
            for (Field field : fields) {
                Object fieldValue = FieldUtil.getFieldValue(field, enumObj, Object.class);
                if (equalsFieldVal(fieldValue, val)) {
                    return enumObj;
                }
            }
        }
        return null;
    }

    /**
     * 从枚举中获取含有指定名称的属性值
     */
    @SneakyThrows
    public <E extends Enum<E>> Object getVal(E enumObj, String valName) {
        Field[] fields = enumObj.getDeclaringClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(valName)) {
                return FieldUtil.getFieldValue(field, enumObj);
            }
        }
        return null;
    }

    /**
     * 从枚举中获取指定类型的属性值，默认只取第一个
     */
    @SneakyThrows
    public <K, E extends Enum<E>> K getVal(E enumObj, Class<K> valType) {
        Field[] fields = enumObj.getDeclaringClass().getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(field.getType(), valType)) {
                return FieldUtil.getFieldValue(field, enumObj, valType);
            }
        }
        return null;
    }

    /**
     * 从枚举中获取含有指定名称的属性值，并封装成指定类型
     */
    @SneakyThrows
    public <K, E extends Enum<E>> K getVal(E enumObj, String valName, Class<K> valType) {
        Field[] fields = enumObj.getDeclaringClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(valName)) {
                return FieldUtil.getFieldValue(field, enumObj, valType);
            }
        }
        return null;
    }

    /**
     * 从指定属性值的枚举中获取含有指定名称的属性值，默认只能取到第一个
     */
    public <E extends Enum<E>> Object getOtherVal(Class<E> enumType, Object val, String otherValName) {
        return getVal(getEnum(enumType, val), otherValName);
    }

    /**
     * 从指定属性值的枚举中获取含有指定名称的属性值，并封装成指定类型，默认只能取到第一个
     */
    public <K, E extends Enum<E>> K getOtherVal(Class<E> enumType, Object val, String otherValName, Class<K> otherValType) {
        return getVal(getEnum(enumType, val), otherValName, otherValType);
    }

    /**
     * Sonar 认知复杂性过高 修复
     */
    private boolean equalsFieldVal(Object fieldVal, Object val) {
        if (fieldVal == val) {
            return true;
        }

        if (Objects.isNull(fieldVal) || Objects.isNull(val)) {
            return false;
        }

        // 数字类型比较
        if (ClassUtil.isNumber(fieldVal.getClass()) && ClassUtil.isNumber(val.getClass())) {
            BigDecimal numObj = new BigDecimal(String.valueOf(fieldVal));
            BigDecimal numVal = new BigDecimal(String.valueOf(val));
            // numObj.equals(numVal);
            return numObj.compareTo(numVal) == 0;
        } else {
            return Objects.equals(val, fieldVal);
        }
    }
}
