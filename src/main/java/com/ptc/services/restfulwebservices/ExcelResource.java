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
import static com.ptc.services.restfulwebservices.excel.ExcelExport.loadItemDataAndGenerateExcelFile;
import com.ptc.services.restfulwebservices.tools.FileUtils;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/excel")
public class ExcelResource {

    private static final String FILE_PATH = "../data/tmp/web/outfile.xlsx";

    /**
     * Excel Service handling multiple IDs provided as query param like
     * ?id=11,12,13
     *
     * Docu:
     * http://resteasy.jboss.org/docs
     * 
     * Examples:
     * http://localhost:7001/IntegrityREST/excel/get?ids=620&gatewayConfig=Defect%20Leakage%20Status%20(Excel)
     * http://localhost:7001/IntegrityREST/excel/get?ids=621,620&gatewayConfig=Main%20Sub%20Issues
     * http://localhost:7001/IntegrityREST/excel/get?ids=539&gatewayConfig=Test%20Result%20History%20(Excel)
     *
     *
     * Common responses (Tibco)
     * https://docs.tibco.com/pub/tpm-rest/1.0.0/doc/html/GUID-BAA2DC07-D7DC-49BD-80A5-B4998B56B9BF.html
     *
     * @param itemIds
     * @param itemId
     * @param issues
     * @param gatewayConfig
     * @return
     */
    @PermitAll
    @GET
    @Path("/get")
    @Produces({"application/vnd.ms-excel", MediaType.TEXT_HTML})
    public Response getFile(@QueryParam("ids") String itemIds,
            @QueryParam("id") String itemId,
            @QueryParam("issues") String issues,
            @QueryParam("gatewayConfig") String gatewayConfig) {
        try {
            // File currentDir = new File("");
            // System.out.println(currentDir.getAbsolutePath());
            itemIds = (itemIds != null && !itemIds.isEmpty()) ? itemIds : "";
            itemId = (itemId != null && !itemId.isEmpty()) ? itemId : "";
            issues = (issues != null && !issues.isEmpty()) ? issues : "";
            gatewayConfig = (gatewayConfig != null && !gatewayConfig.isEmpty()) ? gatewayConfig : "";

            if (itemIds.isEmpty() && itemId.isEmpty() && issues.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("The query parameters 'id', 'ids' and 'issues' can not be empty at the same time").build();
            }
            if (gatewayConfig.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("The query parameter 'gatewayConfig' can not be empty").build();
            }

            File file = loadItemDataAndGenerateExcelFile(issues + itemId + itemIds, gatewayConfig);

            ResponseBuilder response = Response.ok((Object) file);
            response.header("Content-Disposition", "attachment; filename=" + FileUtils.validFileName(gatewayConfig) + ".xlsx");

//            return Response.ok(f, mt)
//                    .header("Content-Disposition", "attachment; filename=Documentation.zip")
//                    .build();
            return response.build();
        } catch (Exception ex) {
            Logger.getLogger(ExcelResource.class.getName()).log(Level.SEVERE, null, ex);
            // throw new Exception (ex);
        }
        return null;
    }

    /**
     * Excel Service handling exactly one ID part of the path
     *
     * @param itemId
     * @return
     */
    @GET
    @Path("/get/{item}")
    @Produces("application/vnd.ms-excel")
    public Response getFileSingle(@PathParam("item") String itemId) {

        File currentDir = new File("");
        System.out.println(currentDir.getAbsolutePath());

        File file = new File(FILE_PATH);

        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=new-excel-file.xls");
        return response.build();

    }
}
