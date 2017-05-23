/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.restfulwebservices.excel;

import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.mks.gateway.data.ExternalItem;
import com.mks.gateway.data.xml.ItemXMLConstants;
import com.mks.gateway.data.xml.ItemXMLWriter;
import com.mks.gateway.mapper.ItemMapperException;
import com.mks.gateway.mapper.UnsupportedPrototypeException;
// import com.ptc.services.common.api.ApplicationProperties;
//import com.ptc.services.common.gateway.DocNodeIterators;
//import com.ptc.services.common.gateway.ItemHandler;
import com.ptc.services.restfulwebservices.api.Config;
import com.ptc.services.restfulwebservices.api.IntegritySession;
import static com.ptc.services.restfulwebservices.api.IntegritySession.getConfigProperties;
import static com.ptc.services.restfulwebservices.api.IntegritySession.getProperty;
import static com.ptc.services.restfulwebservices.api.IntegritySession.initGatewayConfig;
import static com.ptc.services.restfulwebservices.excel.ReportDefectLeakage.handleDefectLeakage;
import static com.ptc.services.restfulwebservices.excel.ReportMainSubIssue.handleMainSubIssues;
import static com.ptc.services.restfulwebservices.excel.ReportTestResultHistory.handleTestResultHistory;
import static com.ptc.services.restfulwebservices.tools.LogAndDebug.log;
import com.ptc.services.restfulwebservices.security.SecurityInterceptor;
import static com.ptc.services.restfulwebservices.tools.FileUtils.canWriteFile;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author veckardt
 */
public class ExcelExport {

    private static final String tempFileName = "../data/tmp/web/outfile.xlsx";
    private static String templateFile = "File On Server";
    private static final String BUILD_NUMBER = "1.1.6";
    private static String lockPassword = "0000";
    // private static String gatewayConfig;
    // private static ItemMapperConfig mappingConfig;
    // private static IGatewayDriver iGatewayDriver;

    // 
    /**
     * Examples:
     * http://localhost:7001/IntegrityREST/excel/get?ids=620&gatewayConfig=Defect%20Leakage%20Status%20(Excel)
     * http://localhost:7001/IntegrityREST/excel/get?ids=621,620&gatewayConfig=Main%20Sub%20Issues
     * http://localhost:7001/IntegrityREST/excel/get?ids=539&gatewayConfig=Test%20Result%20History%20(Excel)
     *
     */
    public enum ExportType {

        DefectLeakage, MainSubIssue, TestResultHistory
    };

    /**
     * For testing only
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        SecurityInterceptor.username = "veckardt";
        SecurityInterceptor.password = "password";

        // loadItemDataAndGenerateExcelFile("622", "Main Sub Issues");
        // Test Result History (Excel)
        loadItemDataAndGenerateExcelFile("539", "Test Result History (Excel)");

        // Defect Leakage Status (Excel)
        // loadItemDataAndGenerateExcelFile("622", "Defect Leakage Status (Excel)");
    }

    /**
     * Main Routine, executed by the IntegrityREST resource 'Excel Resource'
     *
     * @param itemIDs
     * @param gatewayConfig
     * @return
     * @throws Exception
     */
    public static File loadItemDataAndGenerateExcelFile(String itemIDs, String gatewayConfig) throws Exception {
        File tempFile = new File(tempFileName);
        List<ExternalItem> gatewayItems = new ArrayList<>();

        try {
            transformFromItems(gatewayItems, itemIDs, tempFile, null, gatewayConfig);
            return tempFile;
        } catch (ParserConfigurationException | SAXException | IOException | APIException ex) {
            Logger.getLogger(ExcelExport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new Exception(ex);
        }
    }

    /**
     * Usual Gateway Routine, optimized for running not inside the the Gateway
     * :)
     *
     * @param gatewayItems
     * @param itemIDs
     * @param destinationFile
     * @param additionalFields
     * @param gatewayConfig
     * @throws APIException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ItemMapperException
     * @throws Exception
     */
    public static void transformFromItems(List<ExternalItem> gatewayItems, String itemIDs,
            File destinationFile, Map<String, String> additionalFields, String gatewayConfig) throws APIException, IOException, ParserConfigurationException, SAXException, ItemMapperException, Exception {
        try {
            initGatewayConfig(gatewayConfig);
            templateFile = getProperty("template");

            // print intro and versioning information
            log("* ********************************************************************* *", 1);
            log("* Enhanced Excel export via Gateway, (c) 2015 PTC Inc.", 1);
            log("* ********************************************************************* *", 1);
            log("* Build Number:            " + BUILD_NUMBER, 1);
            log("* Default Locale:          " + Locale.getDefault(), 1);
            log("* Number of Gateway Items: " + gatewayItems.size(), 1);
            // log("* Transformer Key:         " + getTransformerKey(), 1);
            log("* Template Name:           " + templateFile, 1);
            // log("* Temp Directory:          " + super.getTempDirectory().getAbsolutePath(), 1);
            log("* mks.gateway.configdir:   " + System.getProperty("mks.gateway.configdir"), 1);
            log("* destinationFile          " + destinationFile.getAbsolutePath(), 1);
            // log("* configProperty:          " + super.getConfigParam("config"), 1);
            log("* ********************************************************************* *", 1);

            // mappingConfig = null; // getMappingConfiguration();
            // iGatewayDriver = null;// getDriver();
            // individual properties
            lockPassword = getProperty("lockPassword", lockPassword);

            // Retrieve Gateway Config Properties
            log("Listing local configuration ...", 1);
            // List all Gateway Config Properties
            for (Object key : getConfigProperties().entrySet()) {
                log("  ConfigProperty: " + key.toString(), 2);
            }
            // Read Configuration Details
            log("End of Listing local configuration.", 1);
            // listMappingConfig(mappingConfig, "default");

            String exportTypeString = getProperty("type", "");
            // exportExcel(gatewayItems, destinationFile, getConfigProperties(), asOfDate, );
            ExportType exportType = ExportType.valueOf(exportTypeString);
            Boolean lockSheet = getProperty("lockSheet", "false").equals("true");

            IntegritySession.connect();

            if (exportType.equals(ExportType.DefectLeakage)) {
                handleDefectLeakage(gatewayItems, itemIDs);
            } else if (exportType.equals(ExportType.MainSubIssue)) {
                handleMainSubIssues(gatewayItems, itemIDs, IntegritySession.getItemMapperConfig());
            } else if (exportType.equals(ExportType.TestResultHistory)) {
                gatewayItems.add(getFirstItem(itemIDs));
                ReportTestResultHistory.initDoc(gatewayItems.get(0), itemIDs);
                handleTestResultHistory(gatewayItems.get(0));
            }

            // now, that we have all data, we can close the session
            IntegritySession.release();
            // adding date field
            Date asOfDate = (Date) gatewayItems.get(0).getAttributeValue(ExternalItem.Attribute.ASOF_DATE);
            gatewayItems.get(0).add("Export Date", Config.dfDayTimeShort.format(new Date()));

            // for debugging only, will do nothing on server 
            writeIIFtoDisk(gatewayItems, 5);

            log("before writeXLSwithData ..", 2);
            writeXLSwithData(gatewayItems.get(0), templateFile, destinationFile, exportType, lockSheet);
            log("after writeXLSwithData ..", 2);

        } catch (ItemMapperException ex) {
            Logger.getLogger(ExcelExport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Read the first item from the item list provided and creates an
     * ExternalItem
     *
     * @param itemIDs
     * @return
     * @throws APIException
     * @throws UnsupportedPrototypeException
     * @throws ItemMapperException
     */
    public static ExternalItem getFirstItem(String itemIDs) throws APIException, UnsupportedPrototypeException, ItemMapperException {
        Command cmd = new Command(Command.IM, "issues");
        cmd.addOption(new Option("fields", "Project,ID,Summary,Assigned User,Type,State"));
        for (String id : itemIDs.split(",")) {
            cmd.addSelection(id);
        }
        Response response = IntegritySession.execute(cmd);
        WorkItemIterator wit1 = response.getWorkItems();
        while (wit1.hasNext()) {
            WorkItem wi = wit1.next();
            ExternalItem ei = new ExternalItem("ISSUE", wi.getId());
            Iterator fields = wi.getFields();
            while (fields.hasNext()) {
                Field field = (Field) fields.next();
                ei.add(field.getName(), field.getValueAsString());
                // System.out.println("field.getName(): " + field.getName() + ": " + field.getValueAsString());
            }
            // gatewayItems.add
            return ei;
        }
        return null;
    }

    /**
     * Generates the physical Excel file from the provided template
     *
     * @param gatewayItems The Input
     * @param destinationFile
     * @param configProps The export properties
     * @param asOfDate The as of date, when empty the current is taken
     * @param lock
     */
//    public static void exportExcel(List<ExternalItem> gatewayItems, File destinationFile, Properties configProps, Date asOfDate, Boolean lock) {
//        try {
//            int exportMode = 1;
//
//            // String outPath = "";
//            String exportType = getProperty("type", "");
//
//            docItem = gatewayItems.get(0);
//            // Date asOfDate = (Date) docItem.getAttributeValue(ExternalItem.Attribute.ASOF_DATE);
//
////            File f = new File("C:\\IntegrityExcelExport");
////            if (f.exists() && f.isDirectory()) {
////                docType = docItem.getField("Type").getStringValue();
////                outFile = outPath + "\\" + validFileName(docType + "_" + docItem.getInternalId()) + ".xlsx";
////            } else {
////                outFile = configProps.getProperty("file", "../data/tmp/web/outfile.xls");
////            }
//            if (exportType.contentEquals("Chart")) {
//                // Case 2: Charting for Test Objectives
//                // adds the computed fields history as Content Sub Items
//                // handleCompFieldsHist(docItem.getId().getInternalID());
//                exportMode = 2;
//                // writeXLSwithData(docItem, templateFile, outFile, 2);
////            } else if (exportType.contentEquals("Trace")) {
////                // Case 3: Trace Export
////                TraceDefinitionList traceDefList = new TraceDefinitionList(configProps.getProperty("traceDefinition"));
////                DocNodeIterators.init(imSession, iGatewayDriver, mappingConfig, null, traceDefList);
////                if (!traceDefList.isEmpty()) {
////                    // newDocItem = getItemCopy(currDocItem);
////                    // buildNewDocList(currDocItem, newDocItem, asOfDate);
////                    DocNodeIterators.iterateChilds(docItem, asOfDate, "Text", false, false);
////                    DocNodeIterators.rebuildChildListWithTraces(docItem, false, asOfDate);
////                    // writeXLSwithData(docItem, templateFile, outFile, 1);
////                }
//            } else if (exportType.contentEquals("DefectLeakage")) {
//
//                // DocNodeIterators.init(imSession, iGatewayDriver, mappingConfig, null, null);
//                handleDefectLeakage(docItem);
//                exportMode = 3;
//
//            } else if (exportType.contentEquals("TestResultHistory")) {
//
//                // DocNodeIterators.init(imSession, iGatewayDriver, mappingConfig, null, null);
//                log("before TestResultHistory ..", 2);
//                handleTestResultChart(docItem);
//                log("after TestResultHistory ..", 2);
//                exportMode = 5;
//
//                // Support for Excel Documents    
//            } else if (exportType.contentEquals("Document")) {
//                // DocNodeIterators.init(imSession, iGatewayDriver, mappingConfig, null, null);
//                exportMode = 6;
//            }
//
//            log("before writeXLSwithData ..", 2);
//            writeXLSwithData(docItem, templateFile, destinationFile, exportMode, lock);
//            log("after writeXLSwithData ..", 2);
//
//        } catch (ItemMapperException ex) {
//            Logger.getLogger(ExcelExport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        } catch (APIException ex) {
//            Logger.getLogger(ExcelExport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        } catch (IOException ex) {
//            Logger.getLogger(ExcelExport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }
    /**
     * Main export routine, which will write the physical Excel out file
     *
     * @param source
     * @param templateFile
     * @param targetfile
     */
    static void writeXLSwithData(ExternalItem source, String templateFile, File targetfile, ExportType exportType, Boolean lock) throws MalformedURLException, IOException, ItemMapperException {
        log("INFO: Exporting Integrity Data into Excel file " + targetfile + " ...", 1);
        // String[] resultFilter = {""};
        // String itemId = source.getInternalId();

        if (targetfile.exists() && !canWriteFile(targetfile)) {
            // updateProgress(1, 1);
            log("ERROR: Please close the Excel file " + targetfile + " first, can not write to it!", 1);
            return;
        }

        ExcelWorkbook workbook = new ExcelWorkbook(templateFile, targetfile, getProperty("headerRow"));

        // Fill all document item fields in the Excel ...
        workbook.getSheet().setDocumentValues(source);

        if (exportType.equals(ExportType.DefectLeakage)) {
            workbook.getSheet().retrieveFieldNameList(Integer.parseInt(getProperty("headerRow", "8")));
        } else {
            // rename sheet name
            // String sheetName = "Doc " + itemId;
            // workbook.getWorkbook().setSheetName(0, sheetName);
        }

        // Fill now the table
        log("Fill now the table with exportRow in mode '" + exportType.name() + "' ...", 2);
        workbook.getSheet().exportRow(source, exportType);
        workbook.getSheet().clearRemainingTags();

        workbook.release();

        if (workbook.getSheet().getCntRows() == 0) {
            log("ERROR: No file written, possibly no data selected?", 1);
        } else {
            if (lock) {
                ExcelTools.lockAll(workbook, lockPassword);
            }

            // delete the template rows
            if (!exportType.equals(ExportType.DefectLeakage)) {
                workbook.getSheet().deleteTemplateRows();
            }
            workbook.write();
        }
    }

    public static void writeIIFtoDisk(List<ExternalItem> gatewayItems, int id) {
        String path = "C:\\IntegrityWordExport\\";

        File f = new File(path);
        if (f.exists() && f.isDirectory()) {

            try {
                // File out = File.createTempFile("c:\\temp\\WORDEXPORT_", ".iif");
                File out = new File(path + "WordExport_iif_" + Integer.toString(id) + ".xml");
                ItemXMLWriter xmlWriter = new ItemXMLWriter(out, ItemXMLConstants.SCHEMA_VERSION_1_0);
                xmlWriter.write(gatewayItems);
                log("DEBUG: Successfully created debug iif-file " + out.getAbsolutePath(), 5);
            } catch (ItemXMLWriter.ItemXMLWriterException | ItemMapperException e) {
                log(e.getMessage(), 10);
            }
        }
    }
}
