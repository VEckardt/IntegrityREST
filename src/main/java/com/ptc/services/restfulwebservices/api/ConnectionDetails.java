/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:15CEST $
 */
package com.ptc.services.restfulwebservices.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.out;
import java.util.Properties;

/**
 *
 * @author veckardt
 */
public class ConnectionDetails {

    private String user = "administrator";
    private String password = "password";
    private String port = "7001";
    private String hostname = "localhost";

    /**
     * @author Crunchify.com
     * @throws java.io.IOException
     *
     */
    public ConnectionDetails() throws IOException {

        InputStream inputStream = null;

        try {
            Properties prop = new Properties();
            String propFileName = "../config/properties/is.properties";

            inputStream = new FileInputStream(propFileName);
            prop.load(inputStream);

            // get the property value and print it out
            user = prop.getProperty("mksis.apiSession.defaultUser") + "";
            password = prop.getProperty("mksis.apiSession.defaultPassword") + "";
            port = prop.getProperty("mksis.clear.port") + "";

            out.println("REST-API: mksis.apiSession.defaultUser: " + user + ", mksis.apiSession.defaultPassword: " + (password.isEmpty() ? " * not set *" : " * set *"));
            out.println("REST-API: mksis.clear.port: " + port);
        } catch (IOException e) {
            out.println("Exception: " + e);
            throw new IOException(e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * @author Crunchify.com
     * @throws java.io.IOException
     *
     */
    public ConnectionDetails(String username, String password) throws IOException {

        InputStream inputStream = null;

        try {
            Properties prop = new Properties();
            String propFileName = "../config/properties/is.properties";

            inputStream = new FileInputStream(propFileName);
            prop.load(inputStream);

            // get the property value and print it out
            this.user = username.equals("is.properties") ? prop.getProperty("mksis.apiSession.defaultUser") + "" : username;
            this.password = password.equals("is.properties") ? prop.getProperty("mksis.apiSession.defaultPassword") + "" : password;
            this.port = prop.getProperty("mksis.clear.port") + "";

            out.println("REST-API: mksis.apiSession.defaultUser: " + user + ", mksis.apiSession.defaultPassword: " + (password.isEmpty() ? " * not set *" : " * set *"));
            out.println("REST-API: mksis.clear.port: " + port);
        } catch (IOException e) {
            out.println("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return Integer.parseInt(port);
    }

    public String getHostname() {
        return hostname;
    }

    public String getLoginInfo() {
        return ("REST-API: Logging in " + hostname + ":" + port + " with " + user + " ..");
    }

}
