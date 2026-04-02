package org.zero.common.data.model.persistent;

import java.io.Serializable;

/**
 * 责任可审计接口
 *
 * @param <CreatedBy> 创建人
 * @param <UpdatedBy> 更新人
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/14
 */
public interface ActorAuditable<CreatedBy extends Serializable, UpdatedBy extends Serializable> extends CreatedByAuditable<CreatedBy>, UpdatedByAuditable<UpdatedBy> {
}
