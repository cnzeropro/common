package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.io.Serializable;

/**
 * 前端列表查询参数对象
 * <p>
 * 两种使用方式：
 * <ul>
 *     <li>直接使用：直接用于承接前端传入参数（不建议，导致接收参数实体增多）</li>
 *     <li>继承使用：查询实体继承其并进行扩展</li>
 * </ul>
 * <b>警告：因数据库字段由前端传入，所以请注意 SQL 注入检查</b>
 * <p>
 * SQL 注入检查常见的两种方式：
 * <ul>
 *     <li>把前端传入的字段与具体的数据实体（PO、DO 或者 Entity）字段做比较（推荐）</li>
 *     <li>SQL 注入关键词过滤，如：delete，insert，set 等等，可以自己实现也可以使用一些开源工具类</li>
 * </ul>
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
    private Field[] fields = {};

    /**
     * 分组字段（GROUP BY x）
     */
    private String[] groupings = {};

    /**
     * 过滤分组（HAVING x）
     */
    private Condition[] havings = {};

    /**
     * 排序规则（ORDER BY x）
     */
    private Collation[] collations = {};

    @Data
    @With
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "create")
    public static class Field implements Serializable {
        public static final String AS_TEMPLATE = "%s AS %s";

        /**
         * 字段
         */
        private String name;

        /**
         * 别名
         */
        private String alias;

        public String getFieldColumn() {
            return alias == null ? name : String.format(AS_TEMPLATE, name, alias);
        }

        public static Field create(String name) {
            return new Field(name, null);
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
        private String field;

        /**
         * 排序方式。默认：ASC（升序）
         */
        private Order order = Order.ASC;

        public boolean isAsc() {
            return order == Order.ASC;
        }

        public boolean isDesc() {
            return order == Order.DESC;
        }

        public String getOrderColumn() {
            return String.format("%s %s", field, order.name());
        }

        public enum Order {
            ASC, DESC
        }
    }
}
