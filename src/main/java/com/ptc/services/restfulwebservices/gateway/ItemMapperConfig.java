/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:16CEST $
 */
package com.ptc.services.restfulwebservices.gateway;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author veckardt
 */
public class ItemMapperConfig {

    private static final String configFilename = "../data/gateway/gateway-tool-configuration.xml";

    Properties properties = new Properties();
    String configName = "";

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        // for testing only!
        new ItemMapperConfig("Audit Trail Report");
    }

    public String getConfigName() {
        return configName;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    public String getProperty(String name, String value) {
        return properties.getProperty(name, value);
    }

    public ItemMapperConfig(String configName) throws ParserConfigurationException, SAXException, IOException {
        this.configName = configName;
        File fXmlFile = new File(configFilename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        NodeList nList = doc.getElementsByTagName("export-config");
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            // System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                if (eElement.getElementsByTagName("name").item(0).getTextContent().equals(configName)) {

                    // System.out.println("name : " + eElement.getAttribute("name"));
                    // System.out.println("name : " + eElement.getElementsByTagName("name").item(0).getTextContent());
                    // System.out.println("gateway-configuration-name : " + eElement.getElementsByTagName("gateway-configuration-name").item(0).getTextContent());
                    // System.out.println("exporter : " + eElement.getElementsByTagName("exporter").item(0).getTextContent());
                    NodeList nProps = eElement.getElementsByTagName("property");

                    // System.out.println(nl.getLength());
                    for (int prop = 0; prop < nProps.getLength(); prop++) {
                        Node nProp = nProps.item(prop);

                        if (nProp.getNodeType() == Node.ELEMENT_NODE) {
                            Element eProperty = (Element) nProp;
                            // System.out.println("property: " + eProperty.getAttribute("name") + " = " + eProperty.getTextContent());
                            properties.setProperty(eProperty.getAttribute("name"), eProperty.getTextContent());
                        }
                    }
                }
            }
        }
    }
}
