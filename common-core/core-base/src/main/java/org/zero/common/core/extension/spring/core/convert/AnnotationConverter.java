package org.zero.common.core.extension.spring.core.convert;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.lang.annotation.Annotation;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/28
 */
@RequiredArgsConstructor
public abstract class AnnotationConverter<A extends Annotation> implements ConditionalGenericConverter {
	protected final Class<A> annotationType;

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return sourceType.hasAnnotation(this.annotationType) || targetType.hasAnnotation(this.annotationType);
	}

	protected A getAnnotation(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return sourceType.hasAnnotation(this.annotationType) ? sourceType.getAnnotation(this.annotationType) : targetType.getAnnotation(this.annotationType);
	}
}
