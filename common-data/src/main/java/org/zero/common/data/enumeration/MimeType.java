package org.zero.common.data.enumeration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.zero.common.data.constant.StringPool;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * MIME 类型模型，表示 {@code type/subtype} 结构及可选参数。
 *
 * <p>遵循 RFC 6838 规范，格式为 {@code type/subtype[;param=value]}。
 * 提供常见文件类型的预定义常量，与 {@link FileType} 枚举一一对应。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://datatracker.ietf.org/doc/html/rf6838">RFC 6838</a>
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Guides/MIME_types">MIME 类型</a>
 * @since 2025/10/14
 */
@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor
public class MimeType implements Serializable {
	/**
	 * 通配符类型，匹配所有类型。
	 */
	protected static final String WILDCARD_TYPE = StringPool.ASTERISK;
	/* ************************************* Discrete Type ************************************* */
	/**
	 * 离散类型：应用程序特定格式
	 */
	protected static final String APPLICATION_TYPE = "application";
	/**
	 * 离散类型：图片
	 */
	protected static final String IMAGE_TYPE = "image";
	/**
	 * 离散类型：文本
	 */
	protected static final String TEXT_TYPE = "text";
	/**
	 * 离散类型：音频
	 */
	protected static final String AUDIO_TYPE = "audio";
	/**
	 * 离散类型：视频
	 */
	protected static final String VIDEO_TYPE = "video";
	/**
	 * 离散类型：字体
	 */
	protected static final String FONT_TYPE = "font";
	/**
	 * 离散类型：3D 模型
	 */
	protected static final String MODEL_TYPE = "model";
	/**
	 * 离散类型：示例（仅供文档示例使用）
	 */
	protected static final String EXAMPLE_TYPE = "example";
	/* ************************************* Multipart Type ************************************* */
	/**
	 * 复合类型：邮件消息
	 */
	protected static final String MESSAGE_TYPE = "message";
	/**
	 * 复合类型：多部分容器
	 */
	protected static final String MULTIPART_TYPE = "multipart";

	/* ************************************* Predefined MIME Types ************************************* */
	/**
	 * AAC 音频：{@code audio/aac}
	 */
	public static final MimeType AAC = new MimeType(AUDIO_TYPE, "aac");
	/**
	 * AbiWord 文档：{@code application/x-abiword}
	 */
	public static final MimeType ABW = new MimeType(APPLICATION_TYPE, "x-abiword");
	/**
	 * 动画 PNG：{@code image/apng}
	 */
	public static final MimeType APNG = new MimeType(IMAGE_TYPE, "apng");
	/**
	 * ARC 归档：{@code application/x-freearc}
	 */
	public static final MimeType ARC = new MimeType(APPLICATION_TYPE, "x-freearc");
	/**
	 * AVIF 图片：{@code image/avif}
	 */
	public static final MimeType AVIF = new MimeType(IMAGE_TYPE, "avif");
	/**
	 * AVI 视频：{@code video/x-msvideo}
	 */
	public static final MimeType AVI = new MimeType(VIDEO_TYPE, "x-msvideo");
	/**
	 * Amazon Kindle 电子书：{@code application/vnd.amazon.ebook}
	 */
	public static final MimeType AZW = new MimeType(APPLICATION_TYPE, "vnd.amazon.ebook");
	/**
	 * 任意二进制数据：{@code application/octet-stream}
	 */
	public static final MimeType BIN = new MimeType(APPLICATION_TYPE, "octet-stream");
	/**
	 * BMP 位图：{@code image/bmp}
	 */
	public static final MimeType BMP = new MimeType(IMAGE_TYPE, "bmp");
	/**
	 * Bzip 压缩：{@code application/x-bzip}
	 */
	public static final MimeType BZ = new MimeType(APPLICATION_TYPE, "x-bzip");
	/**
	 * Bzip2 压缩：{@code application/x-bzip2}
	 */
	public static final MimeType BZ2 = new MimeType(APPLICATION_TYPE, "x-bzip2");
	/**
	 * CD 音频：{@code application/x-cdf}
	 */
	public static final MimeType CDA = new MimeType(APPLICATION_TYPE, "x-cdf");
	/**
	 * C Shell 脚本：{@code application/x-csh}
	 */
	public static final MimeType CSH = new MimeType(APPLICATION_TYPE, "x-csh");
	/**
	 * CSS 样式表：{@code text/css}
	 */
	public static final MimeType CSS = new MimeType(TEXT_TYPE, "css");
	/**
	 * CSV 逗号分隔值：{@code text/csv}
	 */
	public static final MimeType CSV = new MimeType(TEXT_TYPE, "csv");
	/**
	 * Microsoft Word 文档（旧版）：{@code application/msword}
	 */
	public static final MimeType DOC = new MimeType(APPLICATION_TYPE, "msword");
	/**
	 * Microsoft Word 文档（OOXML）：{@code application/vnd.openxmlformats-officedocument.wordprocessingml.document}
	 */
	public static final MimeType DOCX = new MimeType(APPLICATION_TYPE, "vnd.openxmlformats-officedocument.wordprocessingml.document");
	/**
	 * 嵌入式 OpenType 字体：{@code application/vnd.ms-fontobject}
	 */
	public static final MimeType EOT = new MimeType(APPLICATION_TYPE, "vnd.ms-fontobject");
	/**
	 * EPUB 电子书：{@code application/epub+zip}
	 */
	public static final MimeType EPUB = new MimeType(APPLICATION_TYPE, "epub+zip");
	/**
	 * Gzip 压缩：{@code application/gzip}
	 */
	public static final MimeType GZ = new MimeType(APPLICATION_TYPE, "gzip");
	/**
	 * GIF 图片：{@code image/gif}
	 */
	public static final MimeType GIF = new MimeType(IMAGE_TYPE, "gif");
	/**
	 * HTML 文档：{@code text/html}
	 */
	public static final MimeType HTML = new MimeType(TEXT_TYPE, "html");
	/**
	 * ICO 图标：{@code image/vnd.microsoft.icon}
	 */
	public static final MimeType ICO = new MimeType(IMAGE_TYPE, "vnd.microsoft.icon");
	/**
	 * iCalendar 日历：{@code text/calendar}
	 */
	public static final MimeType ICS = new MimeType(TEXT_TYPE, "calendar");
	/**
	 * Java 归档包：{@code application/java-archive}
	 */
	public static final MimeType JAR = new MimeType(APPLICATION_TYPE, "java-archive");
	/**
	 * JPEG 图片：{@code image/jpeg}
	 */
	public static final MimeType JPEG = new MimeType(IMAGE_TYPE, "jpeg");
	/**
	 * JPG 图片（同 JPEG）：{@code image/jpeg}
	 */
	public static final MimeType JPG = new MimeType(IMAGE_TYPE, "jpeg");
	/**
	 * JavaScript 脚本：{@code text/javascript}
	 */
	public static final MimeType JS = new MimeType(TEXT_TYPE, "javascript");
	/**
	 * JSON 数据：{@code application/json}
	 */
	public static final MimeType JSON = new MimeType(APPLICATION_TYPE, "json");
	/**
	 * JSON-LD 链接数据：{@code application/ld+json}
	 */
	public static final MimeType JSONLD = new MimeType(APPLICATION_TYPE, "ld+json");
	/**
	 * MIDI 音乐（短扩展名）：{@code audio/midi}
	 */
	public static final MimeType MID = new MimeType(AUDIO_TYPE, "midi");
	/**
	 * MIDI 音乐：{@code audio/midi}
	 */
	public static final MimeType MIDI = new MimeType(AUDIO_TYPE, "midi");
	/**
	 * JavaScript 模块（ES Module）：{@code text/javascript}
	 */
	public static final MimeType MJS = new MimeType(TEXT_TYPE, "javascript");
	/**
	 * MP3 音频：{@code audio/mpeg}
	 */
	public static final MimeType MP3 = new MimeType(AUDIO_TYPE, "mpeg");
	/**
	 * MP4 视频：{@code video/mp4}
	 */
	public static final MimeType MP4 = new MimeType(VIDEO_TYPE, "mp4");
	/**
	 * MPEG 视频：{@code video/mpeg}
	 */
	public static final MimeType MPEG = new MimeType(VIDEO_TYPE, "mpeg");
	/**
	 * macOS 安装包：{@code application/vnd.apple.installer+xml}
	 */
	public static final MimeType MPKG = new MimeType(APPLICATION_TYPE, "vnd.apple.installer+xml");
	/**
	 * OpenDocument 演示文稿：{@code application/vnd.oasis.opendocument.presentation}
	 */
	public static final MimeType ODP = new MimeType(APPLICATION_TYPE, "vnd.oasis.opendocument.presentation");
	/**
	 * OpenDocument 电子表格：{@code application/vnd.oasis.opendocument.spreadsheet}
	 */
	public static final MimeType ODS = new MimeType(APPLICATION_TYPE, "vnd.oasis.opendocument.spreadsheet");
	/**
	 * OpenDocument 文本：{@code application/vnd.oasis.opendocument.text}
	 */
	public static final MimeType ODT = new MimeType(APPLICATION_TYPE, "vnd.oasis.opendocument.text");
	/**
	 * Ogg 音频：{@code audio/ogg}
	 */
	public static final MimeType OGA = new MimeType(AUDIO_TYPE, "ogg");
	/**
	 * Ogg 视频：{@code video/ogg}
	 */
	public static final MimeType OGV = new MimeType(VIDEO_TYPE, "ogg");
	/**
	 * Ogg 容器：{@code application/ogg}
	 */
	public static final MimeType OGX = new MimeType(APPLICATION_TYPE, "ogg");
	/**
	 * Opus 音频：{@code audio/opus}
	 */
	public static final MimeType OPUS = new MimeType(AUDIO_TYPE, "opus");
	/**
	 * OpenType 字体：{@code font/otf}
	 */
	public static final MimeType OTF = new MimeType(FONT_TYPE, "otf");
	/**
	 * PNG 图片：{@code image/png}
	 */
	public static final MimeType PNG = new MimeType(IMAGE_TYPE, "png");
	/**
	 * PDF 文档：{@code application/pdf}
	 */
	public static final MimeType PDF = new MimeType(APPLICATION_TYPE, "pdf");
	/**
	 * PHP 脚本：{@code application/x-httpd-php}
	 */
	public static final MimeType PHP = new MimeType(APPLICATION_TYPE, "x-httpd-php");
	/**
	 * Microsoft PowerPoint（旧版）：{@code application/vnd.ms-powerpoint}
	 */
	public static final MimeType PPT = new MimeType(APPLICATION_TYPE, "vnd.ms-powerpoint");
	/**
	 * Microsoft PowerPoint（OOXML）：{@code application/vnd.openxmlformats-officedocument.presentationml.presentation}
	 */
	public static final MimeType PPTX = new MimeType(APPLICATION_TYPE, "vnd.openxmlformats-officedocument.presentationml.presentation");
	/**
	 * RAR 压缩包：{@code application/x-rar-compressed}
	 */
	public static final MimeType RAR = new MimeType(APPLICATION_TYPE, "x-rar-compressed");
	/**
	 * 富文本格式：{@code application/rtf}
	 */
	public static final MimeType RTF = new MimeType(APPLICATION_TYPE, "rtf");
	/**
	 * Shell 脚本：{@code application/x-sh}
	 */
	public static final MimeType SH = new MimeType(APPLICATION_TYPE, "x-sh");
	/**
	 * SVG 矢量图：{@code image/svg+xml}
	 */
	public static final MimeType SVG = new MimeType(IMAGE_TYPE, "svg+xml");
	/**
	 * Shockwave Flash：{@code application/x-shockwave-flash}
	 */
	public static final MimeType SWF = new MimeType(APPLICATION_TYPE, "x-shockwave-flash");
	/**
	 * Tar 归档包：{@code application/x-tar}
	 */
	public static final MimeType TAR = new MimeType(APPLICATION_TYPE, "x-tar");
	/**
	 * TIFF 图片（短扩展名）：{@code image/tiff}
	 */
	public static final MimeType TIF = new MimeType(IMAGE_TYPE, "tiff");
	/**
	 * TIFF 图片：{@code image/tiff}
	 */
	public static final MimeType TIFF = new MimeType(IMAGE_TYPE, "tiff");
	/**
	 * MPEG-TS 传输流：{@code video/mp2t}
	 */
	public static final MimeType TS = new MimeType(VIDEO_TYPE, "mp2t");
	/**
	 * TrueType 字体：{@code font/ttf}
	 */
	public static final MimeType TTF = new MimeType(FONT_TYPE, "ttf");
	/**
	 * 纯文本：{@code text/plain}
	 */
	public static final MimeType TXT = new MimeType(TEXT_TYPE, "plain");
	/**
	 * Microsoft Visio 绘图：{@code application/vnd.visio}
	 */
	public static final MimeType VSD = new MimeType(APPLICATION_TYPE, "vnd.visio");
	/**
	 * WAV 音频：{@code audio/wav}
	 */
	public static final MimeType WAV = new MimeType(AUDIO_TYPE, "wav");
	/**
	 * WebM 音频：{@code audio/webm}
	 */
	public static final MimeType WEBA = new MimeType(AUDIO_TYPE, "webm");
	/**
	 * WebM 视频：{@code video/webm}
	 */
	public static final MimeType WEBM = new MimeType(VIDEO_TYPE, "webm");
	/**
	 * WebP 图片：{@code image/webp}
	 */
	public static final MimeType WEBP = new MimeType(IMAGE_TYPE, "webp");
	/**
	 * WOFF 字体：{@code font/woff}
	 */
	public static final MimeType WOFF = new MimeType(FONT_TYPE, "woff");
	/**
	 * WOFF2 字体：{@code font/woff2}
	 */
	public static final MimeType WOFF2 = new MimeType(FONT_TYPE, "woff2");
	/**
	 * XHTML 文档：{@code application/xhtml+xml}
	 */
	public static final MimeType XHTML = new MimeType(APPLICATION_TYPE, "xhtml+xml");
	/**
	 * Microsoft Excel（旧版）：{@code application/vnd.ms-excel}
	 */
	public static final MimeType XLS = new MimeType(APPLICATION_TYPE, "vnd.ms-excel");
	/**
	 * Microsoft Excel（OOXML）：{@code application/vnd.openxmlformats-officedocument.spreadsheetml.sheet}
	 */
	public static final MimeType XLSX = new MimeType(APPLICATION_TYPE, "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	/**
	 * XML 文档：{@code application/xml}
	 */
	public static final MimeType XML = new MimeType(APPLICATION_TYPE, "xml");
	/**
	 * XUL 文档：{@code application/xul+xml}
	 */
	public static final MimeType XUL = new MimeType(APPLICATION_TYPE, "xul+xml");
	/**
	 * ZIP 压缩包：{@code application/zip}
	 */
	public static final MimeType ZIP = new MimeType(APPLICATION_TYPE, "zip");
	/**
	 * 3GPP 音视频：{@code video/3gpp}
	 */
	public static final MimeType THREE_GP = new MimeType(VIDEO_TYPE, "3gpp");
	/**
	 * 3GPP2 音视频：{@code video/3gpp2}
	 */
	public static final MimeType THREE_G2 = new MimeType(VIDEO_TYPE, "3gpp2");
	/**
	 * 7-Zip 压缩包：{@code application/x-7z-compressed}
	 */
	public static final MimeType SEVEN_Z = new MimeType(APPLICATION_TYPE, "x-7z-compressed");

	/**
	 * 主类型，如 {@code "application"}、{@code "image"}、{@code "text"} 等。
	 */
	private final String type;
	/**
	 * 子类型，如 {@code "json"}、{@code "png"}、{@code "html"} 等。
	 */
	private final String subtype;
	/**
	 * 可选参数映射，如 {@code charset=UTF-8}。
	 */
	@Singular
	private final Map<String, String> parameters;

	/**
	 * 仅指定主类型的构造方法，子类型默认为通配符 {@code "*"}。
	 *
	 * @param type 主类型
	 */
	public MimeType(String type) {
		this(type, WILDCARD_TYPE);
	}

	/**
	 * 指定主类型和子类型的构造方法，参数默认为空。
	 *
	 * @param type    主类型
	 * @param subtype 子类型
	 */
	public MimeType(String type, String subtype) {
		this(type, subtype, Collections.emptyMap());
	}

	/**
	 * 根据参数名获取参数值。
	 *
	 * @param name 参数名
	 * @return 参数值，不存在则返回 {@code null}
	 */
	public String getParameter(String name) {
		return this.getParameters().get(name);
	}

	/**
	 * 将 MIME 类型格式化为字符串，格式为 {@code type/subtype[;param=value]}。
	 *
	 * @return 格式化后的 MIME 类型字符串
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getType());
		builder.append('/');
		builder.append(this.getSubtype());
		this.getParameters().forEach((key, val) -> {
			builder.append(';');
			builder.append(key);
			builder.append('=');
			builder.append(val);
		});
		return builder.toString();
	}
}
