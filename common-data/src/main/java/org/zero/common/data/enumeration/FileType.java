package org.zero.common.data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/14
 */
@RequiredArgsConstructor
public enum FileType implements BaseFileType {
	AAC("aac", MimeType.AAC),
	ABW("abw", MimeType.ABW),
	APNG("apng", MimeType.APNG),
	ARC("arc", MimeType.ARC),
	AVIF("avif", MimeType.AVIF),
	AVI("avi", MimeType.AVI),
	AZW("azw", MimeType.AZW),
	BIN("bin", MimeType.BIN),
	BMP("bmp", MimeType.BMP),
	BZ("bz", MimeType.BZ),
	BZ2("bz2", MimeType.BZ2),
	CDA("cda", MimeType.CDA),
	CSH("csh", MimeType.CSH),
	CSS("css", MimeType.CSS),
	CSV("csv", MimeType.CSV),
	DOC("doc", MimeType.DOC),
	DOCX("docx", MimeType.DOCX),
	EOT("eot", MimeType.EOT),
	EPUB("epub", MimeType.EPUB),
	GZ("gz", MimeType.GZ),
	GIF("gif", MimeType.GIF),
	HTML("html", MimeType.HTML),
	ICO("ico", MimeType.ICO),
	ICS("ics", MimeType.ICS),
	JAR("jar", MimeType.JAR),
	JPEG("jpeg", MimeType.JPEG),
	JPG("jpg", MimeType.JPG),
	JS("js", MimeType.JS),
	JSON("json", MimeType.JSON),
	JSONLD("jsonld", MimeType.JSONLD),
	MID("mid", MimeType.MID),
	MIDI("midi", MimeType.MIDI),
	MJS("mjs", MimeType.MJS),
	MP3("mp3", MimeType.MP3),
	MP4("mp4", MimeType.MP4),
	MPEG("mpeg", MimeType.MPEG),
	MPKG("mpkg", MimeType.MPKG),
	ODP("odp", MimeType.ODP),
	ODS("ods", MimeType.ODS),
	ODT("odt", MimeType.ODT),
	OGA("oga", MimeType.OGA),
	OGV("ogv", MimeType.OGV),
	OGX("ogx", MimeType.OGX),
	OPUS("opus", MimeType.OPUS),
	OTF("otf", MimeType.OTF),
	PNG("png", MimeType.PNG),
	PDF("pdf", MimeType.PDF),
	PHP("php", MimeType.PHP),
	PPT("ppt", MimeType.PPT),
	PPTX("pptx", MimeType.PPTX),
	RAR("rar", MimeType.RAR),
	RTF("rtf", MimeType.RTF),
	SH("sh", MimeType.SH),
	SVG("svg", MimeType.SVG),
	SWF("swf", MimeType.SWF),
	TAR("tar", MimeType.TAR),
	TIF("tif", MimeType.TIF),
	TIFF("tiff", MimeType.TIFF),
	TS("ts", MimeType.TS),
	TTF("ttf", MimeType.TTF),
	TXT("txt", MimeType.TXT),
	VSD("vsd", MimeType.VSD),
	WAV("wav", MimeType.WAV),
	WEBA("weba", MimeType.WEBA),
	WEBM("webm", MimeType.WEBM),
	WEBP("webp", MimeType.WEBP),
	WOFF("woff", MimeType.WOFF),
	WOFF2("woff2", MimeType.WOFF2),
	XHTML("xhtml", MimeType.XHTML),
	XLS("xls", MimeType.XLS),
	XLSX("xlsx", MimeType.XLSX),
	XML("xml", MimeType.XML),
	XUL("xul", MimeType.XUL),
	ZIP("zip", MimeType.ZIP),
	THREE_GP("3gp", MimeType.THREE_GP),
	THREE_G2("3g2", MimeType.THREE_G2),
	SEVEN_Z("7z", MimeType.SEVEN_Z),
	;

	@Getter
	private final String extName;
	private final MimeType mediaType;

	@Override
	public String getMimeType() {
		return mediaType.toString();
	}
}
