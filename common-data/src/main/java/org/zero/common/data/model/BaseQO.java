package org.zero.common.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String[] columns = new String[]{"*"};

    /**
     * 排序规则（order by x）
     */
    private Collation[] collations = {};

    /**
     * 等值查询（where x=?）
     */
    private T eq;

    /**
     * 非等值查询（where x!=?、x<>?）
     */
    private T ne;

    /**
     * 查询（where x>?）
     */
    private T gt;

    /**
     * 查询（where x>=?）
     */
    private T ge;

    /**
     * 查询（where x<?）
     */
    private T lt;

    /**
     * 查询（where x<=?）
     */
    private T le;

    /**
     * 模糊查询（where x like %?%）
     */
    private T like;

    /**
     * 模糊查询（where x not like %?%）
     */
    private T notLike;

    /**
     * 左模糊查询（where x like %?）
     */
    private T leftLike;

    /**
     * 左模糊查询（where x not like %?）
     */
    private T notLeftLike;

    /**
     * 右模糊查询（where x like ?%）（可以利用索引）
     */
    private T rightLike;

    /**
     * 右模糊查询（where x not like ?%）（可以利用索引）
     */
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
    private MV in;

    /**
     * 多值查询（where x not in(?,?,...)）
     */
    private MV notIn;

    /**
     * 范围查询（where x between ? and ?）
     */
    private R between;

    /**
     * 范围查询（where x not between ? and ?）
     */
    private R notBetween;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "create")
    public static class Collation implements Serializable {
        /**
         * 排序字段
         */
        private String column;

        /**
         * 排序方式，是否升序，默认true
         */
        private boolean asc = true;
    }
}
