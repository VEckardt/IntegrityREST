/*
 * Copyright:      Copyright 2015 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         V. Eckardt, Senior Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:17CEST $
 */
package com.ptc.services.restfulwebservices.test;

import com.mks.api.response.WorkItem;
import com.mks.gateway.data.ExternalItem;
import com.mks.gateway.mapper.ItemMapperException;
import com.ptc.services.restfulwebservices.api.Config;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class ResultCounter {

    int passCount = 0;
    int failCount = 0;
    int otherCount = 0;
    int totalCnt = 0;

    List<TestResult> resultList = new LinkedList<>();

    public ResultCounter() {

    }

    public void addToCounter(ExternalItem currentChild, WorkItem wi) throws ItemMapperException {
        String result = wi.getField("verdictType").getValueAsString();

        if (result != null) {
            totalCnt++;
            if (result.contentEquals("Pass")) {
                passCount++;
            }
            if (result.contentEquals("Fail")) {
                failCount++;
            }
            if (result.contentEquals("Other")) {
                otherCount++;
            }

        }
        resultList.add(new TestResult(wi));
    }

    public void setCounters(ExternalItem currentChild) throws ItemMapperException {
        currentChild.add("Passed", passCount);
        currentChild.add("Failed", failCount);
        currentChild.add("Other", otherCount);
        currentChild.add("Total", totalCnt);
        if (resultList.size() > 0) {
            Collections.sort(resultList);
            TestResult trLast = resultList.get(0);
            currentChild.add("Last Executed", Config.dfDayTimeShort.format(trLast.getModifiedDate()));

            int size = (resultList.size() > 20 ? 20 : resultList.size());
            int i = 0;
            for (TestResult tr : resultList) {
                currentChild.add("Result" + (size - (i++)), tr.getVerdictType());
                if (i == 20) {
                    break;
                }
            }

        } else {
            currentChild.add("Last Executed", "-");
        }
    }
}
