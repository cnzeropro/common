package org.zero.common.core.util.java.lang.reflect;

import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.junit.jupiter.api.Test;
import org.zero.common.data.exception.UtilException;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/10
 */
class LambdaUtilTest {

	@Test
	void extractShouldReadMethodReferenceMetadata() {
		LambdaMeta lambdaMeta = LambdaUtil.extract((SFunction<Timestamp, Long>) Date::getTime);

		assertEquals("getTime", lambdaMeta.getImplMethodName());
		assertEquals(Date.class, lambdaMeta.getImplClass());
		assertEquals(Timestamp.class, lambdaMeta.getInstantiatedClass());
	}

	@Test
	void extractShouldSupportLambdaAndInterfaceMethodReference() {
		int value = 1;
		SerializableComparator<Integer> comparator = (left, right) -> value;

		LambdaMeta comparatorMeta = LambdaUtil.extract(comparator);
		LambdaMeta streamMeta = LambdaUtil.extract((SerializableFunction<List<?>, Stream<?>>) Collection::stream);

		assertTrue(comparatorMeta.getImplMethodName().contains("lambda$"));
		assertEquals("stream", streamMeta.getImplMethodName());
		assertEquals(Collection.class, streamMeta.getImplClass());
	}

	@Test
	void extractShouldRejectNonLambdaObject() {
		assertThrows(UtilException.class, () -> LambdaUtil.extract("a"));
	}

	@Test
	void isLambdaShouldDetectLambdaObject() {
		assertTrue(LambdaUtil.isLambda((Function<Timestamp, Long>) Date::getTime));
		assertFalse(LambdaUtil.isLambda("a"));
	}

	private interface SerializableComparator<T> extends Comparator<T>, Serializable {
	}

	private interface SerializableFunction<T, R> extends Function<T, R>, Serializable {
	}
}
