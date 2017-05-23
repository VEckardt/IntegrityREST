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
import com.ptc.services.restfulwebservices.api.IntegritySession;
import static com.ptc.services.restfulwebservices.api.IntegritySession.execute;
import static com.ptc.services.restfulwebservices.api.IntegritySession.getProperty;
import static com.ptc.services.restfulwebservices.api.IntegritySession.readPickField;
import static com.ptc.services.restfulwebservices.tools.LogAndDebug.log;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author veckardt
 */
public class ReportDefectLeakage {

    private static final Map<String, Field> fieldMapH = new LinkedHashMap<>();
    private static final Map<String, Field> fieldMapV = new LinkedHashMap<>();
    private static String pickFieldHName = "Test Level";
    private static String pickFieldVName = "Originated In";
    private static final String printBlankNumbersAs = "0";
    private static final HashMap<String, Integer> occurenceMap = new HashMap<>();
    private static final String headerFieldPrefix = "Hdr";

    /**
     * Generates the DefectLeakage Output (Table and Chart), based on the
     * template Template_Defect_Leakage.xlsx
     *
     * @param gatewayItems
     * @param itemIDs
     * @throws APIException
     * @throws ItemMapperException
     * @throws java.io.IOException
     */
    public static void handleDefectLeakage(List<ExternalItem> gatewayItems, String itemIDs) throws APIException, ItemMapperException, IOException {

        gatewayItems.add(ExcelExport.getFirstItem (itemIDs));
        
        ExternalItem root = gatewayItems.get(0);

             
        //Access the worksheet, so that we can update / modify it.
        log("Adding Headers ...", 2);
        int i = 0;
        for (String pick : fieldMapH.keySet()) {
            if (!fieldMapH.get(pick).getValueAsString().endsWith("]")) {
                gatewayItems.get(0).add(headerFieldPrefix + (++i), "      " + fieldMapH.get(pick).getValueAsString());
            }
        }

        pickFieldHName = getProperty(pickFieldHName, "Test Level");
        pickFieldVName = getProperty(pickFieldVName, "Originated In");

        // default query name
        String defectLeakageQueryName = "Defect Leakage Status";
        String projectFilter = " and (field[\"Project\"] = \"" + root.getField("Project").getStringValue() + "\"))";

        // check if the query is there
        try {
            Command cmd = new Command(Command.IM, "viewquery");
            cmd.addSelection(defectLeakageQueryName);
            IntegritySession.execute(cmd);
        } catch (APIException ex) {
            // if not, set it to null to prevent from using it
            defectLeakageQueryName = "";
        }

        // Get the horizontal Pick Field content
        readPickField(pickFieldHName, fieldMapH, "picks", "label");
        // Get the vertical Pick Field content
        readPickField(pickFieldVName, fieldMapV, "picks", "label");

        // Count the Defect data
        Command cmd = new Command(Command.IM, "issues");
        if (!defectLeakageQueryName.isEmpty()) {
            // this query usage shall be flexible and faster
            cmd.addOption(new Option("queryDefinition", "((subquery[\"" + defectLeakageQueryName + "\"])" + projectFilter));
        } else {
            // a litte bit more unfexible, and perhaps a bit slower 
            cmd.addOption(new Option("queryDefinition", "((field[\"Type\"]=Defect)" + projectFilter));
        }
        cmd.addOption(new Option("fields", pickFieldHName + "," + pickFieldVName));
        Response response = execute(cmd);
        WorkItemIterator wit = response.getWorkItems();
        while (wit.hasNext()) {
            WorkItem wi = wit.next();
            // log("Found Defect " + wi.getId(), 2);
            try {
                String testLevel = wi.getField(pickFieldHName).getValueAsString();
                String origin = wi.getField(pickFieldVName).getValueAsString();
                if (testLevel != null && origin != null) {
                    String key = origin + "&" + testLevel;
                    if (occurenceMap.containsKey(key)) {
                        occurenceMap.put(key, occurenceMap.get(key) + 1);
                    } else {
                        occurenceMap.put(key, 1);
                    }
                }
            } catch (NoSuchElementException ex) {
            }
        }

        // Create The Output Row Items
        for (String rowKey : fieldMapV.keySet()) {
            String rowValue = (String) fieldMapV.get(rowKey).getValue();
            ExternalItem rowItem = new ExternalItem("CONTENT", "ROW-" + rowValue.replaceAll("[^a-zA-Z0-9]", ""));
            rowItem.add(pickFieldVName, rowValue);
            for (String colKey : fieldMapH.keySet()) {
                String colValue = (String) fieldMapH.get(colKey).getValue();
                String key = rowValue + "&" + colValue;

                rowItem.add(colValue, occurenceMap.get(key) == null ? printBlankNumbersAs : occurenceMap.get(key));
            }
            // add the new RowItem to the root Item as chiled
            root.addChild(rowItem);
        }
    }
}
