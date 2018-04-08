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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author veckardt
 */
@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
// Isn't that cool?
public class Item2 {

    private String id;
    private List<NameValuePair> values = new ArrayList<>();

    public Item2() {

    }

    public Item2(WorkItem wi, String fieldList) {
        this.id = wi.getId();
        fillFieldValues(wi, this.values, fieldList);
    }

    private Boolean containsField(String fieldName) {
        for (NameValuePair nvp : values) {
            if (nvp.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public Item2(WorkItem wi) {
        this.id = wi.getId();
        fillFieldValues(wi, this.values);
    }

    public Item2(String id, String type, String summary, String state, String description, String project) {
        this.id = id;
        values.add(new NameValuePair("Type", "String", type));
        values.add(new NameValuePair("Summary", "String", summary));
        values.add(new NameValuePair("State", "String", state));
        values.add(new NameValuePair("Description", "String", description));
        values.add(new NameValuePair("Project", "String", project));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<NameValuePair> getValues() {
        return values;
    }

    public void setValues(List<NameValuePair> values) {
        this.values = values;
    }
}
