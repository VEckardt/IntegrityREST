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

import com.mks.gateway.data.ExternalItem;
import com.mks.gateway.mapper.ItemMapperException;
import com.ptc.services.restfulwebservices.excel.ExcelExport.ExportType;
import com.ptc.services.restfulwebservices.tools.LogAndDebug;
import static com.ptc.services.restfulwebservices.tools.LogAndDebug.log;
import static com.ptc.services.restfulwebservices.tools.PrefixingHandler.addIdPrefix;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.unbescape.html.HtmlEscape;

/**
 *
 * @author veckardt
 */
public class ExcelSheet {

    private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
    private static final String beginTag = "<%";
    private static final String endTag = "%>";
    private int beginContentRow = 0;
    private int endContentRow = 0;
    private int rownum = 0;
    private int cntRows = 0;
    private final String headerRow;
    private String fieldNameList = "";
    private String itemType = "";
    private String firstItemType = "";
    private int colOffset = 0;
    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;

    public ExcelSheet(XSSFWorkbook theWorkbook, String theHeaderRow) {
        workbook = theWorkbook;
        headerRow = theHeaderRow;
        beginContentRow = 0;
        endContentRow = 0;
        rownum = 0;
        cntRows = 0;
        sheet = workbook.getSheetAt(0);
    }

    public int getCntRows() {
        return cntRows;
    }

    public XSSFSheet getSheet() {
        return sheet;
    }

    /**
     *
     * @param rowIndex
     */
    public void deleteRow(int rowIndex) {
        int lastRowNum = sheet.getLastRowNum();
        // LogAndDebug.log(endTag);
        // LogAndDebug.log("Deleting row " + (rowIndex + 1) + " ...", 2);
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        }
        if (rowIndex == lastRowNum) {
            XSSFRow removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
    }

    public void deleteTemplateRows() {
        log("Deleting template rows from row " + endContentRow + " down to row " + beginContentRow + " ...", 1);
        for (int k = endContentRow; k >= beginContentRow; k--) {
            deleteRow(k);
        }
    }

    public void setDocumentValues(ExternalItem source) throws ItemMapperException {
        log("Fill all document item fields in the Excel ...", 2);
        // fill all header fields in the Excel
        for (int row = 0; row <= beginContentRow; row++) {
            if (sheet.getRow(row) != null) {
                for (int col = 0; col <= sheet.getRow(row).getLastCellNum(); col++) {
                    setValueIntoTag(source, row, col);
                }
            }
        }
    }

    /**
     * removeTags
     *
     * @param string
     * @return
     */
    public static String removeTags(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }

        Matcher m = REMOVE_TAGS.matcher(string);
        return m.replaceAll("");
    }

    /**
     * setValue
     *
     * @param sheet
     * @param row
     * @param col
     * @param value
     */
    public void setValue(XSSFSheet sheet, int row, int col, String value) {
        try {

            try {
                Integer.parseInt(value);
                sheet.getRow(row).getCell(col).setCellValue(Integer.parseInt(value));
            } catch (NumberFormatException ex) {
                // XSSFRichTextString s1 = new XSSFRichTextString(StringEscapeUtils.unescapeHtml4(value));
                // VE: sheet.getRow(row).getCell(col).setCellValue(StringEscapeUtils.unescapeHtml4(value)); 
                sheet.getRow(row).getCell(col).setCellValue(HtmlEscape.unescapeHtml(value));
            }
        } catch (NullPointerException ex) {
            LogAndDebug.log("ERROR in setValue: Can not set (row=" + (row + 1) + ", col=" + (col + 1) + "): Value '" + value + "'", 1);
            // System.exit(1);
        }
    }

    /**
     *
     * @param rowNumber
     */
    public void retrieveFieldNameList(int rowNumber) {
        // String fieldNameList ="";
        // log("worksheet.getRow(" + rowNumber + ").getLastCellNum():" + worksheet.getRow(rowNumber).getLastCellNum(), 3);
        for (int i = 0; i < sheet.getRow(rowNumber).getLastCellNum(); i++) {
            try {

                // log("Gettign text from row " + rowNumber + ", cell " + i + ": " + worksheet.getRow(rowNumber).getCell(i).getCellType(), 3);
                String cellValue;
                if (sheet.getRow(rowNumber).getCell(i).getCellType() == 0) {
                    cellValue = Double.toString(sheet.getRow(rowNumber).getCell(i).getNumericCellValue());
                } else {
                    cellValue = sheet.getRow(rowNumber).getCell(i).getStringCellValue();
                }
                // log("cellValue = " + cellValue, 3);

                if (cellValue.startsWith(beginTag) && cellValue.endsWith(endTag)) {
                    cellValue = cellValue.replace(beginTag, "");
                    cellValue = cellValue.replace(endTag, "");
                    fieldNameList = fieldNameList + (fieldNameList.isEmpty() ? "" : ",") + cellValue;
                    if (colOffset == 0) {
                        colOffset = i;
                    }
                }
            } catch (NullPointerException ex) {
                LogAndDebug.log("ERROR in retrieveFieldNameList: Can not get value from (row=" + (rowNumber + 1) + ", col=" + (i + 1) + ")", 1);
            }
        }
        // LogAndDebug.log("fieldNameList: " + fieldNameList, 3);
        // LogAndDebug.log("colOffset: " + colOffset, 3);
        // return fieldNameList;
    }

    /**
     * Puts a value into a matching tag
     *
     * @param source
     * @param rowNum
     * @param colNum
     * @throws ItemMapperException
     */
    public void setValueIntoTag(ExternalItem source, int rowNum, int colNum) throws ItemMapperException {
        String tag;
        // read the tag at col
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            Cell cell = row.getCell(colNum);
            if (cell != null) {

                try {
                    tag = cell.getStringCellValue();
                } catch (IllegalStateException ex) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    tag = cell.getStringCellValue();
                }

                // log("Tag = " + tag, 1);
                // is it a tag?
                if (tag.startsWith(beginTag) && tag.endsWith(endTag)) {
                    // remove the tag
                    tag = tag.replace(beginTag, "").replace(endTag, "");
                    if (source.hasField(tag)) {
                        // get the field value and set the tag
                        String value = source.getField(tag).getStringValue();
                        setValue(sheet, rowNum, colNum, value);
                    }
                }
            } else {
                // log("Cell " + rowNum + " in Coll " + col + " is null.", 1);
            }
        } else {
            // log("Row " + rowNum + " is null.", 1);
        }
    }

    public void clearCellsWithTag(int row) {

        for (int col = 0; col <= sheet.getRow(beginContentRow + 1).getLastCellNum(); col++) {
            try {
                // read the tag at col
                String tag = sheet.getRow(row).getCell(col).getStringCellValue();
                // is it a tag?
                if (tag.startsWith(beginTag) && tag.endsWith(endTag)) {
                    // remove the tag
                    setValue(sheet, row, col, "");
                }
            } catch (NullPointerException | IllegalStateException ex) {
            }
        }

    }

    /**
     * Find the section within the document with beginContent and endContent
     *
     */
    public void findDetailBlock() {
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            try {
                switch (sheet.getRow(i).getCell(0).getStringCellValue()) {
                    case beginTag + "beginContent" + endTag:
                        LogAndDebug.log("beginContentRow: " + (i + 1), 2);
                        beginContentRow = i;
                        break;
                    case beginTag + "endContent" + endTag:
                        LogAndDebug.log("endContentRow: " + (i + 1), 2);
                        endContentRow = i;
                        break;
                }
            } catch (NullPointerException ex) {
                // skip over any row without any data in it
            }
        }
    }

    /**
     * copyRow
     *
     * @param sourceRowNum
     * @param destinationRowNum
     */
    public void copyRow(int sourceRowNum, int destinationRowNum) {
        // Get the source / new row
        XSSFRow newRow = sheet.getRow(destinationRowNum);
        XSSFRow sourceRow = sheet.getRow(sourceRowNum);

        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null) {
            sheet.shiftRows(destinationRowNum, sheet.getLastRowNum(), 1);
            newRow = sheet.createRow(destinationRowNum);
        } else {
            newRow = sheet.createRow(destinationRowNum);
        }

        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            XSSFCell oldCell = sourceRow.getCell(i);
            XSSFCell newCell = newRow.createCell(i);

            // If the old cell is null jump to next cell
            if (oldCell == null) {
                // newCell = null;
                // log ("Old Cell " + i + " is null, skip over ...", 1);
                continue;
            }

            // Copy style from old cell and apply to new cell
            XSSFCellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);

            // If there is a cell comment, copy
            if (newCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());

            // log ("oldCell.getCellType(): " + oldCell.getCellType(),1);
            // Set the cell data value
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
                // default: 
                //     log ("oldCell.getCellType(): " + oldCell.getCellType(),1);

            }
        }

        // If there are are any merged regions in the source row, copy to new row
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
                        (newRow.getRowNum()
                        + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
                        cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn());
                sheet.addMergedRegion(newCellRangeAddress);
            }
        }

    }

    /**
     * clearRemainingTags
     *
     */
    public void clearRemainingTags() {
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            clearCellsWithTag(i);
        }
    }

    /**
     * Exports a single row from iif to excel
     *
     * @param source
     * @param exportType
     * @throws ItemMapperException
     */
    public void exportRow(ExternalItem source, ExportType exportType) throws ItemMapperException {
        // log ("INFO: exportRow started in mode "+mode, 3);
        if (!source.getChildren().isEmpty()) {
            // walk through all childs
            Iterator<ExternalItem> childs = source.childrenIterator();

            while (childs.hasNext()) {
                cntRows++;
                ExternalItem currentChild = (ExternalItem) childs.next();
                addIdPrefix(currentChild);

                // log(currentChild.getField("Type").getStringValue() + " => " + currentChild.getInternalId(), 1);
                String currentType = "";
                if (currentChild.getField("Type") != null) {
                    currentType = currentChild.getField("Type").getStringValue();
                }
                if (firstItemType.isEmpty()) {
                    firstItemType = currentType;
                }

                //
                // begin with a new row if:
                //  a) same type 
                //  b) at start
                //  c) same as first item type
                // 
                if (currentType.contentEquals(itemType) || itemType.isEmpty() || currentType.contentEquals(firstItemType)) {
                    // log("Same Type: " + itemType, 2);
                    rownum++;
                    if (!exportType.equals(ExportType.DefectLeakage)) {
                        copyRow(beginContentRow + 1, endContentRow + rownum);
                    }
                }
                itemType = currentType;

                if (!exportType.equals(ExportType.DefectLeakage)) {
                    // 1: enter the data
                    // copyRow(workbook, sheet, beginContentRow + 1, endContentRow + rownum);

                    // Set values into Tag
                    for (int i = 0; i <= sheet.getRow(beginContentRow + 1).getLastCellNum(); i++) {
                        setValueIntoTag(currentChild, endContentRow + rownum, i);
                    }
                    exportRow(currentChild, exportType);

                    if (exportType.equals(ExportType.TestResultHistory)) {
                        int col = colOffset;
                        for (String fieldName : fieldNameList.split(",")) {
                            setValue(sheet, Integer.parseInt(headerRow), col++, fieldName);
                        }
                    }

                } else {
                    int col = colOffset;
                    // for (String fieldName : currentChild.getFieldNames()) {
                    for (String fieldName : fieldNameList.split(",")) {
                        setValue(sheet, Integer.parseInt(headerRow), col, fieldName);
                        String value = "";
                        if (currentChild.getField(fieldName) != null) {
                            value = currentChild.getField(fieldName).getStringValue();
                        }

                        setValue(sheet, Integer.parseInt(headerRow) + rownum, col++, value);
                    }
                }
            }
        }
    }
}
