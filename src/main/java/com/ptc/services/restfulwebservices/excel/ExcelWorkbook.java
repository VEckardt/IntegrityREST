/*
 * Copyright:      Copyright 2015 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         V. Eckardt, Senior Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:16CEST $
 */
package com.ptc.services.restfulwebservices.excel;

// import com.mks.gateway.data.ExternalItem;
// import com.mks.gateway.mapper.ItemMapperException;
import static com.ptc.services.restfulwebservices.tools.LogAndDebug.log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author veckardt
 */
public class ExcelWorkbook {

    //
    // public static int rownum = 0;
    // public static int cntRows = 0;
    // 
    private InputStream inputHttpStream;
    private FileInputStream inputDocStream;
    private XSSFWorkbook workbook;
    private final ExcelSheet sheet; // = new ExcelSheet(this, headerRow);
    private final File targetFile;

    public ExcelSheet getSheet() {
        return sheet;
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public ExcelWorkbook(String templateFile, File targetfile, String headerRow) {
        if (templateFile.toLowerCase().startsWith("http")) {
            try {
                // URL url = new URL(URLEncoder.encode(templateFile, "UTF-8"));
                URL url = new URL(templateFile.replace(" ", "%20"));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                inputHttpStream = urlConnection.getInputStream();
                //Access the workbook
                System.out.println("templateFile: " + templateFile);
                workbook = new XSSFWorkbook(inputHttpStream);
            } catch (IOException ex) {
                Logger.getLogger(ExcelExport.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                //Read the spreadsheet that needs to be updated
                inputDocStream = new FileInputStream(new File(templateFile));
                workbook = new XSSFWorkbook(inputDocStream);
            } catch (IOException ex) {
                Logger.getLogger(ExcelExport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        targetFile = targetfile;
        sheet = new ExcelSheet(workbook, headerRow);
        sheet.findDetailBlock();
    }

    /**
     * recalcWorkbook
     *
     */
    public void recalc() {
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            for (Row r : sheet) {
                for (Cell c : r) {
                    if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }
        }
    }

    public void release() {
        log("Closing input streams ...", 2);
        if (inputDocStream != null) {
            try {
                inputDocStream.close();
            } catch (IOException ex) {
                Logger.getLogger(ExcelWorkbook.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (inputHttpStream != null) {
            try {
                inputHttpStream.close();
            } catch (IOException ex) {
                Logger.getLogger(ExcelWorkbook.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void write() throws FileNotFoundException, IOException {
        // Create the output file
        FileOutputStream out = new FileOutputStream(targetFile);
        {
            recalc();
            workbook.write(out);
        }
        log("INFO: Excel file " + targetFile.getAbsolutePath() + " written successfully.", 1);
    }
}
