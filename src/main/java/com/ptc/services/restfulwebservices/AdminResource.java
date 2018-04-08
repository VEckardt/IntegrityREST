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

import com.mks.api.response.APIException;
import com.ptc.services.restfulwebservices.api.IntegritySession;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author veckardt
 */
@Path("/admin")
public class AdminResource {

    @GET
    @Path("/groupMembers/{groupName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupMembers(@PathParam("groupName") String groupName) throws IOException, JSONException, APIException {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            JSONObject groupMembers = is.getUsersFromGroup(groupName);
            is.release();

            return Response.ok().entity(groupMembers.toString()).build();
        } catch (APIException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
    }
    
    @GET
    @Path("/groups")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroups() throws IOException, JSONException, APIException {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            JSONObject groups = is.getMKSDomainGroups();
            is.release();

            return Response.ok().entity(groups.toString()).build();
        } catch (APIException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
    }    
}
