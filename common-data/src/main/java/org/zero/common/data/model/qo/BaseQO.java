package org.zero.common.data.model.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 前端列表查询对象
 * <p>
 * 两种使用方式：
 * 1、直接使用：直接用于承接前端传入参数（不建议）
 * 2、继承使用：查询实体继承其并进行扩展
 * <p>
 * 警告：因数据库字段由前端传入，所以请注意 SQL 注入检查
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
     * 需求字段（select x）
     */
    @NotEmpty
    private String[] columns = new String[]{"*"};

    /**
     * 排序规则（order by x）
     */
    @Valid
    private Collation[] collations = {};

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "create")
    public static class Collation implements Serializable {
        /**
         * 排序字段
         */
        private String column;

        /**
         * 排序方式：是否升序。默认：true（升序）
         */
        private boolean asc = true;
    }
}
