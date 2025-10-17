package org.zero.common.data.enumeration;

/**
 * 文件类型
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
public interface BaseFileType {
	/**
	 * 获取文件扩展名
	 *
	 * @return 文件扩展名
	 */
	String getExtName();

	/**
	 * 获取文件 MIME 类型
	 *
	 * @return 文件 MIME 类型
	 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Guides/MIME_types/Common_types">常见 MIME 类型列表</a>
	 */
	String getMimeType();

	/**
	 * 获取文件媒体类型
	 *
	 * @return 文件媒体类型
	 */
	default String getMediaType() {
		return this.getMimeType();
	}

	/**
	 * 获取文件内容类型
	 *
	 * @return 文件内容类型
	 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Reference/Headers/Content-Type">Content-Type</a>
	 */
	default String getContentType() {
		return this.getMimeType();
	}
}
