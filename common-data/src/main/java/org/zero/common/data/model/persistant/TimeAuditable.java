package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/14
 */
public interface TimeAuditable<CreatedAt extends Serializable & Comparable<?>, UpdatedAt extends Serializable & Comparable<?>> extends CreationTimeAuditable<CreatedAt>, UpdateTimeAuditable<UpdatedAt> {
}
