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
import com.ptc.services.restfulwebservices.model.ItemList;
import com.ptc.services.restfulwebservices.model.Metadata;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/mms")
public class MeetingMinutes {

    public String[] TypeDef = {"Meeting Minutes", "Meeting Minutes Entry"};

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})

    public Response newMMs(Document document)
            throws IOException, APIException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {
        IntegritySession is = null;

        try {
            is = new IntegritySession();
            // is.getAllUsers();
            // System.out.println("document values count: " + document.getValues().size());
            String itemID = is.putDocument(TypeDef, document);
            Document out = is.getDocument(itemID);
            is.release();
            return Response.ok().entity(out).build();
        } catch (APIException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }

//    @Context
//    UriInfo uriInfo;
//    @Context
//    Request request;
//
//    @GET
//    @Produces({MediaType.TEXT_HTML})
//    public void index(
//            @Context HttpServletRequest request,
//            @Context HttpServletResponse servletResponse) throws IOException {
//        // System.out.println("Forwarding to: " + request.getContextPath() + "/index.jsp");
//        // servletResponse.sendRedirect(request.getContextPath() + "/index.jsp");
//    }
    @GET
    @Path("{itemID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocument(@PathParam("itemID") String itemID) throws APIException, IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        IntegritySession is = null;
        try {
            is = new IntegritySession();
            Document document = is.getDocument(itemID);
            is.release();
            return Response.ok().entity(document).build();
        } catch (APIException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
    }

    /**
     * Gets all Defects in JSON format
     *
     * @param queryName the query name (optional)
     * @param dateformat
     * @return Response with JSON data
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMeetingMinutesByQuery(
            @NotNull @QueryParam("query") String queryName,
            @NotNull @QueryParam("dateformat") String dateformat
    ) {
        IntegritySession is = null;
        try {
            is = new IntegritySession(dateformat);
            // queryName = (queryName != null && !queryName.isEmpty()) ? queryName : "All Meeting Minutes";
            String queryFields = is.getQueryFields(queryName);
            ItemList itemList = new ItemList(new Metadata("host", queryFields), is.getAllItemsByQuery(queryName, queryFields));
            is.release();
            return Response.ok().entity(itemList).build();
        } catch (APIException | IOException ex) {
            if (is != null) {
                is.release();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error:<br>" + ex.getMessage().replaceAll("\\. ", ". <br>")).build();
        }
    }
}
