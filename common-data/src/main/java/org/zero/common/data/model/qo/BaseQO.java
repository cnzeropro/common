package org.zero.common.data.model.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 前端列表查询对象，两种使用方式：
 * 1、直接使用：直接用于承接前端传入参数（请求体 JSON 参数）
 * 2、继承使用：查询实体继承其并进行扩展（URL 参数）
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/1/5
 */
@Data
public class BaseQO<T, MV, R> implements Serializable {
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

    /**
     * 等值查询（where x=?）
     */
    @Valid
    private T eq;

    /**
     * 非等值查询（where x!=?、x<>?）
     */
    @Valid
    private T ne;

    /**
     * 查询（where x>?）
     */
    @Valid
    private T gt;

    /**
     * 查询（where x>=?）
     */
    @Valid
    private T ge;

    /**
     * 查询（where x<?）
     */
    @Valid
    private T lt;

    /**
     * 查询（where x<=?）
     */
    @Valid
    private T le;

    /**
     * 模糊查询（where x like %?%）
     */
    @Valid
    private T like;

    /**
     * 模糊查询（where x not like %?%）
     */
    @Valid
    private T notLike;

    /**
     * 左模糊查询（where x like %?）
     */
    @Valid
    private T leftLike;

    /**
     * 左模糊查询（where x not like %?）
     */
    @Valid
    private T notLeftLike;

    /**
     * 右模糊查询（where x like ?%）（可以利用索引）
     */
    @Valid
    private T rightLike;

    /**
     * 右模糊查询（where x not like ?%）（可以利用索引）
     */
    @Valid
    private T notRightLike;

    /**
     * is null查询（where x is null）
     */
    private String[] nulls = {};

    /**
     * is not null查询（where x is not null）
     */
    private String[] notNulls = {};

    /**
     * 多值查询（where x in(?,?,...)）
     */
    @Valid
    private MV in;

    /**
     * 多值查询（where x not in(?,?,...)）
     */
    @Valid
    private MV notIn;

    /**
     * 范围查询（where x between ? and ?）
     */
    @Valid
    private R between;

    /**
     * 范围查询（where x not between ? and ?）
     */
    @Valid
    private R notBetween;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "create")
    public static class Collation implements Serializable {
        /**
         * 排序字段
         */
        @NotBlank
        private String column;

        /**
         * 排序方式，是否升序，默认true
         */
        private boolean asc = true;
    }
}
