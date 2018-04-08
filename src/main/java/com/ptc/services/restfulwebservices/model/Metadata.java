/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision$
 * Last changed:   $Date$
 */

package com.ptc.services.restfulwebservices.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class Metadata {
    String hostname; 
    List<FieldDef> fields = new ArrayList<>();
    
    public Metadata (String hostname, String fieldList) {
        this.hostname = hostname;
        for (String fieldName: fieldList.split(",")) {
            fields.add(new FieldDef(fieldName, "thetype"));
        }
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setFields(List<FieldDef> fields) {
        this.fields = fields;
    }

    public String getHostname() {
        return hostname;
    }

    public List<FieldDef> getFields() {
        return fields;
    }
    
}
