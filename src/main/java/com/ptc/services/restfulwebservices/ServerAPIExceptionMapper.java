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

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author veckardt
 */
@Provider
public class ServerAPIExceptionMapper implements ExceptionMapper<ServerAPIException> {
  public Response toResponse(ServerAPIException  ex) {
    return Response.status(404).
      entity("Ouchhh, this item leads to following error:" + ex.getMessage()).
      type("text/plain").
      build();
  }
}   
