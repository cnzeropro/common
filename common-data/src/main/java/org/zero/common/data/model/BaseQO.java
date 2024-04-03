package org.zero.common.data.model;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;

/**
 * 前端列表查询对象，两种使用方式：
 * 1、直接使用：直接用于承接前端传入参数
 * 2、继承使用：查询实体继承其并扩展字段
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/1/5
 */
@Setter
@Getter
@ToString
public class BaseQO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 需求字段（select x）
     */
    private Collection<String> columns = CollUtil.newHashSet("*");

    /**
     * 排序规则（order by x）
     */
    private Collection<Collation> collations = CollUtil.newHashSet(Collation.create("createTime", false));

    /**
     * 等值查询（where x=?）
     */
    private Collection<QueryCondition<Object>> eqs = CollUtil.newHashSet();

    /**
     * 非等值查询（where x!=?、x<>?）
     */
    private Collection<QueryCondition<Object>> nes = CollUtil.newHashSet();

    /**
     * 查询（where x>?）
     */
    private Collection<QueryCondition<Object>> gts = CollUtil.newHashSet();

    /**
     * 查询（where x>=?）
     */
    private Collection<QueryCondition<Object>> ges = CollUtil.newHashSet();

    /**
     * 查询（where x<?）
     */
    private Collection<QueryCondition<Object>> lts = CollUtil.newHashSet();

    /**
     * 查询（where x<=?）
     */
    private Collection<QueryCondition<Object>> les = CollUtil.newHashSet();

    /**
     * 模糊查询（where x like %?%）
     */
    private Collection<QueryCondition<String>> likes = CollUtil.newHashSet();

    /**
     * 模糊查询（where x not like %?%）
     */
    private Collection<QueryCondition<String>> notLikes = CollUtil.newHashSet();

    /**
     * 左模糊查询（where x like %?）
     */
    private Collection<QueryCondition<String>> leftLikes = CollUtil.newHashSet();

    /**
     * 左模糊查询（where x not like %?）
     */
    private Collection<QueryCondition<String>> notLeftLikes = CollUtil.newHashSet();

    /**
     * 右模糊查询（where x like ?%）（可以利用索引）
     */
    private Collection<QueryCondition<String>> rightLikes = CollUtil.newHashSet();

    /**
     * 右模糊查询（where x not like ?%）（可以利用索引）
     */
    private Collection<QueryCondition<String>> notRightLikes = CollUtil.newHashSet();

    /**
     * is null查询（where x is null）
     */
    private Collection<String> nulls = CollUtil.newHashSet();

    /**
     * is not null查询（where x is not null）
     */
    private Collection<String> notNulls = CollUtil.newHashSet();

    /**
     * 多值查询（where x in(?,?,...)）
     */
    private Collection<QueryCondition<Object[]>> ins = CollUtil.newHashSet();

    /**
     * 多值查询（where x not in(?,?,...)）
     */
    private Collection<QueryCondition<Object[]>> notIns = CollUtil.newHashSet();

    /**
     * 范围查询（where x between ? and ?）
     */
    private Collection<Range> betweens = CollUtil.newHashSet();

    /**
     * 范围查询（where x not between ? and ?）
     */
    private Collection<Range> notBetweens = CollUtil.newHashSet();

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "create")
    @ToString
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

    @Setter
    @Getter
    @ToString
    public static class QueryCondition<T> implements Serializable {
        /**
         * 查询字段
         */
        private String column;

        /**
         * 查询条件
         */
        private T value;
    }

    @Setter
    @Getter
    @ToString(callSuper = true)
    public static class Range extends QueryCondition<Object> {
        /**
         * 起始的查询条件
         */
        private Object start;

        /**
         * 结束的查询条件
         */
        private Object end;
    }
}
