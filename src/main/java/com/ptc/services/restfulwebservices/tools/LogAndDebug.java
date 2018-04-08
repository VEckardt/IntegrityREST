/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:20CEST $
 */
package com.ptc.services.restfulwebservices.tools;

/**
 *
 * @author veckardt
 */
public class LogAndDebug {

    public static void log(String text) {
        System.out.println(text);
    }

    public static void log(String text, int level) {
        System.out.println(text);
    }
}
