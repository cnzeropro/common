package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * 创建可审计接口
 *
 * @param <CreatedBy> 创建人
 * @param <CreatedAt> 创建时间
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface CreationAuditable<CreatedBy extends Serializable, CreatedAt extends Serializable & Comparable<?>> extends CreatorAuditable<CreatedBy>, CreationTimeAuditable<CreatedAt> {
}
