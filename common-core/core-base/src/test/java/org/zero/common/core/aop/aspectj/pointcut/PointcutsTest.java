package org.zero.common.core.aop.aspectj.pointcut;

import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutParser;
import org.junit.jupiter.api.Test;
import org.zero.common.core.aop.aspectj.pointcut.fixture.accessor.SampleAccessorBean;
import org.zero.common.core.aop.aspectj.pointcut.fixture.controller.SampleController;
import org.zero.common.core.aop.aspectj.pointcut.fixture.dao.SampleDao;
import org.zero.common.core.aop.aspectj.pointcut.fixture.mapper.SampleMapper;
import org.zero.common.core.aop.aspectj.pointcut.fixture.repository.SampleRepository;
import org.zero.common.core.aop.aspectj.pointcut.fixture.service.SampleService;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

	@Test
	void repositoryPointcutShouldMatchMultiSegmentPackage() throws NoSuchMethodException {
		assertMatches("repositoryMethod", SampleRepository.class.getMethod("findById", Long.class));
	}

	@Test
	void daoPointcutShouldMatchMultiSegmentPackage() throws NoSuchMethodException {
		assertMatches("daoMethod", SampleDao.class.getMethod("load", Long.class));
	}

	@Test
	void isPointcutShouldMatchBooleanStyleGetter() throws NoSuchMethodException {
		assertMatches("isMethod", SampleAccessorBean.class.getMethod("isEnabled"));
	}

	@Test
	void accessorPointcutShouldMatchGetterSetterAndBooleanGetter() throws NoSuchMethodException {
		assertMatches("accessorMethod", SampleAccessorBean.class.getMethod("getName"));
		assertMatches("accessorMethod", SampleAccessorBean.class.getMethod("isEnabled"));
		assertMatches("accessorMethod", SampleAccessorBean.class.getMethod("setName", String.class));
	}

	@Test
	void accessorPointcutShouldNotMatchRegularMethod() throws NoSuchMethodException {
		assertNotMatches("accessorMethod", SampleAccessorBean.class.getMethod("execute"));
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
