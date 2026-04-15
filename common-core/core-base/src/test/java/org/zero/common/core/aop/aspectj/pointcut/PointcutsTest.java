package org.zero.common.core.aop.aspectj.pointcut;

import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.junit.jupiter.api.Test;
import org.zero.common.core.aop.aspectj.pointcut.fixture.controller.SampleController;
import org.zero.common.core.aop.aspectj.pointcut.fixture.mapper.SampleMapper;
import org.zero.common.core.aop.aspectj.pointcut.fixture.service.SampleService;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/14
 */
class PointcutsTest {
	private final PointcutParser pointcutParser =
			PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution();

	@Test
	void controllerPointcutShouldMatchMultiSegmentPackage() throws NoSuchMethodException {
		assertMatches("controllerMethod", SampleController.class.getMethod("handle"));
	}

	@Test
	void servicePointcutShouldMatchMultiSegmentPackage() throws NoSuchMethodException {
		assertMatches("serviceMethod", SampleService.class.getMethod("execute"));
	}

	@Test
	void mapperPointcutShouldMatchMultiSegmentPackage() throws NoSuchMethodException {
		assertMatches("mapperMethod", SampleMapper.class.getMethod("selectOne"));
	}

	private void assertMatches(String pointcutMethodName, Method method) throws NoSuchMethodException {
		Pointcut pointcut = Pointcuts.class.getMethod(pointcutMethodName).getAnnotation(Pointcut.class);
		PointcutExpression expression = pointcutParser.parsePointcutExpression(pointcut.value());
		assertTrue(expression.matchesMethodExecution(method).alwaysMatches(),
				() -> String.format("Pointcut '%s' should match method %s", pointcut.value(), method));
	}
}
