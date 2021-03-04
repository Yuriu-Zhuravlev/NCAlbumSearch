package com.example.demo.beens.impls;

import com.example.demo.beens.interfaces.ErrorResponse;
import com.example.demo.classes.Album;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class ErrorResponseNothingFoundXml implements ErrorResponse {
    private static final Logger log = Logger.getLogger(ErrorResponseNothingFoundXml.class);
    @Override
    public String createResponse() {
        String res = null;
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element baseRoot = document.createElement("albumFinder");
            document.appendChild(baseRoot);
            Attr attr = document.createAttribute("status");
            attr.setValue("failed");
            baseRoot.setAttributeNode(attr);

            Element error = document.createElement("error");
            error.appendChild(document.createTextNode("Nothing found"));
            baseRoot.appendChild(error);
            Attr attrError = document.createAttribute("code");
            attrError.setValue("6");
            error.setAttributeNode(attrError);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(domSource, streamResult);
            res = writer.getBuffer().toString();
        } catch (ParserConfigurationException e) {
            log.error("Failed to call DocumentBuilder", e);
        } catch (TransformerException e) {
            log.error("Failed to transform xml into StringWriter", e);
        }
        return res;
    }
}
