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
	protected static final String WILDCARD_TYPE = StringPool.ASTERISK;
	/* ************************************* Discrete Type ************************************* */
	protected static final String APPLICATION_TYPE = "application";
	protected static final String IMAGE_TYPE = "image";
	protected static final String TEXT_TYPE = "text";
	protected static final String AUDIO_TYPE = "audio";
	protected static final String VIDEO_TYPE = "video";
	protected static final String FONT_TYPE = "font";
	protected static final String MODEL_TYPE = "model";
	protected static final String EXAMPLE_TYPE = "example";
	/* ************************************* Multipart Type ************************************* */
	protected static final String MESSAGE_TYPE = "message";
	protected static final String MULTIPART_TYPE = "multipart";

	/* ************************************* Types ************************************* */
	public static final MimeType AAC = new MimeType(AUDIO_TYPE, "aac");
	public static final MimeType ABW = new MimeType(APPLICATION_TYPE, "x-abiword");
	public static final MimeType APNG = new MimeType(IMAGE_TYPE, "apng");
	public static final MimeType ARC = new MimeType(APPLICATION_TYPE, "x-freearc");
	public static final MimeType AVIF = new MimeType(IMAGE_TYPE, "avif");
	public static final MimeType AVI = new MimeType(VIDEO_TYPE, "x-msvideo");
	public static final MimeType AZW = new MimeType(APPLICATION_TYPE, "vnd.amazon.ebook");
	public static final MimeType BIN = new MimeType(APPLICATION_TYPE, "octet-stream");
	public static final MimeType BMP = new MimeType(IMAGE_TYPE, "bmp");
	public static final MimeType BZ = new MimeType(APPLICATION_TYPE, "x-bzip");
	public static final MimeType BZ2 = new MimeType(APPLICATION_TYPE, "x-bzip2");
	public static final MimeType CDA = new MimeType(APPLICATION_TYPE, "x-cdf");
	public static final MimeType CSH = new MimeType(APPLICATION_TYPE, "x-csh");
	public static final MimeType CSS = new MimeType(TEXT_TYPE, "css");
	public static final MimeType CSV = new MimeType(TEXT_TYPE, "csv");
	public static final MimeType DOC = new MimeType(APPLICATION_TYPE, "msword");
	public static final MimeType DOCX = new MimeType(APPLICATION_TYPE, "vnd.openxmlformats-officedocument.wordprocessingml.document");
	public static final MimeType EOT = new MimeType(APPLICATION_TYPE, "vnd.ms-fontobject");
	public static final MimeType EPUB = new MimeType(APPLICATION_TYPE, "epub+zip");
	public static final MimeType GZ = new MimeType(APPLICATION_TYPE, "gzip");
	public static final MimeType GIF = new MimeType(IMAGE_TYPE, "gif");
	public static final MimeType HTML = new MimeType(TEXT_TYPE, "html");
	public static final MimeType ICO = new MimeType(IMAGE_TYPE, "vnd.microsoft.icon");
	public static final MimeType ICS = new MimeType(TEXT_TYPE, "calendar");
	public static final MimeType JAR = new MimeType(APPLICATION_TYPE, "java-archive");
	public static final MimeType JPEG = new MimeType(IMAGE_TYPE, "jpeg");
	public static final MimeType JPG = new MimeType(IMAGE_TYPE, "jpeg");
	public static final MimeType JS = new MimeType(TEXT_TYPE, "javascript");
	public static final MimeType JSON = new MimeType(APPLICATION_TYPE, "json");
	public static final MimeType JSONLD = new MimeType(APPLICATION_TYPE, "ld+json");
	public static final MimeType MID = new MimeType(AUDIO_TYPE, "midi");
	public static final MimeType MIDI = new MimeType(AUDIO_TYPE, "midi");
	public static final MimeType MJS = new MimeType(TEXT_TYPE, "javascript");
	public static final MimeType MP3 = new MimeType(AUDIO_TYPE, "mpeg");
	public static final MimeType MP4 = new MimeType(VIDEO_TYPE, "mp4");
	public static final MimeType MPEG = new MimeType(VIDEO_TYPE, "mpeg");
	public static final MimeType MPKG = new MimeType(APPLICATION_TYPE, "vnd.apple.installer+xml");
	public static final MimeType ODP = new MimeType(APPLICATION_TYPE, "vnd.oasis.opendocument.presentation");
	public static final MimeType ODS = new MimeType(APPLICATION_TYPE, "vnd.oasis.opendocument.spreadsheet");
	public static final MimeType ODT = new MimeType(APPLICATION_TYPE, "vnd.oasis.opendocument.text");
	public static final MimeType OGA = new MimeType(AUDIO_TYPE, "ogg");
	public static final MimeType OGV = new MimeType(VIDEO_TYPE, "ogg");
	public static final MimeType OGX = new MimeType(APPLICATION_TYPE, "ogg");
	public static final MimeType OPUS = new MimeType(AUDIO_TYPE, "opus");
	public static final MimeType OTF = new MimeType(FONT_TYPE, "otf");
	public static final MimeType PNG = new MimeType(IMAGE_TYPE, "png");
	public static final MimeType PDF = new MimeType(APPLICATION_TYPE, "pdf");
	public static final MimeType PHP = new MimeType(APPLICATION_TYPE, "x-httpd-php");
	public static final MimeType PPT = new MimeType(APPLICATION_TYPE, "vnd.ms-powerpoint");
	public static final MimeType PPTX = new MimeType(APPLICATION_TYPE, "vnd.openxmlformats-officedocument.presentationml.presentation");
	public static final MimeType RAR = new MimeType(APPLICATION_TYPE, "x-rar-compressed");
	public static final MimeType RTF = new MimeType(APPLICATION_TYPE, "rtf");
	public static final MimeType SH = new MimeType(APPLICATION_TYPE, "x-sh");
	public static final MimeType SVG = new MimeType(IMAGE_TYPE, "svg+xml");
	public static final MimeType SWF = new MimeType(APPLICATION_TYPE, "x-shockwave-flash");
	public static final MimeType TAR = new MimeType(APPLICATION_TYPE, "x-tar");
	public static final MimeType TIF = new MimeType(IMAGE_TYPE, "tiff");
	public static final MimeType TIFF = new MimeType(IMAGE_TYPE, "tiff");
	public static final MimeType TS = new MimeType(VIDEO_TYPE, "mp2t");
	public static final MimeType TTF = new MimeType(FONT_TYPE, "ttf");
	public static final MimeType TXT = new MimeType(TEXT_TYPE, "plain");
	public static final MimeType VSD = new MimeType(APPLICATION_TYPE, "vnd.visio");
	public static final MimeType WAV = new MimeType(AUDIO_TYPE, "wav");
	public static final MimeType WEBA = new MimeType(AUDIO_TYPE, "webm");
	public static final MimeType WEBM = new MimeType(VIDEO_TYPE, "webm");
	public static final MimeType WEBP = new MimeType(IMAGE_TYPE, "webp");
	public static final MimeType WOFF = new MimeType(FONT_TYPE, "woff");
	public static final MimeType WOFF2 = new MimeType(FONT_TYPE, "woff2");
	public static final MimeType XHTML = new MimeType(APPLICATION_TYPE, "xhtml+xml");
	public static final MimeType XLS = new MimeType(APPLICATION_TYPE, "vnd.ms-excel");
	public static final MimeType XLSX = new MimeType(APPLICATION_TYPE, "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	public static final MimeType XML = new MimeType(APPLICATION_TYPE, "xml");
	public static final MimeType XUL = new MimeType(APPLICATION_TYPE, "xul+xml");
	public static final MimeType ZIP = new MimeType(APPLICATION_TYPE, "zip");
	public static final MimeType THREE_GP = new MimeType(VIDEO_TYPE, "3gpp");
	public static final MimeType THREE_G2 = new MimeType(VIDEO_TYPE, "3gpp2");
	public static final MimeType SEVEN_Z = new MimeType(APPLICATION_TYPE, "x-7z-compressed");


	private final String type;
	private final String subtype;
	@Singular
	private final Map<String, String> parameters;

	public MimeType(String type) {
		this(type, WILDCARD_TYPE);
	}

	public MimeType(String type, String subtype) {
		this(type, subtype, Collections.emptyMap());
	}

	public String getParameter(String name) {
		return this.getParameters().get(name);
	}

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
