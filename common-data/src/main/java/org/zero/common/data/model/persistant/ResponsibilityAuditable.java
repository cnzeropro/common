package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/14
 */
public interface ResponsibilityAuditable<CreatedBy extends Serializable, UpdatedBy extends Serializable> extends CreatorAuditable<CreatedBy>, UpdaterAuditable<UpdatedBy> {
}
