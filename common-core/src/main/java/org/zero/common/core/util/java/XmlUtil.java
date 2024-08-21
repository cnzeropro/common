package org.zero.common.core.util.java;

import lombok.experimental.UtilityClass;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/11/29
 */
@UtilityClass
public class XmlUtil {
    public static Document domRead(InputStream in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // to be compliant, completely disable DOCTYPE declaration:
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        // or completely disable external entities declarations:
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        // or prohibit the use of all protocols by external entities:
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        // or disable entity expansion but keep in mind that this doesn't prevent fetching external entities
        // and this solution is not correct for OpenJDK < 13 due to a bug: https://bugs.openjdk.java.net/browse/JDK-8206132
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(in);
    }

    public static void saxRead(InputStream in, DefaultHandler handler) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // to be compliant, completely disable DOCTYPE declaration:
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        // or completely disable external entities declarations:
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        // or prohibit the use of all protocols by external entities:
        SAXParser parser = factory.newSAXParser();
        parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        parser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        parser.parse(in, handler);
    }

    public static XPath createXPath() {
        XPathFactory factory = XPathFactory.newInstance();
        return factory.newXPath();
    }

    public static XMLInputFactory createXMLInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        // to be compliant, completely disable DOCTYPE declaration:
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        // or completely disable external entities declarations:
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        // or prohibit the use of all protocols by external entities:
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return factory;
    }

    public static XMLOutputFactory createXMLOutputFactory() {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        return factory;
    }


    public static Schema createSchema() throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // to be compliant, completely disable DOCTYPE declaration:
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        // or prohibit the use of all protocols by external entities:
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return factory.newSchema();
    }

    public static Transformer createTransformer() throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        // to be compliant, prohibit the use of all protocols by external entities:
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        return factory.newTransformer();
    }
}
