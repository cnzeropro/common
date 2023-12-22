package org.zero.common.core.util.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.data.model.BaseQO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/03/10
 */
@Slf4j
@UtilityClass
public class MpTableInfoUtil {
    /**
     * 属性名（实体-驼峰命名法，如：createTime）转化为字段名（数据库-蛇形命名法，如：create_time）
     */
    public static String toColumn(final String property) {
        return StringUtils.camelToUnderline(property);
    }

    /**
     * 属性名（实体-驼峰命名法，如：createTime）转化为MP缓存的字段名（数据库-蛇形命名法，如：create_time）
     */
    public static String toColumn(final Class<?> clazz, final String property) {
        return getTableFieldInfos(clazz).stream()
                .filter(tableFieldInfo -> Objects.equals(tableFieldInfo.getProperty(), property))
                .map(TableFieldInfo::getColumn)
                .findFirst()
                .orElse(null);
    }

    /**
     * 属性名（实体-驼峰命名法，如：createTime）转化为MP缓存的字段名（数据库-蛇形命名法，如：create_time）
     */
    public static List<String> toColumns(final Class<?> clazz, final String[] properties) {
        if (ArrayUtils.isEmpty(properties)) {
            return new ArrayList<>();
        }

        return getTableFieldInfos(clazz).stream()
                .filter(tableFieldInfo -> Arrays.binarySearch(properties, tableFieldInfo.getProperty()) >= 0)
                .map(TableFieldInfo::getColumn)
                .collect(Collectors.toList());
    }

    /**
     * 获取MP缓存的表字段信息
     */
    public static List<TableFieldInfo> getTableFieldInfos(final Class<?> clazz) {
        return Optional.ofNullable(TableInfoHelper.getTableInfo(clazz)).map(TableInfo::getFieldList).orElseGet(ArrayList::new);
    }

    /**
     * 使用属性名称（如：createTime）填充QueryWrapper
     */
    public static void setSelect(final QueryWrapper<?> queryWrapper, final Class<?> clazz, final String[] properties) {
        if (isSelectAll(properties)) {
            return;
        }
        queryWrapper.select(toColumns(clazz, properties));
    }

    /**
     * 使用字段名称（如：create_time）填充QueryWrapper
     */
    public static void setSelect(final QueryWrapper<?> queryWrapper, final String[] columns) {
        if (isSelectAll(columns)) {
            return;
        }
        queryWrapper.select(columns);
    }

    /**
     * 使用字段名称（如：create_time）填充QueryWrapper
     */
    public static void setSelect(final QueryWrapper<?> queryWrapper, final List<String> columns) {
        if (isSelectAll(columns)) {
            return;
        }
        queryWrapper.select(columns);
    }

    /**
     * 使用属性名称（如：createTime）填充QueryWrapper
     */
    public static void setGroup(final QueryWrapper<?> queryWrapper, final Class<?> clazz, final String[] properties) {
        setGroup(queryWrapper, toColumns(clazz, properties));
    }

    /**
     * 使用字段名称（如：create_time）填充QueryWrapper
     */
    public static void setGroup(final QueryWrapper<?> queryWrapper, final String[] columns) {
        setGroup(queryWrapper, Arrays.asList(columns));
    }

    /**
     * 使用字段名称（如：create_time）填充QueryWrapper
     */
    public static void setGroup(final QueryWrapper<?> queryWrapper, final List<String> columns) {
        queryWrapper.groupBy(CollectionUtils.isNotEmpty(columns), columns);
    }

    /**
     * 使用属性名称（如：createTime）填充QueryWrapper
     */
    public static void setOrder(final QueryWrapper<?> queryWrapper, final Class<?> clazz, final BaseQO.Collation[] collations) {
        setOrder(queryWrapper, Arrays.stream(collations)
                .peek(collation -> collation.setColumn(toColumn(clazz, collation.getColumn())))
                .toArray(BaseQO.Collation[]::new));
    }

    /**
     * 使用字段名称（如：create_time）填充QueryWrapper
     */
    public static void setOrder(final QueryWrapper<?> queryWrapper, final BaseQO.Collation[] collations) {
        for (BaseQO.Collation collation : collations) {
            queryWrapper.orderBy(StringUtils.isNotBlank(collation.getColumn()), collation.isAsc(), collation.getColumn());
        }
    }

    /**
     * 使用属性名称（如：createTime）填充QueryWrapper
     */
    public static void setIn(final QueryWrapper<?> queryWrapper, Class<?> clazz, final BaseQO.MultiVal[] multiVals) {
        setIn(queryWrapper, Arrays.stream(multiVals)
                .peek(multiVal -> multiVal.setColumn(toColumn(clazz, multiVal.getColumn())))
                .toArray(BaseQO.MultiVal[]::new));
    }

    /**
     * 使用字段名称（如：create_time）填充QueryWrapper
     */
    public static void setIn(final QueryWrapper<?> queryWrapper, final BaseQO.MultiVal[] multiVals) {
        for (BaseQO.MultiVal multiVal : multiVals) {
            queryWrapper.in(StringUtils.isNotBlank(multiVal.getColumn()) && ArrayUtils.isNotEmpty(multiVal.getValues()), multiVal.getColumn(), multiVal.getValues());
        }
    }

    /**
     * 使用属性名称（如：createTime）填充QueryWrapper
     */
    public static void setBetween(final QueryWrapper<?> queryWrapper, Class<?> clazz, final BaseQO.Range[] ranges) {
        setBetween(queryWrapper, Arrays.stream(ranges)
                .peek(range -> range.setColumn(toColumn(clazz, range.getColumn())))
                .toArray(BaseQO.Range[]::new));
    }

    /**
     * 使用字段名称（如：create_time）填充QueryWrapper
     */
    public static void setBetween(final QueryWrapper<?> queryWrapper, final BaseQO.Range[] ranges) {
        for (BaseQO.Range range : ranges) {
            queryWrapper.between(StringUtils.isNotBlank(range.getColumn()), range.getColumn(), range.getStartValue(), range.getEndValue());
        }
    }

    private static boolean isSelectAll(final List<String> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return true;
        }
        return fields.stream().anyMatch(MpTableInfoUtil::isSelectAll);
    }

    private static boolean isSelectAll(final String[] fields) {
        if (ArrayUtils.isEmpty(fields)) {
            return true;
        }
        for (String field : fields) {
            if (isSelectAll(field)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSelectAll(final String field) {
        return "*".equals(field);
    }
}
