/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:14CEST $
 */
package com.ptc.services.restfulwebservices;

/**
 *
 * @author veckardt
 */
import com.mks.api.response.APIException;
import com.ptc.services.restfulwebservices.api.IntegritySession;
import com.ptc.services.restfulwebservices.model.Item2;
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
import javax.ws.rs.QueryParam;
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
//    @RolesAllowed("ADMIN")
//    @GET
//    @Produces(MediaType.TEXT_XML)
//    public Response getDefectsBrowser() {
//        try {
//            List<Item> allItems = IntegritySession.getAllItems("All Defects");
//            return Response.ok().entity(allItems).build();
//        } catch (APIException ex) {
//            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
//        }
//    }
    /**
     * Gets all Defects in JSON format
     *
     * @param queryName the query name (optional)
     * @return Response with JSON data
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDefectsByQuery(@QueryParam("query") String queryName) {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            queryName = (queryName != null && !queryName.isEmpty()) ? queryName : "All Defects";
            List<Item2> allItems = is.getAllItemsByQuery(queryName);
            is.release();
            return Response.ok().entity(allItems).build();
        } catch (APIException | IOException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }

    /**
     * Returns a single defect
     *
     * @param id
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{item}")
    public Response getDefect(@PathParam("item") String id) {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            // ItemResource itemResource = new ItemResource(uriInfo, request, id);
            // return Response.ok().entity(itemResource).build();
            Item2 item = is.getItem(id);
            is.release();
            return Response.ok().entity(item).build();
        } catch (IOException | APIException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }

    /**
     * Returns the number of defects in Integrity
     *
     * @return
     * @throws APIException
     * @throws java.io.IOException
     */
    @RolesAllowed("ADMIN")
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCount() throws APIException, IOException {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            String count = String.valueOf(is.getAllItemsByQuery("All Defects").size());
            is.release();
            return Response.ok().entity(count).build();
        } catch (APIException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }

    /**
     * Creates a new Item2
     *
     * @param item
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    //    public Item2 newDefect(@QueryParam("id") String id,
    //            @QueryParam("summary") String summary,
    //            @QueryParam("description") String description,
    //            @QueryParam("project") String project
    // @Context HttpServletResponse servletResponse
    public Response newDefect(Item2 item) throws APIException, IOException {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            String itemID = is.putItem(item) ; // new Item2(item.getId(), item.getSummary(), "", item.getDescription(), item.getProject()));
            Item2 newItem = is.getItem(itemID);
            is.release();
            return Response.ok().entity(newItem).build();
        } catch (APIException ex) {
            if (is != null)is.release();
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
        // servletResponse.sendRedirect("../createDefect.html");
    }

    /**
     * Receives the defect details from the custom form and creates a defect in
     * Integrity
     *
     * @param summary
     * @param id
     * @param description
     * @param project
     * @return
     * @throws IOException
     * @throws APIException
     */
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response newDefectForm(@FormParam("summary") String summary,
            @FormParam("id") String id,
            @FormParam("description") String description,
            @FormParam("project") String project) throws IOException, APIException {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            Item2 item = new Item2(id, "Defect", summary, "", description, project);
            String itemID = is.putItem(item);
            is.release();
            return Response.ok().entity(itemID).build();
        } catch (APIException ex) {
            if (is != null)is.release();
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }

    /**
     * Deletes an item in Integrity, requires the permissions to do so
     *
     * @param id
     * @return
     */
    @DELETE
    @Path("{item}")
    public Response deleteDefect(@PathParam("item") String id) throws IOException, APIException {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            is.deleteItem(id);
            is.release();
            return Response.ok().entity("Item " + id + " deleted.").build();
        } catch (APIException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }
}
