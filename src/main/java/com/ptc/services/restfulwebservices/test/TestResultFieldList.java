/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.restfulwebservices.test;

import com.mks.api.Command;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.ptc.services.restfulwebservices.api.IntegritySession;
import java.util.ArrayList;

/**
 *
 * @author veckardt
 */
public class TestResultFieldList extends ArrayList<TestResultField> {

    public void init(IntegritySession is) throws APIException {

        Command cmd = new Command(Command.TM, "resultfields");
        Response response = is.execute(cmd);
        WorkItemIterator wii = response.getWorkItems();
        while (wii.hasNext()) {
            WorkItem wi = wii.next();
            this.add(new TestResultField(wi.getId()));
            // log ("Adding ResultField: "+wi.getId(),1);
        }
    }
}
