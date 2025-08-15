package org.zero.common.core.util.java;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/8/4
 */
public class MavenToGradleBuilder {
	protected File pomPath;
	protected File libsPath;


	public void pomToLibs() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(pomPath);
	}
}
