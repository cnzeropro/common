package org.zero.common.core.extension.common.query;

import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.zero.common.core.util.java.reflect.ClassUtil;
import org.zero.common.data.model.query.Condition;
import org.zero.common.data.model.query.Operator;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/2
 */
@UtilityClass
public class ConditionUtil {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\?");

    /**
     * 获取 SQL 片段，如：name LIKE '%a%'
     */
    public static String getSqlSnippet(Condition condition) {
        String precompiledSnippet = getPrecompiledSnippet(condition);
        Object formattedParam = getFormattedParam(condition);

        if (!precompiledSnippet.contains("?")) {
            return precompiledSnippet;
        }

        Object[] params = {formattedParam};
        if (formattedParam instanceof String) {
            String str = (String) formattedParam;
            if (str.contains(",")) {
                params = StringUtils.delimitedListToStringArray(str, ",");
            }
        } else if (ObjectUtils.isArray(formattedParam)) {
            params = (Object[]) formattedParam;
        } else if (formattedParam instanceof Collection) {
            Collection<?> collection = (Collection<?>) formattedParam;
            params = collection.toArray();
        }

        String sqlSnippet = precompiledSnippet;
        for (Object param : params) {
            String paramStr = getParamStr(param);
            sqlSnippet = PLACEHOLDER_PATTERN.matcher(sqlSnippet)
                    .replaceFirst(paramStr);
        }
        return sqlSnippet;
    }

    private static final Pattern NOT_NEED_QUOTE_PATTERN = Pattern.compile("^(true|false|\\d+(\\.\\d+)?)$", Pattern.CASE_INSENSITIVE);

    private static String getParamStr(Object param) {
        if (Objects.isNull(param)) {
            return "null";
        }

        Class<?> clazz = param.getClass();
        if (ClassUtil.isNumClass(clazz)) {
            // 避免科学计数法的干扰
            return new BigDecimal(param.toString()).toPlainString();
        } else {
            String str = param.toString();
            if (NOT_NEED_QUOTE_PATTERN.matcher(str).find()) {
                return str;
            } else {
                return String.format("'%s'", param);
            }
        }
    }

    /**
     * 获取可以预编译的 SQL 片段，如：name LIKE ?
     */
    public static String getPrecompiledSnippet(Condition condition) {
        Operator operator = condition.getOperator();
        String field = condition.getField();
        Object value = condition.getValue();
        String template = operator.getTemplate();
        Object[] args = new Object[]{field};
        Collection<Function<Object, String>> statementMappers = operator.getStatementMappers();
        if (Objects.nonNull(value) && !CollectionUtils.isEmpty(statementMappers)) {
            for (Function<Object, String> statementMapper : statementMappers) {
                Object applied = statementMapper.apply(value);
                args = ObjectUtils.addObjectToArray(args, applied);
            }
        }
        return String.format(template, args);
    }

    /**
     * 获取处理的 SQL 参数，如：%a%
     */
    public static Object getFormattedParam(Condition condition) {
        Operator operator = condition.getOperator();
        Object value = condition.getValue();
        // 目前仅支持一个参数
        Collection<UnaryOperator<Object>> paramMappers = operator.getParamMappers();
        if (Objects.isNull(paramMappers) || CollectionUtils.isEmpty(paramMappers)) {
            return value;
        }
        return paramMappers.stream()
                .findFirst()
                .map(paramMapper -> paramMapper.apply(value))
                .orElse(value);
    }
}
