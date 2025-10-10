package org.zero.common.data.exception;

import lombok.Getter;
import org.zero.common.data.constant.StringPool;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/18
 */
@Getter
public class MultiException extends BaseException {
    protected final Throwable[] throwables;

    public MultiException(Throwable... throwables) {
        this(String.format("MultiException: %s",
                        Arrays.stream(throwables)
                                .map(Throwable::toString)
                                .collect(Collectors.joining(StringPool.SEMICOLON + StringPool.SPACE))),
                throwables);
    }

    public MultiException(String message, Throwable... throwables) {
        super(message);
        this.throwables = throwables;
    }

	@Override
	public void printStackTrace() {
		super.printStackTrace();
	}
}