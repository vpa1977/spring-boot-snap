/*
 * This file is part of Spring Boot snap.
 *
 * Copyright 2024 Canonical Ltd.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 3, as published by the
 * Free Software Foundation.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 3, as published by the
 * Free Software Foundation.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.canonical.springboot.configure;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/*
 * This class represents ~/.m2/settings.xml file It will create a new one if the
 * existing file is not found
 */
public class Settings {

    private static final String SPRING_BOOT_PROFILE =
    """
        <profile>
        <id>spring-boot-snap</id>
        <activation>
          <activeByDefault>true</activeByDefault>
        </activation>
        <repositories>
          <repository>
            <id>local-spring-boot</id>
            <name>Spring Boot</name>
            <snapshots>
              <enabled>true</enabled>
            </snapshots>
            <releases>
              <enabled>true</enabled>
              <updatePolicy>always</updatePolicy>
            </releases>
            <url>file:///snap/spring-boot/current/maven-repo/</url>
            <layout>default</layout>
          </repository>
        </repositories>
        <pluginRepositories>
          <pluginRepository>
            <id>spring-boot-plugins</id>
            <name>Spring Boot plugins</name>
            <releases>
              <enabled>true</enabled>
            </releases>
            <url>file:///snap/spring-boot/current/maven-repo/</url>
          </pluginRepository>
        </pluginRepositories>
      </profile>
            """;

    private Document m_settings;
    private DocumentBuilder m_builder;

    public Settings(File settingsDir) throws ParserConfigurationException, SAXException, IOException {
        m_builder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        if (!settingsDir.exists() && !settingsDir.mkdirs())
            throw new IOException("Unable to create the settings directory");
        File settings = new File(settingsDir, "settings.xml");
        if (!settings.exists()) {

            m_settings = m_builder.newDocument();
            Element root = m_settings.createElement("settings");
            m_settings.appendChild(root);
            root.setAttribute("xmlns", "http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xsi:schemaLocation",
                    "http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd");
            root.appendChild(m_settings.createElement("profiles"));
        } else {
            String content = Files.readString(settings.toPath());
            content = content.replaceAll(">[\\s\r\n]*<", "><");
            m_settings = m_builder.parse(new ByteArrayInputStream(content.getBytes()));
            //
            if (!"settings".equals(m_settings.getDocumentElement().getNodeName())) {
                throw new RuntimeException(
                        "settings.xml should have <settings/> as a root element");
            }
        }
    }

    public String toXml() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        StringWriter wr = new StringWriter();
        transformer.transform(new DOMSource(m_settings), new StreamResult(wr));
        return wr.toString();
    }

    public boolean addSpringBootProfile() throws XPathExpressionException,  IllegalArgumentException, SAXException, IOException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        String existingProfile = "/settings/profiles/profile/id[. ='spring-boot-snap']";
        String expression = "/settings/profiles";
        Node snapProfile =
                (Node) xpath.evaluate(existingProfile, m_settings, XPathConstants.NODE);
        if (snapProfile != null) {
            return false;
        }
        Node profilesNode = (Node) xpath.evaluate(expression, m_settings, XPathConstants.NODE);
        if (profilesNode == null) {
            throw new IllegalArgumentException("profiles node is not found");
        }
        Element fragment = m_builder.parse(new ByteArrayInputStream(SPRING_BOOT_PROFILE.getBytes())).getDocumentElement();
        profilesNode.appendChild(m_settings.adoptNode(fragment));
        return true;
    }
}
