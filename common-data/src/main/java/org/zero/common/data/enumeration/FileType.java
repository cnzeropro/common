package org.zero.common.data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 文件类型枚举，定义常见文件扩展名与对应的 MIME 类型映射。
 *
 * <p>每个枚举项持有文件扩展名（不含点号）和 {@link MimeType}，
 * 通过 {@link BaseFileType} 接口统一提供 MIME 类型字符串。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/14
 */
@RequiredArgsConstructor
public enum FileType implements BaseFileType {
	/** AAC 音频 */
	AAC("aac", MimeType.AAC),
	/** AbiWord 文档 */
	ABW("abw", MimeType.ABW),
	/** 动画 PNG 图片 */
	APNG("apng", MimeType.APNG),
	/** ARC 压缩包 */
	ARC("arc", MimeType.ARC),
	/** AVIF 图片 */
	AVIF("avif", MimeType.AVIF),
	/** AVI 视频 */
	AVI("avi", MimeType.AVI),
	/** Amazon Kindle 电子书 */
	AZW("azw", MimeType.AZW),
	/** 二进制文件 */
	BIN("bin", MimeType.BIN),
	/** BMP 位图 */
	BMP("bmp", MimeType.BMP),
	/** Bzip 压缩包 */
	BZ("bz", MimeType.BZ),
	/** Bzip2 压缩包 */
	BZ2("bz2", MimeType.BZ2),
	/** CD 音频快捷方式 */
	CDA("cda", MimeType.CDA),
	/** C Shell 脚本 */
	CSH("csh", MimeType.CSH),
	/** CSS 样式表 */
	CSS("css", MimeType.CSS),
	/** CSV 逗号分隔值文件 */
	CSV("csv", MimeType.CSV),
	/** Microsoft Word 文档（旧版） */
	DOC("doc", MimeType.DOC),
	/** Microsoft Word 文档（OOXML） */
	DOCX("docx", MimeType.DOCX),
	/** 嵌入式 OpenType 字体 */
	EOT("eot", MimeType.EOT),
	/** EPUB 电子书 */
	EPUB("epub", MimeType.EPUB),
	/** Gzip 压缩包 */
	GZ("gz", MimeType.GZ),
	/** GIF 图片 */
	GIF("gif", MimeType.GIF),
	/** HTML 超文本标记语言 */
	HTML("html", MimeType.HTML),
	/** ICO 图标 */
	ICO("ico", MimeType.ICO),
	/** iCalendar 日历文件 */
	ICS("ics", MimeType.ICS),
	/** Java 归档包 */
	JAR("jar", MimeType.JAR),
	/** JPEG 图片 */
	JPEG("jpeg", MimeType.JPEG),
	/** JPG 图片 */
	JPG("jpg", MimeType.JPG),
	/** JavaScript 脚本 */
	JS("js", MimeType.JS),
	/** JSON 数据 */
	JSON("json", MimeType.JSON),
	/** JSON-LD 链接数据 */
	JSONLD("jsonld", MimeType.JSONLD),
	/** MIDI 音乐（单扩展名） */
	MID("mid", MimeType.MID),
	/** MIDI 音乐 */
	MIDI("midi", MimeType.MIDI),
	/** JavaScript 模块（ES Module） */
	MJS("mjs", MimeType.MJS),
	/** MP3 音频 */
	MP3("mp3", MimeType.MP3),
	/** MP4 视频 */
	MP4("mp4", MimeType.MP4),
	/** MPEG 视频 */
	MPEG("mpeg", MimeType.MPEG),
	/** macOS 安装包 */
	MPKG("mpkg", MimeType.MPKG),
	/** OpenDocument 演示文稿 */
	ODP("odp", MimeType.ODP),
	/** OpenDocument 电子表格 */
	ODS("ods", MimeType.ODS),
	/** OpenDocument 文本 */
	ODT("odt", MimeType.ODT),
	/** Ogg 音频 */
	OGA("oga", MimeType.OGA),
	/** Ogg 视频 */
	OGV("ogv", MimeType.OGV),
	/** Ogg 容器 */
	OGX("ogx", MimeType.OGX),
	/** Opus 音频 */
	OPUS("opus", MimeType.OPUS),
	/** OpenType 字体 */
	OTF("otf", MimeType.OTF),
	/** PNG 图片 */
	PNG("png", MimeType.PNG),
	/** PDF 文档 */
	PDF("pdf", MimeType.PDF),
	/** PHP 脚本 */
	PHP("php", MimeType.PHP),
	/** Microsoft PowerPoint 演示文稿（旧版） */
	PPT("ppt", MimeType.PPT),
	/** Microsoft PowerPoint 演示文稿（OOXML） */
	PPTX("pptx", MimeType.PPTX),
	/** RAR 压缩包 */
	RAR("rar", MimeType.RAR),
	/** 富文本格式 */
	RTF("rtf", MimeType.RTF),
	/** Shell 脚本 */
	SH("sh", MimeType.SH),
	/** SVG 矢量图 */
	SVG("svg", MimeType.SVG),
	/** Shockwave Flash */
	SWF("swf", MimeType.SWF),
	/** Tar 归档包 */
	TAR("tar", MimeType.TAR),
	/** TIF 图片（短扩展名） */
	TIF("tif", MimeType.TIF),
	/** TIFF 图片 */
	TIFF("tiff", MimeType.TIFF),
	/** TypeScript / MPEG-TS 传输流 */
	TS("ts", MimeType.TS),
	/** TrueType 字体 */
	TTF("ttf", MimeType.TTF),
	/** 纯文本 */
	TXT("txt", MimeType.TXT),
	/** Microsoft Visio 绘图 */
	VSD("vsd", MimeType.VSD),
	/** WAV 音频 */
	WAV("wav", MimeType.WAV),
	/** WebM 音频 */
	WEBA("weba", MimeType.WEBA),
	/** WebM 视频 */
	WEBM("webm", MimeType.WEBM),
	/** WebP 图片 */
	WEBP("webp", MimeType.WEBP),
	/** WOFF 字体 */
	WOFF("woff", MimeType.WOFF),
	/** WOFF2 字体 */
	WOFF2("woff2", MimeType.WOFF2),
	/** XHTML 文档 */
	XHTML("xhtml", MimeType.XHTML),
	/** Microsoft Excel 电子表格（旧版） */
	XLS("xls", MimeType.XLS),
	/** Microsoft Excel 电子表格（OOXML） */
	XLSX("xlsx", MimeType.XLSX),
	/** XML 文档 */
	XML("xml", MimeType.XML),
	/** XUL 文档 */
	XUL("xul", MimeType.XUL),
	/** ZIP 压缩包 */
	ZIP("zip", MimeType.ZIP),
	/** 3GPP 音视频 */
	THREE_GP("3gp", MimeType.THREE_GP),
	/** 3GPP2 音视频 */
	THREE_G2("3g2", MimeType.THREE_G2),
	/** 7-Zip 压缩包 */
	SEVEN_Z("7z", MimeType.SEVEN_Z),
	;

	/** 文件扩展名（不含前导点号），如 {@code "png"}、{@code "pdf"}。 */
	@Getter
	private final String extName;

	/** 对应的 MIME 类型。 */
	private final MimeType mediaType;

	/**
	 * 获取该文件类型的 MIME 类型字符串。
	 *
	 * @return MIME 类型字符串，如 {@code "image/png"}
	 */
	@Override
	public String getMimeType() {
		return mediaType.toString();
	}
}
