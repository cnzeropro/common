package org.zero.common.test.feature;

import com.querydsl.core.types.Predicate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.zero.common.data.model.view.Result;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.servlet.http.PushBuilder;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.Principal;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

/**
 * 控制器方法参数解析器支持的类型测试
 * <p>
 * 大部分参数都支持 {@link Optional} 包装，等同于相关注解的 {@code required = false}。
 * 也支持多值包装，如数组、{@link java.util.Collection}及其子类。
 * 此处提及一下，示例不做额外展示。<br>
 * 另外，很多参数解析器虽然都支持 {@link Object} 及其派生类型，但并不是说所有都支持，具体支持情况还得参考注册的转换器
 * （如：{@link org.springframework.format.Formatter}、
 * {@link org.springframework.core.convert.converter.Converter}、
 * {@link org.springframework.core.convert.converter.GenericConverter}、
 * {@link org.springframework.core.convert.converter.ConverterFactory}）等等。
 *
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver
 * @see org.springframework.web.method.support.HandlerMethodArgumentResolverComposite
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#getDefaultArgumentResolvers()
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addArgumentResolvers(java.util.List)
 * @since 2025/5/6
 */
@Slf4j
@RestController
@RequestMapping("/arg")
class MethodArgumentTypeController {
    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.RequestPartMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link MultipartFile} 类
     */
    @PostMapping("/multipartFile")
    public Result<Void> multipartFile(MultipartFile file) {
        return this.result(file);
    }

    /**
     * 参见 {@link MultipartFile} 类
     */
    @PostMapping("/annotatedMultipartFile")
    public Result<Void> annotatedMultipartFile(@RequestPart("file") MultipartFile multipartFile) {
        return this.result(multipartFile);
    }

    /**
     * 参见 {@link Part} 类
     */
    @PostMapping("/part")
    public Result<Void> part(Part file) {
        return this.result(file);
    }

    /**
     * 参见 {@link Part} 类
     */
    @PostMapping("/annotatedPart")
    public Result<Void> annotatedPart(@RequestPart("file") Part part) {
        return this.result(part);
    }

    /**
     * 参见 {@link Object} 及其派生类
     * <p>
     * 如果参数类型为 {@link Object} 的派生类，那必须注解 {@link RequestPart}
     */
    @PostMapping("/requestPart")
    public Result<Void> requestPart(@RequestPart("name") String object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.HttpEntityMethodProcessor ****************************** */

    /**
     * 参见 {@link HttpEntity} 类
     */
    @RequestMapping("/httpEntity")
    public Result<Void> httpEntity(HttpEntity<String> httpEntity) {
        return this.result(httpEntity);
    }

    /**
     * 参见 {@link RequestEntity} 类
     */
    @RequestMapping("/requestEntity")
    public Result<Void> requestEntity(RequestEntity<String> requestEntity) {
        return this.result(requestEntity);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.RequestResponseBodyMethodProcessor ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     * <p>
     * 控制器方法参数必须注解 {@linkplain org.springframework.web.bind.annotation.RequestBody @RequestBody}，并且有且仅有一个
     */
    @RequestMapping("/requestBody")
    public Result<Void> requestBody(@RequestBody TestPojo object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.RequestResponseBodyMethodProcessor ****************************** */

    /**
     * 参见 {@link Cookie} 类
     */
    @RequestMapping("/cookieValueCookie")
    public Result<Void> cookieValueCookie(@CookieValue("test") Cookie cookie) {
        return this.result(cookie);
    }

    /**
     * 参见 {@link Object} 及其派生类
     */
    @RequestMapping("/cookieValueString")
    public Result<Void> cookieValueString(@CookieValue("test") String object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.targetMethod.annotation.ExpressionValueMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     */
    @RequestMapping("/value")
    public Result<Void> value(@Value("${1+1}") Integer object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.MatrixVariableMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     * <p>
     * 注意：<br>
     * 1、使用矩阵变量必须显式声明该路径变量，否则 Spring 会忽略路径中的矩阵变量解析。<br>
     * 2、Spring 默认禁用矩阵变量功能，需手动配置：
     * <pre>{@code
     * @Configuration
     * public class WebConfig implements WebMvcConfigurer {
     *     @Override
     *     public void configurePathMatch(PathMatchConfigurer configurer) {
     *         UrlPathHelper urlPathHelper = new UrlPathHelper();
     *         // 启用矩阵变量
     *         urlPathHelper.setRemoveSemicolonContent(false);
     *         configurer.setUrlPathHelper(urlPathHelper);
     *     }
     * }
     * }</pre>
     * 3、如果参数类型为 {@link Map}及其派生类，矩阵变量又未指定名称，则交由 {@link org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMapMethodArgumentResolver} 处理。
     */
    @RequestMapping("/matrixVariable/{path}")
    public Result<Void> matrixVariable(@PathVariable("path") String unused, @MatrixVariable("test") String object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.MatrixVariableMapMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Map} 及其派生类
     * <p>
     * 与 {@link org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMethodArgumentResolver} 不同的是，
     * 其绑定所有矩阵变量，如果想绑定某个路径变量的所有矩阵变量，则必须指定 {@code pathVar} 属性
     */
    @RequestMapping("/matrixVariableMap/{path}")
    public Result<Void> matrixVariableMap(@PathVariable("path") String unused, @MatrixVariable(pathVar = "path") MultiValueMap<String, Object> map) {
        return this.result(map);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.PathVariableMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     * <p>
     * 如果参数类型为 {@link Map} 及其派生类，则交由 {@link org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver} 处理。
     */
    @RequestMapping("/pathVariable/{test}")
    public Result<Void> pathVariable(@PathVariable("test") String object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.PathVariableMapMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Map} 及其派生类
     */
    @RequestMapping("/pathVariableMap/{test}")
    public Result<Void> pathVariableMap(@PathVariable Map<String, String> map) {
        return this.result(map);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.RequestAttributeMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     */
    @RequestMapping("/requestAttribute")
    public Result<Void> requestAttribute(@RequestAttribute("test") String object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.targetMethod.annotation.RequestHeaderMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     * <p>
     * 如果参数类型为 {@link Map} 及其派生类，则交由 {@link org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver} 处理。
     */
    @RequestMapping("/requestHeader")
    public Result<Void> requestHeader(@RequestHeader("X-Test") String object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.targetMethod.annotation.RequestHeaderMapMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Map} 及其派生类
     */
    @RequestMapping("/requestHeaderMap")
    public Result<Void> requestHeaderMap(@RequestHeader HttpHeaders map) {
        return this.result(map);
    }

    /* ****************************** org.springframework.web.targetMethod.annotation.RequestParamMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     * <p>
     * 如果参数类型为 {@link Map} 及其派生类，则交由 {@link org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver} 处理。<br>
     * 另外也支持 {@link MultipartFile} 与 {@link Part} 的类型和其数组、集合类型，但不能使用 {@linkplain RequestParam @RequestParam} 注解
     */
    @RequestMapping("/requestParam")
    public Result<Void> requestParam(@RequestParam("test") String object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.targetMethod.annotation.RequestParamMapMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Map} 及其派生类
     */
    @RequestMapping("/requestParamMap")
    public Result<Void> requestParamMap(@RequestParam MultiValueMap<String, String> map) {
        return this.result(map);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.SessionAttributeMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     */
    @RequestMapping("/sessionAttribute")
    public Result<Void> sessionAttribute(@SessionAttribute("test") String object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.ServletWebArgumentResolverAdapter ****************************** */

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.ContinuationHandlerMethodArgumentResolver ****************************** */

    /* ****************************** org.springframework.web.targetMethod.annotation.ErrorsMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Errors} 及其派生类
     */
    @RequestMapping("/errors")
    public Result<Void> errors(BindingResult errors) {
        return this.result(errors);
    }

    /* ****************************** org.springframework.web.targetMethod.annotation.MapMethodProcessor ****************************** */

    /**
     * 参见 {@link Map} 及其派生类
     * <p>
     * 注意：1、参数不能有任何注解；2、数据从模型（{@linkplain org.springframework.web.method.support.ModelAndViewContainer#getModel() Model}）获取
     */
    @RequestMapping("/map")
    public Result<Void> map(Map<String, Object> map) {
        return this.result(map);
    }

    /* ****************************** org.springframework.web.targetMethod.annotation.ModelAttributeMethodProcessor ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     */
    @RequestMapping("/modelAttribute")
    public Result<Void> modelAttribute(@ModelAttribute("test") String object) {
        return this.result(object);
    }

    /* ****************************** org.springframework.web.targetMethod.annotation.ModelMethodProcessor ****************************** */

    /**
     * 参见 {@link Model} 及其派生类
     * <p>
     * 注意：数据从模型（{@linkplain org.springframework.web.method.support.ModelAndViewContainer#getModel() Model}）获取
     */
    @RequestMapping("/model")
    public Result<Void> model(Model model) {
        return this.result(model);
    }

    /* ****************************** org.springframework.data.web.PageableHandlerMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Pageable} 类
     */
    @RequestMapping("/pageable")
    public Result<Void> pageable(Pageable pageable) {
        return this.result(pageable);
    }

    /* ****************************** org.springframework.data.web.PagedResourcesAssemblerArgumentResolver ****************************** */

    /**
     * 参见 {@link PagedResourcesAssembler} 类
     */
    @RequestMapping("/pagedResourcesAssembler")
    public Result<Void> pagedResourcesAssembler(PagedResourcesAssembler<String> pageable) {
        return this.result(pageable);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.PrincipalMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Principal} 及其派生类
     */
    @RequestMapping("/principal")
    public Result<Void> principal(Principal principal) {
        return this.result(principal);
    }

    /* ****************************** org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolver ****************************** */

    /**
     * 参见 {@link Predicate} 及其派生类
     */
    @RequestMapping("/predicate")
    public Result<Void> predicate(Predicate predicate) {
        return this.result(predicate);
    }

    /**
     * 参见 {@link Predicate} 及其派生类
     */
    @RequestMapping("/annotatedPredicate")
    public Result<Void> annotatedPredicate(@QuerydslPredicate Predicate predicate) {
        return this.result(predicate);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.RedirectAttributesMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link RedirectAttributes} 及其派生类
     */
    @RequestMapping("/redirectAttributes")
    public Result<Void> redirectAttributes(RedirectAttributes redirectAttributes) {
        return this.result(redirectAttributes);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.ServletRequestMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link WebRequest} 及其派生类
     */
    @RequestMapping("/webRequest")
    public Result<Void> webRequest(WebRequest webRequest) {
        return this.result(webRequest);
    }

    /**
     * 参见 {@link ServletRequest} 及其派生类
     */
    @RequestMapping("/servletRequest")
    public Result<Void> servletRequest(ServletRequest servletRequest) {
        return this.result(servletRequest);
    }

    /**
     * 参见 {@link MultipartRequest} 及其派生类
     */
    @RequestMapping("/multipartRequest")
    public Result<Void> multipartRequest(MultipartRequest multipartRequest) {
        return this.result(multipartRequest);
    }

    /**
     * 参见 {@link HttpSession} 及其派生类
     */
    @RequestMapping("/httpSession")
    public Result<Void> httpSession(HttpSession httpSession) {
        return this.result(httpSession);
    }

    /**
     * 参见 {@link PushBuilder} 及其派生类
     */
    @RequestMapping("/pushBuilder")
    public Result<Void> pushBuilder(PushBuilder pushBuilder) {
        return this.result(pushBuilder);
    }

    /**
     * 参见 {@link InputStream} 及其派生类
     */
    @RequestMapping("/inputStream")
    public Result<Void> inputStream(InputStream inputStream) {
        return this.result(inputStream);
    }

    /**
     * 参见 {@link Reader} 及其派生类
     */
    @RequestMapping("/reader")
    public Result<Void> inputStream(Reader reader) {
        return this.result(reader);
    }

    /**
     * 参见 {@link HttpMethod} 类
     */
    @RequestMapping("/httpMethod")
    public Result<Void> httpMethod(HttpMethod httpMethod) {
        return this.result(httpMethod);
    }

    /**
     * 参见 {@link Locale} 类
     */
    @RequestMapping("/locale")
    public Result<Void> httpMethod(Locale locale) {
        return this.result(locale);
    }

    /**
     * 参见 {@link TimeZone} 类
     */
    @RequestMapping("/timeZone")
    public Result<Void> timeZone(TimeZone timeZone) {
        return this.result(timeZone);
    }

    /**
     * 参见 {@link ZoneId} 类
     */
    @RequestMapping("/zoneId")
    public Result<Void> zoneId(ZoneId zoneId) {
        return this.result(zoneId);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.ServletResponseMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link ServletResponse} 及其派生类
     */
    @RequestMapping("/servletResponse")
    public Result<Void> servletResponse(ServletResponse servletResponse) {
        return this.result(servletResponse);
    }

    /**
     * 参见 {@link OutputStream} 及其派生类
     */
    @RequestMapping("/outputStream")
    public Result<Void> outputStream(OutputStream outputStream) {
        return this.result(outputStream);
    }

    /**
     * 参见 {@link Writer} 及其派生类
     */
    @RequestMapping("/writer")
    public Result<Void> writer(Writer writer) {
        return this.result(writer);
    }

    /* ****************************** org.springframework.web.targetMethod.annotation.SessionStatusMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link SessionStatus} 类
     */
    @RequestMapping("/sessionStatus")
    public Result<Void> sessionStatus(SessionStatus sessionStatus) {
        return this.result(sessionStatus);
    }

    /* ****************************** org.springframework.data.web.SortHandlerMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link Sort} 类
     */
    @RequestMapping("/sort")
    public Result<Void> sort(Sort sort) {
        return this.result(sort);
    }

    /* ****************************** org.springframework.web.servlet.mvc.targetMethod.annotation.UriComponentsBuilderMethodArgumentResolver ****************************** */

    /**
     * 参见 {@link UriComponentsBuilder} 类
     */
    @RequestMapping("/uriComponentsBuilder")
    public Result<Void> uriComponentsBuilder(UriComponentsBuilder uriComponentsBuilder) {
        return this.result(uriComponentsBuilder);
    }

    /**
     * 参见 {@link ServletUriComponentsBuilder} 类
     */
    @RequestMapping("/servletUriComponentsBuilder")
    public Result<Void> servletUriComponentsBuilder(ServletUriComponentsBuilder servletUriComponentsBuilder) {
        return this.result(servletUriComponentsBuilder);
    }

    /* ****************************** other ****************************** */
    private Result<Void> result(Object param) {
        log.info("Param: {}", param);
        return Result.ok();
    }

    @Data
    public static class TestPojo {
        private Long id;
        private String name;
        private String description;
    }
}
