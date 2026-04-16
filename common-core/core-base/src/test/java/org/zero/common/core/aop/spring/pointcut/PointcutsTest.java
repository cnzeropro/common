package org.zero.common.core.aop.spring.pointcut;

import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutParser;
import org.junit.jupiter.api.Test;
import org.zero.common.core.aop.spring.pointcut.fixture.advice.SampleControllerAdvice;
import org.zero.common.core.aop.spring.pointcut.fixture.advice.SampleRestControllerAdvice;
import org.zero.common.core.aop.spring.pointcut.fixture.controller.SampleController;
import org.zero.common.core.aop.spring.pointcut.fixture.controller.SampleRestController;
import org.zero.common.core.aop.spring.pointcut.fixture.dao.SampleDao;
import org.zero.common.core.aop.spring.pointcut.fixture.mapper.SampleMapper;
import org.zero.common.core.aop.spring.pointcut.fixture.repository.SampleRepository;
import org.zero.common.core.aop.spring.pointcut.fixture.service.SampleService;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class PointcutsTest {
	private final PointcutParser pointcutParser =
			PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution();

	@Test
	void controllerAnnotationPointcutShouldMatchController() throws NoSuchMethodException {
		assertMatches("controllerAnnotationMethod", SampleController.class.getMethod("handle"));
	}

	@Test
	void controllerAnnotationPointcutShouldMatchRestController() throws NoSuchMethodException {
		assertMatches("controllerAnnotationMethod", SampleRestController.class.getMethod("handle"));
	}

	@Test
	void serviceAnnotationPointcutShouldMatchService() throws NoSuchMethodException {
		assertMatches("serviceAnnotationMethod", SampleService.class.getMethod("execute"));
	}

	@Test
	void repositoryAnnotationPointcutShouldMatchRepository() throws NoSuchMethodException {
		assertMatches("repositoryAnnotationMethod", SampleRepository.class.getMethod("findById", Long.class));
	}

	@Test
	void controllerAdvicePointcutShouldMatchControllerAdvice() throws NoSuchMethodException {
		assertMatches("controllerAdviceMethod", SampleControllerAdvice.class.getMethod("handleException", RuntimeException.class));
	}

	@Test
	void controllerAdvicePointcutShouldMatchRestControllerAdvice() throws NoSuchMethodException {
		assertMatches("controllerAdviceMethod", SampleRestControllerAdvice.class.getMethod("handleException", RuntimeException.class));
	}

	@Test
	void webPointcutShouldMatchControllerAndAdvice() throws NoSuchMethodException {
		assertMatches("webMethod", SampleController.class.getMethod("handle"));
		assertMatches("webMethod", SampleRestControllerAdvice.class.getMethod("handleException", RuntimeException.class));
	}

	@Test
	void webPointcutShouldNotMatchRepositoryMethod() throws NoSuchMethodException {
		assertNotMatches("webMethod", SampleRepository.class.getMethod("findById", Long.class));
	}

	@Test
	void businessServicePointcutShouldMatchService() throws NoSuchMethodException {
		assertMatches("businessServiceMethod", SampleService.class.getMethod("execute"));
	}

	@Test
	void dataAccessPointcutShouldMatchMapperDaoAndRepository() throws NoSuchMethodException {
		assertMatches("dataAccessMethod", SampleMapper.class.getMethod("selectOne"));
		assertMatches("dataAccessMethod", SampleDao.class.getMethod("load", Long.class));
		assertMatches("dataAccessMethod", SampleRepository.class.getMethod("findById", Long.class));
	}

	@Test
	void dataAccessPointcutShouldNotMatchControllerMethod() throws NoSuchMethodException {
		assertNotMatches("dataAccessMethod", SampleController.class.getMethod("handle"));
	}

	@Test
	void publicWebPointcutShouldMatchOnlyPublicWebMethods() throws NoSuchMethodException {
		assertMatches("publicWebMethod", SampleController.class.getMethod("handle"));
		assertNotMatches("publicWebMethod", SampleController.class.getDeclaredMethod("internal"));
	}

	@Test
	void publicBusinessServicePointcutShouldMatchPublicServiceMethods() throws NoSuchMethodException {
		assertMatches("publicBusinessServiceMethod", SampleService.class.getMethod("execute"));
	}

	@Test
	void publicDataAccessPointcutShouldMatchPublicDataAccessMethods() throws NoSuchMethodException {
		assertMatches("publicDataAccessMethod", SampleRepository.class.getMethod("findById", Long.class));
	}

	private void assertMatches(String pointcutMethodName, Method method) throws NoSuchMethodException {
		PointcutExpression expression = parsePointcutExpression(pointcutMethodName);
		assertTrue(expression.matchesMethodExecution(method).alwaysMatches(),
				() -> String.format("Pointcut '%s' should match method %s", expression.getPointcutExpression(), method));
	}

	private void assertNotMatches(String pointcutMethodName, Method method) throws NoSuchMethodException {
		PointcutExpression expression = parsePointcutExpression(pointcutMethodName);
		assertFalse(expression.matchesMethodExecution(method).alwaysMatches(),
				() -> String.format("Pointcut '%s' should not match method %s", expression.getPointcutExpression(), method));
	}

	private PointcutExpression parsePointcutExpression(String pointcutMethodName) throws NoSuchMethodException {
		Pointcut pointcut = Pointcuts.class.getMethod(pointcutMethodName).getAnnotation(Pointcut.class);
		return pointcutParser.parsePointcutExpression(pointcut.value(), Pointcuts.class, new PointcutParameter[0]);
	}
}
