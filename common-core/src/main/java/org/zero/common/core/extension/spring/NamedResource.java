package org.zero.common.core.extension.spring;

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
    private final @NonNull String name;

    private final @NonNull @Delegate Resource delegate;

    @Override
    public String getFilename() {
        return name;
    }

    public String getOriginalFilename() {
        return delegate.getFilename();
    }
}
