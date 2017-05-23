/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:14CEST $
 */
// https://www.java2novice.com/restful-web-services/resteasy-hello-world/
// https://www.java2novice.com/restful-web-services/xml-resteasy-jaxb/
// http://www.mastertheboss.com/jboss-frameworks/resteasy/resteasy-basic-authentication-example
package com.ptc.services.restfulwebservices;

/**
 *
 * @author veckardt
 */
import com.mks.api.response.APIException;
import com.ptc.services.restfulwebservices.api.IntegritySession;
import com.ptc.services.restfulwebservices.model.Document;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Path("/mms")
public class MeetingMinutes {

    public String[] TypeDef = {"Meeting Minutes", "Meeting Minutes Entry"};

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})

    public Document newMMs(Document document
    ) throws IOException, APIException {
        IntegritySession.getAllUsers();

        String itemID = IntegritySession.putDocument(TypeDef, document);
        return IntegritySession.getDocument(itemID);
    }

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @GET
    @Produces({MediaType.TEXT_HTML})
    public void index(
            @Context HttpServletRequest request,
            @Context HttpServletResponse servletResponse) throws IOException {
        System.out.println("Forwarding to: " + request.getContextPath() + "/index.jsp");
        servletResponse.sendRedirect(request.getContextPath() + "/index.jsp");
    }

}
