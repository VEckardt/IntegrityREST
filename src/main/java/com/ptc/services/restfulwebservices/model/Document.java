/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:17CEST $
 */
package com.ptc.services.restfulwebservices.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class Document {

    // Unique Doc ID
    private String id;
    // The Doc Attributes
    private List<NameValuePair> values = new ArrayList<>();
    // The Nodes
    private Node[] nodelist;

    public static String fieldList = "Document Short Title,Description,Assigned User,Project,State";

//    public enum FieldList {
//
//        Summary("Document Short Title"),
//        Description("Description"),
//        AssignedUser("Assigned User"),
//        Project("Project");
//
//        private final String name;
//
//        FieldList(String name) {
//            this.name = name;
//        }
//
//        @Override
//        public String toString() {
//            return name;
//        }
//    };
    public Document() {
    }

    public List<NameValuePair> getValues() {
        return values;
    }

    public Node[] getNodelist() {
        return nodelist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNodelist(Node[] nodelist) {
        this.nodelist = nodelist;
    }

    public void setValues(List<NameValuePair> values) {
        this.values = values;
    }
}
