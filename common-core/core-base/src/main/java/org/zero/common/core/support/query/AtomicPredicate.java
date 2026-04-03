package org.zero.common.core.support.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 原子条件节点。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AtomicPredicate implements PredicateNode {
	private static final long serialVersionUID = 1L;

	private String field;
	private String operator;
	private List<Object> values = Collections.emptyList();
}
