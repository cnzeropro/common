package org.zero.common.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示类型所需依赖（坐标/版本声明）。
 * <p>
 * 该注解主要用于<strong>文档化</strong>与<strong>静态分析/工具扫描</strong>：在阅读源码或生成文档时，明确某个类型为了正常工作需要引入哪些三方库。
 * <p>
 * 注意：{@link RetentionPolicy#CLASS} 表示注解会被编译进 class 文件，但默认不会在运行期通过反射读取；也不会自动替你拉取依赖。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Repeatable(NeedLibs.class)
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface NeedLib {
    /**
     * 组 ID（Maven coordinates 中的 groupId）。
     * <p>
     * 当 {@link #groupId()} / {@link #version()} 无法确定时，可使用 {@link #UNKNOWN} 作为占位。
     */
    String groupId() default UNKNOWN;

    /**
     * 工件 ID（Maven coordinates 中的 artifactId）。
     */
    String artifactId();

    /**
     * 版本（Maven/Gradle 常见版本表达）。
     * <p>
     * 支持版本范围（Version Ranges）语法：
     * <ol>
     *     <li>固定版本号
     *     <table>
     *        <tr>
     *            <th>示例</th>
     *            <th>说明</th>
     *        </tr>
     *        <tr>
     *            <td>1.2.3</td>
     *            <td>选择固定的 1.2.3 版本</td>
     *        </tr>
     *     </table>
     *     </li>
     *     <li>动态版本号
     *     <table>
     *        <tr>
     *            <th>示例</th>
     *            <th>说明</th>
     *        </tr>
     *        <tr>
     *            <td>1.2.*</td>
     *            <td>选择 1.2.x 的最新版本</td>
     *        </tr>
     *        <tr>
     *            <td>1.2.3+</td>
     *            <td>选择 1.2.3 及其后续小版本（如 1.2.4）</td>
     *        </tr>
     *     </table>
     *     </li>
     *     <li>版本区间范围
     *     <table>
     *        <tr>
     *            <th>示例</th>
     *            <th>说明</th>
     *        </tr>
     *        <tr>
     *            <td>[1.0.0, 2.0.0)</td>
     *            <td>选择大于等于 1.0.0 且小于 2.0.0 的版本</td>
     *        </tr>
     *        <tr>
     *            <td>(,2.0.0]</td>
     *            <td>选择小于等于 2.0.0 的版本</td>
     *        </tr>
     *        <tr>
     *            <td>(,2.0.0],[1.2.3,)</td>
     *            <td>选择小于等于 2.0.0 或大于等于 1.2.3 的版本</td>
     *        </tr>
     *        <tr>
     *            <td>(,1.2),(1.2,)</td>
     *            <td>排除 1.2.x 的版本</td>
     *        </tr>
     *     </table>
     *     </li>
     * </ol>
     */
    String version() default UNKNOWN;

    /**
     * 未知占位（不确认，建议自行尝试）。
     */
    String UNKNOWN = "unknown";
}
