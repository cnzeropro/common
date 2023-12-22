package org.zero.common.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 前端列表查询对象，两种使用方式：
 * 1、直接使用：直接用于承接前端传入参数
 * 2、继承使用：查询实体继承其并扩展字段
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
    private String[] columns = {"*"};

    /**
     * 排序规则（order by x）
     */
    private Collation[] collations = {new Collation("createTime", false)};

    /**
     * 等值查询（where x=?）
     */
    private String[] eqs = {};

    /**
     * 非等值查询（where x!=?、x<>?）
     */
    private String[] nes = {};

    /**
     * 查询（where x>?）
     */
    private String[] gts = {};

    /**
     * 查询（where x>=?）
     */
    private String[] ges = {};

    /**
     * 查询（where x<?）
     */
    private String[] lts = {};

    /**
     * 查询（where x<=?）
     */
    private String[] les = {};

    /**
     * 模糊查询（where x like %?%）
     */
    private String[] likes = {};

    /**
     * 模糊查询（where x not like %?%）
     */
    private String[] notLikes = {};

    /**
     * 左模糊查询（where x like %?）
     */
    private String[] leftLikes = {};

    /**
     * 左模糊查询（where x not like %?）
     */
    private String[] notLeftLikes = {};

    /**
     * 右模糊查询（where x like ?%）（可以利用索引）
     */
    private String[] rightLikes = {};

    /**
     * 右模糊查询（where x not like ?%）（可以利用索引）
     */
    private String[] notRightLikes = {};

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
    private MultiVal[] ins = {};

    /**
     * 多值查询（where x not in(?,?,...)）
     */
    private MultiVal[] notIns = {};

    /**
     * 范围查询（where x between ? and ?）
     */
    private Range[] betweens = {};

    /**
     * 范围查询（where x not between ? and ?）
     */
    private Range[] notBetweens = {};

    @Data
    @AllArgsConstructor
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

    @Data
    public static class MultiVal implements Serializable {
        /**
         * in查询字段
         */
        private String column;

        /**
         * in查询条件
         */
        private Object[] values;
    }

    @Data
    public static class Range implements Serializable {
        /**
         * 查询字段
         */
        private String column;

        /**
         * 起始的查询条件
         */
        private Object startValue;

        /**
         * 结束的查询条件
         */
        private Object endValue;
    }
}
