package org.zero.common.api.dynamic;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.zero.common.data.model.view.Result;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/27
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/mapping")
public class MappingController {
    private final RequestMappingHandlerMapping mapping;

    @PutMapping("/register/{id}")
    public Result<Void> register(@PathVariable Long id) {
        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths("/register").build();
        mapping.registerMapping(requestMappingInfo, null, null);
        return Result.ok();
    }

    @PutMapping("/unregister/{id}")
    public Result<Void> unregister(@PathVariable Long id) {
        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths("/unregister").methods().build();
        mapping.registerMapping(requestMappingInfo, null, null);
        mapping.unregisterMapping(requestMappingInfo);
        return Result.ok();
    }
}
