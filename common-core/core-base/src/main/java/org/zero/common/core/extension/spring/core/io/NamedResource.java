package org.zero.common.core.extension.spring.core.io;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/29
 */
@Getter
@RequiredArgsConstructor
public class NamedResource implements Resource {
    @NonNull
    private final String name;

    @NonNull
    @Delegate
    private final Resource delegate;

    @NonNull
    @Override
    public String getFilename() {
        return name;
    }

    public String getOriginalFilename() {
        return delegate.getFilename();
    }
}
