/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:16CEST $
 */
package com.ptc.services.restfulwebservices.excel;

import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.mks.gateway.data.ExternalItem;
import com.mks.gateway.mapper.ItemMapperException;
import com.mks.gateway.mapper.UnsupportedPrototypeException;
import com.ptc.services.restfulwebservices.api.IntegritySession;
import static com.ptc.services.restfulwebservices.api.IntegritySession.execute;
import com.ptc.services.restfulwebservices.test.ResultCounter;
import static com.ptc.services.restfulwebservices.tools.LogAndDebug.log;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author veckardt
 */
public class ReportTestResultHistory {

    public static void initDoc(ExternalItem docItem, String docID) throws APIException, UnsupportedPrototypeException, ItemMapperException {
        Command cmd = new Command(Command.IM, "viewsegment");
        // cmd.addOption(new Option("expandRelationshipFields", "contains"));
        cmd.addOption(new Option("fields", "Section,Text,ID,Category,Type,State,Last Result"));
        // cmd.addOption(new Option("expandLevel", "3"));
        // for (String id : itemIDs.split(",")) {
        cmd.addSelection(docID);
        // }
        Response response = IntegritySession.execute(cmd);
        WorkItemIterator wit = response.getWorkItems();
        wit.next();
        while (wit.hasNext()) {
            WorkItem wi = wit.next();
            ExternalItem ei = new ExternalItem("CONTENT", wi.getId());
            Iterator fields = wi.getFields();
            while (fields.hasNext()) {
                Field field = (Field) fields.next();
                ei.add(field.getName(), field.getValueAsString());
                // System.out.println("field.getName(): " + field.getName() + ": " + field.getValueAsString());
            }
            docItem.addChild(ei);
        }
    }

    /**
     *
     * @param docItem
     */
    public static void handleTestResultHistory(ExternalItem docItem) throws APIException, UnsupportedPrototypeException, ItemMapperException, IOException {
        // log("In IterateChilds ...", 1);
        // does this item have any children?
        if (!docItem.getChildren().isEmpty()) {
            // walk through all childs
            Iterator<ExternalItem> childs = docItem.childrenIterator();
            while (childs.hasNext()) {
                ExternalItem currentChild = (ExternalItem) childs.next();
                if (isTestCase(currentChild)) {

                    Command cmd = new Command(Command.TM, "results");
                    cmd.addOption(new Option("caseID", currentChild.getExternalId()));
                    cmd.addOption(new Option("sortAscending"));
                    Response response = execute(cmd);
                    WorkItemIterator wit = response.getWorkItems();

                    ResultCounter rc = new ResultCounter();

                    // loop through all test results
                    while (wit.hasNext()) {
                        WorkItem wi = wit.next();
                        rc.addToCounter(currentChild, wi);
                    }
                    rc.setCounters(currentChild);
                    // Last Executed
                }
                // currentChild.add(pickFieldPrefix + "Count", fieldMapH.keySet().size());
                handleTestResultHistory(currentChild);
            }
        } else {
            // log("no childs.", 2);
        }
        // log("End IterateChilds.", 1);
    }

    /**
     * Returns true if the item is of type TestCase
     *
     * @param item
     * @return
     * @throws com.mks.gateway.mapper.ItemMapperException
     * @throws com.mks.api.response.APIException
     */
    public static Boolean isTestCase(ExternalItem item) throws ItemMapperException, APIException {
        String type = item.getValueAsString("Type");
        String isMeaningful = item.getValueAsString("Is Meaningful") + "";

        // check, if current test case is meaningful, if not, exit with false
        if (isMeaningful.contentEquals("false")) {
            return false;
        }
        // check, if current test case type is already in the stack, if yes, take it to return it faster
        if (testCaseTypes.containsKey(type)) {
            return testCaseTypes.get(type);
        } else {
            // Determine dynmically if this type is a test case
            Command cmd = new Command(Command.IM, "types");
            cmd.addOption(new Option("fields", "TestRole"));
            cmd.addSelection(type);
            Response response = execute(cmd);
            // ResponseUtil.printResponse(response, 1, System.out);
            WorkItem wi = response.getWorkItem(type);
            String testRole = wi.getField("testRole").getValueAsString();
            testCaseTypes.put(type, testRole.contentEquals("testCase"));
            return testRole.contentEquals("testCase");
        }
        // return getType(item).contentEquals("Test Case");
    }
    private static final Map<String, Boolean> testCaseTypes = new TreeMap<>();
}
