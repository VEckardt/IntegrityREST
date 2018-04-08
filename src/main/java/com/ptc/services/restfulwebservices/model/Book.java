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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;

/**
 *
 * @author veckardt
 */
@JsonAutoDetect
@JsonSerialize
public class Book {

    @JsonProperty("id")
    String id;

    // @JsonFormat(with = Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    List<NameValuePair> values; //  = new ArrayList<NameValuePair>() ;

    public Book() {

    }

    // @JsonProperty("values")
    public List<NameValuePair> getValues() {
        return values;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValues(List<NameValuePair> values) {
        // objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        this.values = values;
    }

    public void list() {
        System.out.println("id = " + id);
        System.out.println("len= " + values.size());
    }

}
