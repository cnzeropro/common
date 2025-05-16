package org.zero.common.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示所需依赖
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
     * 组 ID
     */
    String groupId() default UNKNOWN;

    /**
     * 工件 ID
     */
    String artifactId();

    /**
     * 版本
     * <p>
     * 版本范围（Version Ranges）语法
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
     * 未知（不确认，建议自行尝试）
     */
    String UNKNOWN = "unknown";
}
