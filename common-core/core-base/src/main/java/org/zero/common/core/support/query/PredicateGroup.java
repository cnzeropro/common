package org.zero.common.core.support.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 组合条件节点。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class PredicateGroup implements PredicateNode {
	private static final long serialVersionUID = 1L;

	private Logic logic = Logic.AND;
	private List<PredicateNode> children = Collections.emptyList();
}
