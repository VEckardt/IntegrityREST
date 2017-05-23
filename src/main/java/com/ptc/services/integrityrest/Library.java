/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:14CEST $
 */
package com.ptc.services.integrityrest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 *
 * Try out only
 * 
 * http://planet.jboss.org/post/seemingly_common_resteasy_error_and_a_solution
 * http://docs.jboss.org/resteasy/docs/3.1.2.Final/userguide/html_single/index.html#javax.ws.rs.core.Application
 * 
 * @author veckardt
 */
@Path("/library")
public class Library{

    @GET
    @Path("/books")
    public String getBooks() {
        return "getBooks";
    }

    @GET
    @Path("/book/{isbn}")
    public String getBook(@PathParam("isbn") String id) {
        // search my database and get a string representation and return it
        return "getBooks";
    }

    @PUT
    @Path("/book/{isbn}")
    public void addBook(@PathParam("isbn") String id, @QueryParam("name") String name) {
    }

    @DELETE
    @Path("/book/{id}")
    public void removeBook(@PathParam("id") String id) {
    }

}
