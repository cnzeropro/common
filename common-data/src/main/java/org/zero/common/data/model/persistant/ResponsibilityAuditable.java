package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * 责任可审计接口
 *
 * @param <CreatedBy> 创建人
 * @param <UpdatedBy> 更新人
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/14
 */
public interface ResponsibilityAuditable<CreatedBy extends Serializable, UpdatedBy extends Serializable> extends CreatorAuditable<CreatedBy>, UpdaterAuditable<UpdatedBy> {
}
