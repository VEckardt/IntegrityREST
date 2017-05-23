/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:17CEST $
 */
package com.ptc.services.restfulwebservices.model;

/**
 *
 * @author veckardt
 */

import com.mks.api.response.APIException;
import com.ptc.services.restfulwebservices.api.IntegritySession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

//@Path("/defect")
public class ItemResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    String id;

    public ItemResource(UriInfo uriInfo, Request request, String id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    //Application integration
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Item getItem() throws APIException {
        Item item = IntegritySession.getItem(id);
        if (item == null) {
            throw new RuntimeException("Get: Item with " + id + " not found");
        }
        return item;
    }

    // for the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public Item getItemHTML() throws APIException {
        Item item = IntegritySession.getItem(id); // TodoDao.instance.getModel().get(id);
        if (item == null) {
            throw new RuntimeException("Get: Item with " + id + " not found");
        }
        return item;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putDefect(JAXBElement<Item> item) throws APIException {
        Item c = item.getValue();
        return putAndGetResponse(c);
    }

    @DELETE
    public void deleteDefect() {
        Item c = null; //; TodoDao.instance.getModel().remove(id);
        if (c == null) {
            throw new RuntimeException("Delete: Item with " + id + " not found");
        }
    }

    private Response putAndGetResponse(Item item) throws APIException {
        Response res;
        try {
            IntegritySession.getItem(item.getId());
            // found
            res = Response.noContent().build();
        } catch (APIException ex) {
            // not found
            res = Response.created(uriInfo.getAbsolutePath()).build();
        }

        IntegritySession.putItem(item);

//        if (TodoDao.instance.getModel().containsKey(item.getId())) {
//            res = Response.noContent().build();
//        } else {
//            res = Response.created(uriInfo.getAbsolutePath()).build();
//        }
//        TodoDao.instance.getModel().put(item.getId(), item);
        return res;
    }
}
