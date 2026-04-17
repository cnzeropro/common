package org.zero.common.core.extension.java.lang;

import org.junit.jupiter.api.Test;
import org.zero.common.data.exception.CommonException;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class InnerBuilderTest {
	@Test
	void shouldBuildWithAutoInferredConstructorMetadata() {
		AutoInferredConstructorPojo pojo = AutoInferredConstructorPojo.builder()
				.id(1)
				.name("zero")
				.build();

		assertEquals(Integer.valueOf(1), pojo.id);
		assertEquals("zero", pojo.name);
	}

	@Test
	void shouldPropagateConstructorExceptionWithoutFallback() {
		InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> GuardedPojo.builder().build());

		assertTrue(exception.getTargetException() instanceof IllegalStateException);
		assertEquals("name must not be null", exception.getTargetException().getMessage());
	}

	@Test
	void shouldInstantiateWithPrivateNoArgsConstructorAndSetters() {
		SetterBackedPojo pojo = SetterBackedPojo.builder()
				.name("zero")
				.count(3)
				.build();

		assertEquals("zero", pojo.name);
		assertEquals(3, pojo.count);
	}

	@Test
	void shouldFailFastWhenSetterIsMissing() {
		CommonException exception = assertThrows(CommonException.class, () -> MissingSetterPojo.builder()
				.name("zero")
				.build());

		assertTrue(exception.getMessage().contains(MissingSetterPojo.class.getName()));
		assertTrue(exception.getMessage().contains("name"));
	}

	@Test
	void shouldRejectMismatchedConstructorMetadata() {
		CommonException exception = assertThrows(CommonException.class, () -> MetadataMismatchPojo.builder().build());

		assertTrue(exception.getMessage().contains("length mismatch"));
	}

	@Test
	void shouldFailWhenExplicitConstructorSignatureDoesNotExist() {
		CommonException exception = assertThrows(CommonException.class, () -> MissingConstructorPojo.builder()
				.name("zero")
				.build());

		assertTrue(exception.getMessage().contains("has no constructor"));
	}

	private static class AutoInferredConstructorPojo {
		private final String name;
		private final Integer id;

		private AutoInferredConstructorPojo(String name, Integer id) {
			this.name = name;
			this.id = id;
		}

		private static Builder builder() {
			return new Builder();
		}

		private static class Builder extends InnerBuilder<AutoInferredConstructorPojo, Builder> {
			private Integer id;
			private String name;

			private Builder id(Integer id) {
				this.id = id;
				return this;
			}

			private Builder name(String name) {
				this.name = name;
				return this;
			}
		}
	}

	private static class GuardedPojo {
		private String name;
		@SuppressWarnings("unused")
		private boolean fallbackUsed;

		private GuardedPojo() {
			this.fallbackUsed = true;
		}

		private GuardedPojo(String name) {
			if (name == null) {
				throw new IllegalStateException("name must not be null");
			}
			this.name = name;
		}

		private static Builder builder() {
			return new Builder();
		}

		public void setName(String name) {
			this.name = name;
		}

		private static class Builder extends InnerBuilder<GuardedPojo, Builder> {
			private String name;

			@Override
			protected Class<?>[] constructorParameterTypes() {
				return new Class<?>[]{String.class};
			}

			@Override
			protected Object[] constructorArguments() {
				return new Object[]{name};
			}
		}
	}

	private static class SetterBackedPojo {
		private String name;
		private int count;

		private SetterBackedPojo() {
		}

		private static Builder builder() {
			return new Builder();
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setCount(int count) {
			this.count = count;
		}

		private static class Builder extends InnerBuilder<SetterBackedPojo, Builder> {
			private String name;
			private int count;

			private Builder name(String name) {
				this.name = name;
				return this;
			}

			private Builder count(int count) {
				this.count = count;
				return this;
			}
		}
	}

	private static class MissingSetterPojo {
		@SuppressWarnings("unused")
		private String name;

		private MissingSetterPojo() {
		}

		private static Builder builder() {
			return new Builder();
		}

		private static class Builder extends InnerBuilder<MissingSetterPojo, Builder> {
			private String name;

			private Builder name(String name) {
				this.name = name;
				return this;
			}
		}
	}

	private static class MetadataMismatchPojo {
		private MetadataMismatchPojo(String name) {
		}

		private static Builder builder() {
			return new Builder();
		}

		private static class Builder extends InnerBuilder<MetadataMismatchPojo, Builder> {
			@Override
			protected Class<?>[] constructorParameterTypes() {
				return new Class<?>[]{String.class};
			}

			@Override
			protected Object[] constructorArguments() {
				return new Object[0];
			}
		}
	}

	private static class MissingConstructorPojo {
		@SuppressWarnings("unused")
		private final String name;

		private MissingConstructorPojo(String name) {
			this.name = name;
		}

		private static Builder builder() {
			return new Builder();
		}

		private static class Builder extends InnerBuilder<MissingConstructorPojo, Builder> {
			private String name;

			private Builder name(String name) {
				this.name = name;
				return this;
			}

			@Override
			protected Class<?>[] constructorParameterTypes() {
				return new Class<?>[]{Long.class};
			}

			@Override
			protected Object[] constructorArguments() {
				return new Object[]{1L};
			}
		}
	}
}
