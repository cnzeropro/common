package org.zero.common.core.extension.jakarta.validation.groups;

/**
 * CRUD 场景的 Bean Validation 分组。
 * <p>
 * 分组本身不继承 {@link jakarta.validation.groups.Default}，需要默认组时请在调用方显式组合
 * {@link jakarta.validation.groups.Default}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
public final class CrudValidationGroups {
	private CrudValidationGroups() {
	}

	/**
	 * 新增场景。
	 */
	public interface Create {
	}

	/**
	 * 查询场景。
	 */
	public interface Read {
	}

	/**
	 * 更新场景。
	 */
	public interface Update {
	}

	/**
	 * 删除场景。
	 */
	public interface Delete {
	}
}
