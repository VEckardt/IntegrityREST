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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author veckardt
 */
public class Node {

    private String category;
    private String nodeid;
    private String text;
    private String project;
    private String action;
    private String assignee;
    private Date targetdate;
    private String textkey;

    /**
     * List of all Node Fields to consider
     */
    public enum FieldList {

        Category("Category"),
        Text("Text"),
        TextKey("Text Key"),
        AssignedUser("Assigned User"),
        TargetDate("Target Date"),
        Action("Expected Results");

        private final String name;

        FieldList(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    };

    /**
     * Empty Constructor
     */
    public Node() {

    }

    /**
     * Constructor to create a Node from workitem
     *
     * @param wi
     */
    public Node(WorkItem wi) {
        this.nodeid = wi.getId();
        this.category = wi.getField(FieldList.Category.toString()).getValueAsString();
        this.text = wi.getField(FieldList.Text.toString()).getValueAsString();
        try {
            this.textkey = wi.getField(FieldList.TextKey.toString()).getValueAsString();
        } catch (java.util.NoSuchElementException ex) {
        }
        try {
            this.targetdate = wi.getField(FieldList.TargetDate.toString()).getDateTime();
        } catch (java.util.NoSuchElementException ex) {
        }
        try {
            this.action = wi.getField(FieldList.Action.toString()).getValueAsString();
        } catch (java.util.NoSuchElementException ex) {
        }        
        // this.action = wi.getField("action").getValueAsString();
        this.assignee = wi.getField(FieldList.AssignedUser.toString()).getValueAsString();
    }

    public String getCategory() {
        return category;
    }

    public String getText() {
        return text;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getAction() {
        return action;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String id) {
        this.nodeid = id;
    }

    public Date getTargetdate() {
        return targetdate;
    }

    public void setTargetdate(String targetdate) throws ParseException {
        if (targetdate == null || targetdate.isEmpty()) {
            this.targetdate = null;
        } else {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            this.targetdate = format.parse(targetdate);
        }
    }

    public String getTextkey() {
        return textkey;
    }

    public void setTextkey(String textkey) {
        this.textkey = textkey;
    }
}
