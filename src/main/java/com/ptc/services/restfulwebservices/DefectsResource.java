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
import java.io.IOException;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/defects")
public class DefectsResource {

    //    @Context
    //    UriInfo uriInfo;
    //    @Context
    //    Request request;
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
    public Response getDefectsBrowser() {
        try {
            List<Item> allItems = IntegritySession.getAllItems("All Defects");
            return Response.ok().entity(allItems).build();
        } catch (APIException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getDefects() throws APIException {
        try {
            List<Item> allItems = IntegritySession.getAllItems("All Defects");
            return Response.ok().entity(allItems).build();
        } catch (APIException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{item}")
    public Response getDefect(@PathParam("item") String id) {
        try {
            // ItemResource itemResource = new ItemResource(uriInfo, request, id);
            // return Response.ok().entity(itemResource).build();
            Item item = IntegritySession.getItem(id);
            return Response.ok().entity(item).build();
        } catch (APIException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCount() throws APIException {
        try {
            String count = String.valueOf(IntegritySession.getAllItems("All Defects").size());
            return Response.ok().entity(count).build();
        } catch (APIException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    //    public Item newDefect(@QueryParam("id") String id,
    //            @QueryParam("summary") String summary,
    //            @QueryParam("description") String description,
    //            @QueryParam("project") String project
    // @Context HttpServletResponse servletResponse
    public Response newDefect(Item item) {
        try {
            String itemID = IntegritySession.putItem(new Item(item.getId(), item.getSummary(), "", item.getDescription(), item.getProject()));
            Item newItem = IntegritySession.getItem(itemID);
            return Response.ok().entity(newItem).build();
        } catch (APIException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
        // servletResponse.sendRedirect("../createDefect.html");
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response newDefectForm(@FormParam("summary") String summary,
            @FormParam("id") String id,
            @FormParam("description") String description,
            @FormParam("project") String project) throws IOException, APIException {
        try {
            Item item = new Item("1", summary, "", description, project);
            String itemID = IntegritySession.putItem(item);
            return Response.ok().entity(itemID).build();
        } catch (APIException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }

    @DELETE
    @Path("{item}")
    public Response deleteDefect(@PathParam("item") String id) {
        try {
            IntegritySession.deleteItem(id);
            return Response.ok().entity("Item " + id + " deleted.").build();
        } catch (APIException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }
}
