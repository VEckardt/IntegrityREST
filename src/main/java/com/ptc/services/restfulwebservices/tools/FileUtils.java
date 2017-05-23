/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.restfulwebservices.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author veckardt
 */
public class FileUtils {

    private FileUtils() {
        // to disallow class instanciation
    }

    /**
     * validFileName
     *
     * @param inName
     * @return
     */
    public static String validFileName(String inName) {
        return inName.replaceAll("[^\\dA-Za-z_ ]", "").replaceAll("\\s+", "_");
    }

    /**
     * canWriteFile2
     *
     * @param targetFile
     * @return
     */
//    public static Boolean canWriteFile2(String targetFile) {
//        try {
//            FileOutputStream out = new FileOutputStream(new File(targetFile));
//            out.close();
//            return true;
//        } catch (FileNotFoundException ex) {
//            return false;
//        } catch (IOException ex) {
//            return false;
//        }
//    }
    /**
     * canWriteFile
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Boolean canWriteFile(File file) {
//        boolean res = false;
//        FileChannel channel = new RandomAccessFile(new File(file), "rw").getChannel();
//        // Get an exclusive lock on the whole file
//        FileLock lock = channel.lock();
//        try {
//            //The file is not already opened 
//            lock = channel.tryLock();
//        } catch (OverlappingFileLockException e) {
//            // File is open by someone else
//            res = true;
//        } finally {
//            lock.release();
//        }
//        return res;
        // String fileName = "C:\\IntegrityWordExport\\SystemIntegrationTestDocument_TestSuite_539.docx";
        log("Checking read-write for file " + file.getAbsolutePath(), 1);
        // File file = new File(fileName);
        if (!file.exists()) {
            return true;
        }

        // try to rename the file with the same name
        File sameFileName = new File(file.getAbsolutePath());
        return file.renameTo(sameFileName);

    }

    /**
     * openWindowsFile
     *
     * @param type
     * @param filePathAndName
     */
    public static void openWindowsFile(String type, String filePathAndName) {
        try {
            log("Opening " + type + " file ...", 2);
            Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + filePathAndName);
            p.waitFor();

            // info.setText("Opening Document " + documentID + " in MS Word ...");
            // String command = "\"" + cgp.propMSWordPath + "\" " + filePathAndName;
            // log("Opening in MS Word ...", 2);
            // log(command, 3);
            // OSCommandHandler osh = new OSCommandHandler(resultFilter);
            // int retCode = osh.executeCmd(command, false);
            // log("Return code: " + retCode, 3);
            // log("Return text: \n" + osh.getUnfilteredResult(), 3);
            log("End of Opening " + type + " file.", 2);
        } catch (IOException ex) {
            // Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            log(ex.getMessage(), 2);
        } catch (InterruptedException ex) {
            // Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            log(ex.getMessage(), 2);
        }
    }

    public static void updateContentAndGeneratePDF(Boolean genPDF, String filePathAndPath) {

        if (filePathAndPath.endsWith(".docx")) {

            try {

                File pdfFile = new File(filePathAndPath.replace(".docx", ".pdf"));
                if (genPDF) {
                    if (pdfFile.exists()) {
                        pdfFile.delete();
                    }
                }

                String[] cmds = new String[5];
                cmds[0] = "powershell.exe";
                cmds[1] = "-executionpolicy";
                cmds[2] = "ByPass";
                cmds[3] = "-Command";
                cmds[4] = "$filename = '" + filePathAndPath + "';"
                        + "$word_app = New-Object -ComObject Word.Application; $document = $word_app.Documents.Open($filename);"
                        + "if ($document.TablesOfContents -ne $null) {$document.TablesOfContents.item(1).Update();};";
                if (genPDF) {
                    cmds[4] = cmds[4]
                            + "$pdf_filename = '" + filePathAndPath.replace(".docx", ".pdf") + "';"
                            + "$document.SaveAs([ref] $pdf_filename, [ref] 17);";

                }
                cmds[4] = cmds[4] + "$document.Close();";
                cmds[4] = cmds[4] + "$word_app.quit();";
                Runtime runtime = Runtime.getRuntime();
                Process proc = runtime.exec(cmds);
                proc.getOutputStream().close();
                proc.waitFor();

                int retCode = proc.exitValue();
                InputStream inputstream = proc.getErrorStream();
                InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
                BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                String line;
                while ((line = bufferedreader.readLine()) != null) {
                    // System.out.println(line);
                    log(line, 2);
                }
                if (genPDF) {
                    if (pdfFile.exists()) {
                        log("New PDF File '" + pdfFile.getAbsolutePath() + " generated.", 1);
                    } else {
                        log("Unable to generate PDF File '" + pdfFile.getAbsolutePath(), 1);
                    }
                }

            } catch (InterruptedException ex) {
                error(ex);
            } catch (IOException ex) {
                error(ex);
            }
        } else {
            log("INFO: PDF Generation is only supported for Word Files.", 2);
        }
    }

    public static void updateContentAndGeneratePDFWithBookmarks(Boolean genPDF, String filePathAndPath) {

        if (filePathAndPath.endsWith(".docx")) {

            try {

                File pdfFile = new File(filePathAndPath.replace(".docx", ".pdf"));
                if (genPDF) {
                    if (pdfFile.exists()) {
                        pdfFile.delete();
                    }
                }

                String[] cmds = new String[5];
                cmds[0] = "powershell.exe";
                cmds[1] = "-executionpolicy";
                cmds[2] = "ByPass";
                cmds[3] = "-Command";
                cmds[4] = "";

                cmds[4] = cmds[4] + "Add-type -AssemblyName Microsoft.Office.Interop.Word\n";
                cmds[4] = cmds[4] + "\n";
                cmds[4] = cmds[4] + "$wdSourceFile = '" + filePathAndPath + "'\n";
                cmds[4] = cmds[4] + "$wdExportFile = '" + filePathAndPath.replace(".docx", ".pdf") + "'\n";
                cmds[4] = cmds[4] + "\n";
                cmds[4] = cmds[4] + "$wdExportFormat = [Microsoft.Office.Interop.Word.WdExportFormat]::wdExportFormatPDF\n";
                cmds[4] = cmds[4] + "$wdOpenAfterExport = $false\n";
                cmds[4] = cmds[4] + "$wdExportOptimizeFor = [Microsoft.Office.Interop.Word.WdExportOptimizeFor]::wdExportOptimizeForOnScreen\n";
                cmds[4] = cmds[4] + "$wdExportRange = [Microsoft.Office.Interop.Word.WdExportRange]::wdExportAllDocument\n";
                cmds[4] = cmds[4] + "$wdStartPage = 0\n";
                cmds[4] = cmds[4] + "$wdEndPage = 0\n";
                cmds[4] = cmds[4] + "$wdExportItem = [Microsoft.Office.Interop.Word.WdExportItem]::wdExportDocumentContent\n";
                cmds[4] = cmds[4] + "$wdIncludeDocProps = $true\n";
                cmds[4] = cmds[4] + "$wdKeepIRM = $true\n";
                // this is the important line here!
                cmds[4] = cmds[4] + "$wdCreateBookmarks = [Microsoft.Office.Interop.Word.WdExportCreateBookmarks]::wdExportCreateWordBookmarks\n";
                cmds[4] = cmds[4] + "$wdDocStructureTags = $true\n";
                cmds[4] = cmds[4] + "$wdBitmapMissingFonts = $true\n";
                cmds[4] = cmds[4] + "$wdUseISO19005_1 = $false\n";
                cmds[4] = cmds[4] + "\n";
                cmds[4] = cmds[4] + "$wdApplication = $null;\n";
                cmds[4] = cmds[4] + "$wdDocument = $null;\n";
                cmds[4] = cmds[4] + "\n";
                cmds[4] = cmds[4] + " try \n";
                cmds[4] = cmds[4] + "{\n";
                cmds[4] = cmds[4] + "       $wdApplication = New-Object -ComObject \"Word.Application\"\n";
                cmds[4] = cmds[4] + "       $wdDocument = $wdApplication.Documents.Open($wdSourceFile)\n";
                cmds[4] = cmds[4] + "       if ($wdDocument.TablesOfContents -ne $null) {$wdDocument.TablesOfContents.item(1).Update();};";
                cmds[4] = cmds[4] + "       $wdDocument.ExportAsFixedFormat(\n";
                cmds[4] = cmds[4] + "       $wdExportFile,\n";
                cmds[4] = cmds[4] + "       $wdExportFormat,\n";
                cmds[4] = cmds[4] + "       $wdOpenAfterExport,\n";
                cmds[4] = cmds[4] + "       $wdExportOptimizeFor,\n";
                cmds[4] = cmds[4] + "       $wdExportRange,\n";
                cmds[4] = cmds[4] + "       $wdStartPage,\n";
                cmds[4] = cmds[4] + "       $wdEndPage,\n";
                cmds[4] = cmds[4] + "       $wdExportItem,\n";
                cmds[4] = cmds[4] + "       $wdIncludeDocProps,\n";
                cmds[4] = cmds[4] + "       $wdKeepIRM,\n";
                cmds[4] = cmds[4] + "       $wdCreateBookmarks,\n";
                cmds[4] = cmds[4] + "       $wdDocStructureTags,\n";
                cmds[4] = cmds[4] + "       $wdBitmapMissingFonts,\n";
                cmds[4] = cmds[4] + "       $wdUseISO19005_1\n";
                cmds[4] = cmds[4] + "       )\n";
                cmds[4] = cmds[4] + "}\n";
                cmds[4] = cmds[4] + " catch \n";
                cmds[4] = cmds[4] + "{\n";
                cmds[4] = cmds[4] + "       $wshShell = New-Object -ComObject WScript.Shell\n";
                cmds[4] = cmds[4] + "       $wshShell.Popup($_.Exception.ToString(), 0, 'Error', 0)\n";
                cmds[4] = cmds[4] + "       $wshShell = $null\n";
                cmds[4] = cmds[4] + "}\n";
                cmds[4] = cmds[4] + "finally\n";
                cmds[4] = cmds[4] + "{\n";
                cmds[4] = cmds[4] + "       if ($wdDocument)\n";
                cmds[4] = cmds[4] + "       {\n";
                cmds[4] = cmds[4] + "              $wdDocument.Close()\n";
                cmds[4] = cmds[4] + "              $wdDocument = $null\n";
                cmds[4] = cmds[4] + "       }\n";
                cmds[4] = cmds[4] + "       if ($wdApplication)\n";
                cmds[4] = cmds[4] + "       {\n";
                cmds[4] = cmds[4] + "              $wdApplication.Quit()\n";
                cmds[4] = cmds[4] + "              $wdApplication = $null\n";
                cmds[4] = cmds[4] + "       }\n";
                cmds[4] = cmds[4] + "       [GC]::Collect()\n";
                cmds[4] = cmds[4] + "       [GC]::WaitForPendingFinalizers()\n";
                cmds[4] = cmds[4] + "}\n";

                Runtime runtime = Runtime.getRuntime();
                Process proc = runtime.exec(cmds);
                proc.getOutputStream().close();
                proc.waitFor();

                int retCode = proc.exitValue();
                InputStream inputstream = proc.getErrorStream();
                InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
                BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                String line;
                while ((line = bufferedreader.readLine()) != null) {
                    // System.out.println(line);
                    log(line, 2);
                }
                if (genPDF && pdfFile.exists()) {
                    log("New PDF File '" + pdfFile.getAbsolutePath() + " generated.", 1);
                }

            } catch (InterruptedException ex) {
                error(ex);
            } catch (IOException ex) {
                error(ex);
            }
        } else {
            log("INFO: PDF Generation is only supported for Word Files.", 2);
        }
    }

    /**
     * getStringFromInputStream
     *
     * @param is
     * @return
     */
    public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            // br.
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private static void error(Exception ex) {
        Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
    }

    private static void log(String text, int level) {
        Logger.getLogger(FileUtils.class.getName()).info(text);
    }
}
