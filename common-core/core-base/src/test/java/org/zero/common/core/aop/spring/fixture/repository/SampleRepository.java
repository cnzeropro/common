package org.zero.common.core.aop.spring.fixture.repository;

import org.springframework.stereotype.Repository;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
@Repository
public class SampleRepository {
	public Object findById(Long id) {
		return id;
	}
}
