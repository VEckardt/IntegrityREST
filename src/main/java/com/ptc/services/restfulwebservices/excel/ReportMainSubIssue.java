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
import com.mks.api.response.Field;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.mks.gateway.data.ExternalItem;
import com.mks.gateway.mapper.ItemMapperException;
import com.mks.gateway.mapper.UnsupportedPrototypeException;
import com.ptc.services.restfulwebservices.api.IntegritySession;
import static com.ptc.services.restfulwebservices.excel.ExcelExport.writeIIFtoDisk;
import com.ptc.services.restfulwebservices.gateway.ItemMapperConfig;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class ReportMainSubIssue {

    public static void handleMainSubIssues(List<ExternalItem> gatewayItems, String itemIDs, ItemMapperConfig itemMapperConfig) throws Exception {

        // List<ExternalItem> itemList = new ArrayList<>();
        ExternalItem root = new ExternalItem("ISSUE", "100");
        root.add("Summary", itemMapperConfig.getConfigName());

        Command cmd = new Command(Command.IM, "relationships");
        cmd.addOption(new Option("expandRelationshipFields", itemMapperConfig.getProperty("relationshipFields", "Related Items,Active Test Sessions")));
        cmd.addOption(new Option("fields", itemMapperConfig.getProperty("fields", "Project,ID,Summary,Assigned User,Type")));
        cmd.addOption(new Option("expandLevel", "3"));
        for (String id : itemIDs.split(",")) {
            cmd.addSelection(id);
        }
        Response response = IntegritySession.execute(cmd);
        WorkItem wiLevel1 = null;
        ExternalItem eiLevel1 = null;
        WorkItem wiLevel2 = null;
        int lastLevel = 0;
        WorkItemIterator wit = response.getWorkItems();
        while (wit.hasNext()) {
            WorkItem wi = wit.next();
            int thisLevel = Integer.parseInt(wi.getContext("expand-level")) + 1;
            if (thisLevel == 1) {
                wiLevel1 = wi;
                eiLevel1 = newEi(wi);
                root.addChild(eiLevel1);
            } else if (thisLevel == 2 && lastLevel == 1) {
                wiLevel2 = wi;
                addFields(eiLevel1, wi);
            } else if (thisLevel == 2 && lastLevel == 2) {
                wiLevel2 = wi;
                eiLevel1 = newEi(wiLevel1);
                addFields(eiLevel1, wi);
                root.addChild(eiLevel1);
            } else if (thisLevel == 2 && lastLevel == 3) {
                wiLevel2 = wi;
                eiLevel1 = newEi(wiLevel1);
                addFields(eiLevel1, wi);
                root.addChild(eiLevel1);
            } else if (thisLevel == 3 && lastLevel == 2) {
                addFields(eiLevel1, wi);
            } else if (thisLevel == 3 && lastLevel == 3) {
                eiLevel1 = newEi(wiLevel1);
                addFields(eiLevel1, wiLevel2);
                addFields(eiLevel1, wi);
                root.addChild(eiLevel1);
            }
            lastLevel = thisLevel;
        }
        gatewayItems.add(root);
    }

    static ExternalItem newEi(WorkItem wi) throws UnsupportedPrototypeException, ItemMapperException {
        ExternalItem ei = new ExternalItem("ISSUE", wi.getId());
        int expandLevel = Integer.parseInt(wi.getContext("expand-level"));
        Iterator fields = wi.getFields();
        while (fields.hasNext()) {
            Field field = (Field) fields.next();
            ei.add("L" + (expandLevel + 1) + "_" + field.getName(), field.getValueAsString());
            // System.out.println(expandLevel + ": field.getName(): " + field.getName() + ": " + field.getValueAsString());
        }
        return ei;
    }

    static ExternalItem addFields(ExternalItem ei, WorkItem wi) throws UnsupportedPrototypeException, ItemMapperException {
        int expandLevel = Integer.parseInt(wi.getContext("expand-level"));
        Iterator fields = wi.getFields();
        while (fields.hasNext()) {
            Field field = (Field) fields.next();
            ei.add("L" + (expandLevel + 1) + "_" + field.getName(), field.getValueAsString());
            // System.out.println(expandLevel + ": field.getName(): " + field.getName() + ": " + field.getValueAsString());
        }
        return ei;
    }
}
