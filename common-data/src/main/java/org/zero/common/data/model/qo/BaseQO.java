package org.zero.common.data.model.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * 前端列表查询参数对象
 * <p>
 * 两种使用方式：
 * 1、直接使用：直接用于承接前端传入参数（不建议，导致接收参数实体增多）
 * 2、继承使用：查询实体继承其并进行扩展
 * <p>
 * <b>警告：因数据库字段由前端传入，所以请注意 SQL 注入检查</b>
 * <p>
 * 常见有两种方式：
 * 1、把前端传入的字段与具体的数据实体（PO、DO 或者 Entity）字段做比较
 * 2、SQL 注入关键词过滤，如：delete，insert，set 等等，可以自己实现也可以使用一些开源工具类
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/1/5
 */
@Data
public class BaseQO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 需求字段（SELECT x[ AS y]）
     */
    @Valid
    @NotEmpty
    private Alias[] aliases = {};

    /**
     * 分组字段（GROUP BY x）
     */
    @NotNull
    private String[] groupings = {};

    /**
     * 过滤分组（HAVING x）
     */
    @Valid
    private Condition[] havings = {};

    /**
     * 排序规则（ORDER BY x）
     */
    @Valid
    private Collation[] collations = {};

    @Data
    @With
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "create")
    public static class Alias implements Serializable {
        public static final String AS_TEMPLATE = "%s AS %s";

        /**
         * 字段
         */
        @NotEmpty
        private String field;

        /**
         * 别名
         */
        private String alias;

        public String getAliasColumn() {
            return Objects.isNull(alias) ? field : String.format(AS_TEMPLATE, field, alias);
        }
    }

    @Data
    @With
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "create")
    public static class Collation implements Serializable {
        /**
         * 排序字段
         */
        @NotEmpty
        private String field;

        /**
         * 排序方式。默认：ASC（升序）
         */
        private Order order = Order.ASC;

        public boolean isAsc() {
            return order == Order.ASC;
        }

        public String getOrderColumn() {
            return String.format("%s %s", field, order.name());
        }

        public enum Order {
            ASC, DESC,
            ;
        }
    }
}
