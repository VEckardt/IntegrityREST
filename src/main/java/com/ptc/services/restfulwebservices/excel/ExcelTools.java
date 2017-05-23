/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:16CEST $
 */
package com.ptc.services.restfulwebservices.excel;

import static java.lang.System.out;

/**
 *
 * @author veckardt
 */
public class ExcelTools {

    public static void lockAll(ExcelWorkbook workbook, String lockPassword) {
        // String password = "abcd";
//        byte[] pwdBytes = null;
//        try {
//            pwdBytes = Hex.decodeHex(lockPassword.toCharArray());
//        } catch (DecoderException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

        //  xssfSheet.enableLocking();
        //  CTSheetProtection sheetProtection = xssfSheet.getCTWorksheet().getSheetProtection();
        //  sheetProtection.setSelectLockedCells(true); 
        //  sheetProtection.setSelectUnlockedCells(false); 
        //  sheetProtection.setFormatCells(true); 
        //  sheetProtection.setFormatColumns(true); 
        //  sheetProtection.setFormatRows(true); 
        //  sheetProtection.setInsertColumns(true); 
        //  sheetProtection.setInsertRows(true); 
        //  sheetProtection.setInsertHyperlinks(true); 
        //  sheetProtection.setDeleteColumns(true); 
        //  sheetProtection.setDeleteRows(true); 
        //  sheetProtection.setSort(false); 
        //  sheetProtection.setAutoFilter(false); 
        //  sheetProtection.setPivotTables(true); 
        //  sheetProtection.setObjects(true); 
        //  sheetProtection.setScenarios(true);        
        // XSSFSheet sheet = ((XSSFSheet) s);
        // removePivot(s,workbookx);
        workbook.getSheet().getSheet().lockDeleteColumns(true);
        workbook.getSheet().getSheet().lockDeleteRows(true);
        workbook.getSheet().getSheet().lockFormatCells(true);
        workbook.getSheet().getSheet().lockFormatColumns(true);
        workbook.getSheet().getSheet().lockFormatRows(true);
        workbook.getSheet().getSheet().lockInsertColumns(true);
        workbook.getSheet().getSheet().lockInsertRows(true);

        // Biff8EncryptionKey.setCurrentUserPassword("secret");    
        // sheet.getCTWorksheet().getSheetProtection().setPassword(pwdBytes);
        // for (byte pwdChar : pwdBytes) {
        out.println("Excel Sheet protected with Password.");
        // }
        workbook.getSheet().getSheet().enableLocking();
        workbook.getSheet().getSheet().protectSheet(lockPassword);

        workbook.getWorkbook().lockStructure();
        // workbookx.writeProtectWorkbook(Biff8EncryptionKey.getCurrentUserPassword(), "");
    }
}
