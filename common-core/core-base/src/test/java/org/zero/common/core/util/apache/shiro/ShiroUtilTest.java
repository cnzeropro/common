package org.zero.common.core.util.apache.shiro;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.zero.common.data.model.security.ShiroLoginUser;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class ShiroUtilTest {
	@AfterEach
	void tearDown() {
		ThreadContext.remove();
	}

	@Test
	void shouldResolveShiroLoginUserFromContext() {
		ShiroLoginUser loginUser = new ShiroLoginUser(4L, "shiro-user", Collections.singletonMap("dept", "platform"));
		DefaultSecurityManager securityManager = new DefaultSecurityManager();
		Subject subject = new Subject.Builder(securityManager)
				.principals(new SimplePrincipalCollection(loginUser, "testRealm"))
				.authenticated(true)
				.buildSubject();

		ThreadContext.bind(securityManager);
		ThreadContext.bind(subject);

		assertAll(
				() -> assertSame(loginUser, ShiroUtil.getUser()),
				() -> assertEquals(Long.valueOf(4L), ShiroUtil.getUserId()),
				() -> assertEquals("shiro-user", ShiroUtil.getUsername())
		);
	}
}
