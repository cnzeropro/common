package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface UpdateAuditable<UpdatedBy extends Serializable, UpdatedAt extends Serializable & Comparable<?>> extends UpdaterAuditable<UpdatedBy>, UpdateTimeAuditable<UpdatedAt> {
}
