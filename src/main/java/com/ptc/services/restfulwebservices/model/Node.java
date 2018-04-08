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

import com.mks.api.response.WorkItem;
import static com.ptc.services.restfulwebservices.api.IntegritySession.fillFieldValues;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class Node {

    public static String fieldList = "Category,Text,Text Key,Assigned User,Target Date,Expected Results,State";

    private String nodeid;
    private String id;
    private List<NameValuePair> values = new ArrayList<>();

    /**
     * Empty Constructor
     */
    public Node() {

    }

    /**
     * Constructor to create a Node from workitem
     *
     * @param wi
     * @throws java.lang.NoSuchMethodException
     */
    public Node(WorkItem wi) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.id = wi.getId();
        this.nodeid = wi.getId();
        fillFieldValues(wi, values);
    }

    public List<NameValuePair> getValues() {
        return values;
    }

    public void setValues(List<NameValuePair> values) {
        this.values = values;
    }

    public String getNodeid() {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).getName().equals("nodeid")) {
                return values.get(i).getValue().replace("-", "").replace("undefined", "");
            }
        }
        return "";
    }

    public void setNodeid(String id) {
        values.add(new NameValuePair("nodeid", "String", id));
        this.id = id;
        this.nodeid = id;
    }
}
