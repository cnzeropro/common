package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * 时间审计接口
 *
 * @param <CreatedAt> 创建时间
 * @param <UpdatedAt> 更新时间
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/14
 */
public interface TimeAuditable<CreatedAt extends Serializable & Comparable<?>, UpdatedAt extends Serializable & Comparable<?>> extends CreationTimeAuditable<CreatedAt>, UpdateTimeAuditable<UpdatedAt> {
}
