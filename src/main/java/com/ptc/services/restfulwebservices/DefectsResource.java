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
import com.ptc.services.restfulwebservices.model.Item;
import com.ptc.services.restfulwebservices.model.ItemResource;
import java.io.IOException;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Path("/defects")
public class DefectsResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

//    @GET
//    @Path("/{message}")
//    public Response publishMessage(@PathParam("message") String msgStr){
//
//        String responseStr = "Received message: "+msgStr;
//        return Response.status(200).entity(responseStr).build();
//    }
    @RolesAllowed("ADMIN")
    @GET
    @Produces(MediaType.TEXT_XML)
    public List<Item> getDefectsBrowser() throws APIException {
        return IntegritySession.getAllItems("All Defects");
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Item> getDefects() throws APIException {
        return IntegritySession.getAllItems("All Defects");
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() throws APIException {
        // int count = 10;
        return String.valueOf(IntegritySession.getAllItems("All Defects").size());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    public Item newDefect(@QueryParam("id") String id,
//            @QueryParam("summary") String summary,
//            @QueryParam("description") String description,
//            @QueryParam("project") String project
    // @Context HttpServletResponse servletResponse
    public Item newDefect(Item item
    ) throws IOException, APIException {
        Item item2 = new Item(item.getId(), item.getSummary(), "", item.getDescription(), item.getProject());
//        System.out.println("project => " + project);
//        System.out.println("id => " + id);
//        System.out.println("summary => " + summary);
//        System.out.println("description => " + description);
        String itemID = IntegritySession.putItem(item2);
        return IntegritySession.getItem(itemID);
//        servletResponse.sendRedirect("../createDefect.html");
    }

    // https://developer.jboss.org/thread/248859
    // http://www.mastertheboss.com/jboss-frameworks/resteasy/resteasy-tutorial-part-two-web-parameters
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public String newDefectForm(@FormParam("summary") String summary,
            @FormParam("id") String id,
            @FormParam("description") String description,
            @FormParam("project") String project) throws IOException, APIException {
        Item item = new Item("1", summary, "", description, project);
//        System.out.println("project => " + project);
//        System.out.println("id => " + item.getId());
//        System.out.println("summary => " + item.getSummary());
//        System.out.println("description => " + item.getDescription());
        return IntegritySession.putItem(item);
        // servletResponse.sendRedirect("../createDefect.html");
    }

    @Path("{item}")
    public ItemResource getDefect(@PathParam("item") String id) {
        return new ItemResource(uriInfo, request, id);
    }
}
