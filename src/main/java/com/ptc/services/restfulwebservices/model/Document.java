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

}
