/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision$
 * Last changed:   $Date$
 */
package com.ptc.services.restfulwebservices;

import com.ptc.services.restfulwebservices.model.Item;
import com.ptc.services.restfulwebservices.model.ItemResource;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author veckardt
 */
public class DefectsResourceTest {

    public DefectsResourceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDefectsBrowser method, of class DefectsResource.
     */
//    @Test
//    public void testGetDefectsBrowser() throws Exception {
//        System.out.println("getDefectsBrowser");
//        DefectsResource instance = new DefectsResource();
//        // List<Item> expResult = null;
//        Response result = instance.getDefectsBrowser();
//
//        assertTrue("Error, result is too high", result.getStatus() == 200);
//    }

    /**
     * Test of getDefects method, of class DefectsResource.
     */
//    @Test
//    public void testGetDefects() throws Exception {
//        System.out.println("getDefects");
//        DefectsResource instance = new DefectsResource();
//        // List<Item> expResult = null;
//        Response result = instance.getDefects();
//        // assertEquals(expResult, result);
//        assertTrue("Error, result is too high", result.getStatus() == 200);
//        // TODO review the generated test code and remove the default call to fail.
//        // fail("The test case is a prototype.");
//    }

    /**
     * Test of getCount method, of class DefectsResource.
     */
    @org.junit.Test
    public void testGetCount() throws Exception {
        System.out.println("getCount");
        DefectsResource instance = new DefectsResource();
        int high = 500;
        int low = 200;
        Response result = instance.getCount();
        System.out.println("getCount: " + result);
        assertTrue("Error, result is too high", high >= Integer.parseInt((String) result.getEntity()));
        assertTrue("Error, result is too low", low <= Integer.parseInt((String) result.getEntity()));
    }

    /**
     * Test of newDefect method, of class DefectsResource.
     */
    @org.junit.Test
    public void testNewDefect() throws Exception {
        System.out.println("newDefect");
        Item item = new Item("1", "Summary", "", "Description", "/Projects");
        DefectsResource instance = new DefectsResource();
        Response result = instance.newDefect(item);
        System.out.println("newDefect: " + ((Item) result.getEntity()).getId());
        assertTrue("Error, result is too low", Integer.parseInt(((Item) result.getEntity()).getId()) >= 1000);
        // fail("The test case is a prototype.");
    }

    /**
     * Test of newDefectForm method, of class DefectsResource.
     */
    @org.junit.Test
    public void testNewDefectForm() throws Exception {
        System.out.println("newDefectForm");
        String summary = "Summary";
        String id = "";
        String description = "Description";
        String project = "/Projects";
        DefectsResource instance = new DefectsResource();
        // String expResult = "";
        Response result = instance.newDefectForm(summary, id, description, project);
        assertTrue("Error, result is too low", result.getStatus() == 200);
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getDefect method, of class DefectsResource.
     */
    @org.junit.Test
    public void testGetDefect() {
        System.out.println("getDefect");
        String id = "620";
        DefectsResource instance = new DefectsResource();
        ItemResource expResult = new ItemResource(null, null, "620");
        Response result = instance.getDefect(id);
        assertEquals(expResult.getId(), ((Item) result.getEntity()).getId());
    }

    /**
     * Test of getDefect method, of class DefectsResource.
     */
//    @org.junit.Test
//    public void testDeleteDefect() {
//        System.out.println("deleteDefect");
//        String id = "621";
//        DefectsResource instance = new DefectsResource();
//        String expResult = "Item 621 deleted.";
//        Response result = instance.deleteDefect(id);
//        assertEquals(expResult, ((String) result.getEntity()));
//    }

}
