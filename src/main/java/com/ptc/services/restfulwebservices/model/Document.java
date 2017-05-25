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

import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 *
 * @author veckardt
 */
public class Document {

    private String id;
    private String Summary;
    private String AssignedUser;
    private String Description;
    private String project;
    private String attendees;
    private Node[] nodelist;
    // does not work yet
    // private NameValuePair[] values;

    public static String fieldList = "Document Short Title,Description,Assigned User,Project";

    public enum FieldList {

        Summary("Document Short Title"),
        Description("Description"),
        AssignedUser("Assigned User"),
        Project("Project");

        private final String name;

        FieldList(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    };
//    public NameValuePair[] getValues() {
//        return values;
//    }

    public Document() {
    }

    public String getSummary() {
        return Summary;
    }

    public String getAssignedUser() {
        return AssignedUser;
    }

    public String getId() {
        return id;
    }

    public String getProject() {
        return project;
    }

    public Node[] getNodelist() {
        return nodelist;
    }

    public void setSummary(String Summary) {
        this.Summary = Summary;
    }

    public void setAssignedUser(String AssignedUser) {
        this.AssignedUser = AssignedUser;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setProject(String project) {
        this.project = project;
    }
    public void setNodelist(Node[] nodelist) {
        this.nodelist = nodelist;
    }

//    public void setValues(NameValuePair[] values) {
//        this.values = values;
//    }    
    
    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getAttendees() {
        return attendees;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }
    public void fillFieldValues(WorkItem wi) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Iterator docFields = wi.getFields();
        while (docFields.hasNext()) {
            Field fld = (Field) docFields.next();
            // out.println(fld.getName() + "" + fld.getModelType().toString());

            String fldName = fld.getName();
            if (fld.getDataType() != null) {
                String value = null;
                if (fld.getDataType().endsWith("String")) {
                    value = fld.getValueAsString();
                } else if (fld.getDataType().endsWith("Date")) {
                    value = fld.getDateTime().toString();
                } else if (fld.getDataType().endsWith("Item")) {
                    com.mks.api.response.Item item = fld.getItem();
                    String modelType = item.getModelType();
                    if (modelType.endsWith("User")) {
                        value = fld.getItem().getId();
                    }
                } else {
                    value = fld.getValueAsString();
                }
                String methodeName = "";
                for (Document.FieldList fl : Document.FieldList.values()) {
                    if (fl.toString().equals(fldName)) {
                        methodeName = fl.name();
                    }
                }
                Method setNameMethod = this.getClass().getMethod("set" + methodeName, String.class);
                setNameMethod.invoke(this, value); // pass arg

                // values.add(new NameValuePair(fldName, value));
                // System.out.println(fldName + "=>" + value);
            }
        }
    }
}
