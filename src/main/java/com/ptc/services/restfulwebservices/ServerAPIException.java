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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author veckardt
 */
public class ServerAPIException extends WebApplicationException {

    /**
     * Create a HTTP 404 (Not Found) exception.
     */
//  public ServerAPIException() {
//    super(Responses.notFound().build());
//  }

    /**
     * Create a HTTP 404 (Not Found) exception.
     *
     * @param message the String that is the entity of the 404 response.
     */
    public ServerAPIException(String message) {
        super(Response.status(404).
                entity(message).type("text/plain").build());
    }
}
