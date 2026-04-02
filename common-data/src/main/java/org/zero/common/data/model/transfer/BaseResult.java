package org.zero.common.data.model.transfer;

import java.io.Serializable;

/**
 * 基础结果接口，用于标识远程调用或业务操作的返回结果是否成功。
 *
 * <p>所有表示调用/操作结果的 DTO 都应实现此接口，以便上层统一判断成功与否。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2014/11/18
 */
public interface BaseResult extends Serializable {

    /**
     * 判断操作是否成功。
     *
     * @return {@code true} 表示成功，{@code false} 表示失败
     */
    boolean isSuccess();
}
