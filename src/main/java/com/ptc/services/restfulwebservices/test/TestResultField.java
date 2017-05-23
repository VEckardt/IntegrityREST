/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.restfulwebservices.test;

/**
 *
 * @author veckardt
 */
public class TestResultField {

    private String fieldName;
    private String fieldType;
    private String fieldValue;

    public TestResultField(String fieldName) {
        this.fieldName = fieldName;
    }

    public TestResultField(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public void setValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }
}
