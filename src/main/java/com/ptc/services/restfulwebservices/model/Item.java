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

// com.ptc.services.jersey.jaxb/rest/todo
// /rest/todo
import com.ptc.services.restfulwebservices.*;
import com.mks.api.response.WorkItem;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author veckardt
 */
@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
// Isn't that cool?
public class Item {

    private String id;
    private String summary;
    private String description;
    private String state;
    private String project;

    public Item() {

    }

    public Item(WorkItem wi) {
        this.id = wi.getId();
        this.summary = wi.getField("Summary").getValueAsString();
        this.state = wi.getField("State").getValueAsString();
        this.description = wi.getField("description").getValueAsString();
        this.project = wi.getField("project").getValueAsString();
    }

    public Item(String id, String summary, String state, String description, String project) {
        this.id = id;
        this.summary = summary;
        this.state = state;
        this.description = description;
        this.project = project;
    }

    public String getId() {
        return id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
