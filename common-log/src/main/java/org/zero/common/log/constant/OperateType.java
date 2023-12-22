package org.zero.common.log.constant;

/**
 * @author zero
 * @since 2021/7/21
 */
public enum OperateType {
    /**
     * C：新增
     */
    CREATE,
    /**
     * C：导入
     */
    IMPORT,
    /**
     * R：查询
     */
    READ,
    /**
     * R：导出
     */
    EXPORT,
    /**
     * U：更新
     */
    UPDATE,
    /**
     * D：删除
     */
    DELETE,
    /**
     * D：清空
     */
    CLEAR,

    /**
     * 默认
     */
    OTHER,
    ;
}
