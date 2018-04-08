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
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author veckardt
 */
@Path("/config")
public class ConfigResource {

    @GET
    @Path("/typeCategories/{typeName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTypeCategories(@PathParam("typeName") String typeName) {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            JSONArray typeCategories = is.getAllowedNodeCategories(typeName);
            is.release();
            return Response.ok().entity(typeCategories.toString()).build();
        } catch (IOException | JSONException | APIException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
    }

    @GET
    @Path("/typeStates/{typeName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTypeStates(@NotNull @PathParam("typeName") String typeName) {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            JSONArray typeCategories = is.getAllowedStates(typeName);
            is.release();
            return Response.ok().entity(typeCategories.toString()).build();
        } catch (IOException | JSONException | APIException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
    }
}
