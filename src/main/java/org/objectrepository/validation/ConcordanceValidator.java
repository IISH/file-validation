package org.objectrepository.validation;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.io.*;
import java.util.*;

/**
* Author: Christian Roosendaal
 */

public class ConcordanceValidator {

    // mandatory columns:
    private static final String OBJECT_COLUMN_NAME = "objnr";
    private static final String VOLGNR_COLUMN_NAME = "volgnr";
    private static final String TIF_COLUMN_NAME = "master";

    // optional columns:
    private static final String PID_COLUMN_NAME = "PID";
    private static final String JPEG_COLUMN_NAME = "jpeg";
    private static final String JPEG2_COLUMN_NAME = "jpeg2";
    private static final String OCR_COLUMN_NAME = "OCR";

    private static final String CSV_SEPARATOR = ",";

    private static final String ERROR_FILE_EXISTENCE = "Error: file entry in concordance table does not exist in directory";
    private static final String ERROR_CONCORDANCE_FILE_MISSING = "Error: file in directory is not listed in the concordance table";
    private static final String ERROR_CONCORDANCE_FILE_DUPLICATE = "Duplicate entry in concordance table.";

    private static final String REPORT_FILE = "report.txt";


    private static final int MINIMAL_FILE_SIZE = 1000;

    private static final byte[] MAGIC_NUMBER_TIFF_BIG_ENDIAN = new byte[]{(byte) 0x4D, (byte) 0x4D, (byte) 0x00, (byte) 0x2A};
    private static final byte[] MAGIC_NUMBER_TIFF_LITTLE_ENDIAN = new byte[]{(byte) 0x49, (byte) 0x49, (byte) 0x2A, (byte) 0x00};
    private static final byte[] MAGIC_NUMBER_JPEG = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};


    int objectColumnNr;
    int masterColumnNr;
    int volgNrColumnNr;
    int pidColumnNr;
    int jpegColumnNr;
    int jpeg2ColumnNr;
    int jpeg3ColumnNr;
    int ocrColumnNr;

    boolean objectColumnPresent;
    boolean masterColumnPresent;
    boolean volgNrColumnPresent;
    boolean ocrPresent;
    boolean jpegPresent;
    boolean jpeg2Present;
    boolean pidColumnPresent;

    // for testing purposes:
    public static boolean exitCalled;
    public static boolean warning;
    public static boolean relationshipError;
    public static boolean fileOrHeaderError;
    public static boolean volgnummerError;
    public static boolean headerOrFilesizeError;


    boolean isUnitTesting = false;

    ArrayList<File> subDirList = new ArrayList<File>();

    private String dataDirLoc;
    private String prefix;
    private String pidPrefix;
    private String baseDir;

    File concordanceFile;

    public ConcordanceValidator() {

    }

    public ConcordanceValidator(String dataDirLoc, String prefix, String pidPrefix) {
        this.pidColumnPresent = false;
        this.prefix = prefix;
        this.dataDirLoc = dataDirLoc + File.separator + this.prefix;
        this.exitCalled = false;
        this.pidPrefix = pidPrefix;
        this.baseDir = dataDirLoc;

        String concordanceFileLocation = this.dataDirLoc + File.separator + prefix + ".csv";

        this.concordanceFile = new File(concordanceFileLocation);

        // setup the file to log all output:
        File reportFile = new File(dataDirLoc + File.separator + REPORT_FILE);


    }

    public void start() {

        parseColumns();


        try {

            testVolgnummers();

            testRelationShips();

            testFileExistenceAndTestHeaders(masterColumnNr);

//            testSubdirectories();

            if (jpegPresent) {
                testFileExistenceAndTestHeaders(jpegColumnNr);
//                testSubdirectories();
            }

            if (jpeg2Present) {
                testFileExistenceAndTestHeaders(jpeg2ColumnNr);
//                testSubdirectories();
            }

            if (ocrPresent) {
                testFileExistenceAndTestHeaders(ocrColumnNr);
//                testSubdirectories();
            }


            if (!pidColumnPresent) createPidColumn();

            writeLog("----  All tests passed.  ----");
            writeLog("");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /*
    * Creates an extra column in the concordance table to contain PID numbers, if it is not already there.
    *
    * */
    public void createPidColumn() {

        File tempConcordanceFile = new File(dataDirLoc + File.separator + "concordanceValidWithPID.csv");

        try {
            BufferedReader input = new BufferedReader(new FileReader(concordanceFile));
            BufferedWriter output = new BufferedWriter(new FileWriter(tempConcordanceFile));


            String inputLine;
            String outputLine;

            inputLine = input.readLine();
            if (inputLine.charAt(inputLine.length() - 1) == CSV_SEPARATOR.charAt(0)) {
                outputLine = inputLine + "PID";
            } else {
                outputLine = inputLine + CSV_SEPARATOR + "PID";
            }

            output.write(outputLine + "\r\n");

            while ((inputLine = input.readLine()) != null) {

                final String pid = pidPrefix + "/" + UUID.randomUUID().toString().toUpperCase();
                if (inputLine.charAt(inputLine.length() - 1) == CSV_SEPARATOR.charAt(0)) {
                    outputLine = inputLine + pid;
                } else {
                    outputLine = inputLine + CSV_SEPARATOR + pid;
                }

                output.write(outputLine + "\r\n");
            }

            output.flush();
            input.close();
            output.close();

            concordanceFile = tempConcordanceFile;


        } catch (IOException e) {
            e.printStackTrace();
        }

        writeLog("Creation of PID column containing UUIDs succeeded.");

    }


    public void parseColumns() {
        try {

            BufferedReader input = new BufferedReader(new FileReader(concordanceFile));

            String line = input.readLine();

            String[] columnNames = line.split(CSV_SEPARATOR);

            if (columnNames.length < 3) {
                writeErrorLog("Error: incorrect CSV separator. Expected \";\".");
                exit();
            }

            for (int i = 0; i < columnNames.length; i++) {

                if (columnNames[i].equals(OBJECT_COLUMN_NAME)) {
                    objectColumnNr = i;
                    objectColumnPresent = true;
                } else if (columnNames[i].equals(TIF_COLUMN_NAME)) {
                    masterColumnNr = i;
                    masterColumnPresent = true;
                } else if (columnNames[i].equals(VOLGNR_COLUMN_NAME)) {
                    volgNrColumnNr = i;
                    volgNrColumnPresent = true;
                } else if (columnNames[i].equals(PID_COLUMN_NAME)) {
                    pidColumnNr = i;
                    pidColumnPresent = true;
                } else if (columnNames[i].equals(JPEG_COLUMN_NAME)) {
                    jpegColumnNr = i;
                    jpegPresent = true;
                } else if (columnNames[i].equals(JPEG2_COLUMN_NAME)) {
                    jpeg2ColumnNr = i;
                    jpeg2Present = true;
                } else if (columnNames[i].equals(OCR_COLUMN_NAME)) {
                    ocrColumnNr = i;
                    ocrPresent = true;
                }

            }

            if (!objectColumnPresent || !masterColumnPresent || !volgNrColumnPresent) {

                writeErrorLog("One of the mandatory colunms " + OBJECT_COLUMN_NAME + ", "
                        + TIF_COLUMN_NAME + ", " + VOLGNR_COLUMN_NAME + " is missing in the concordance table");
                exit();

            }


        } catch (IOException e) {

            writeErrorLog("Error: Concordance table file not found or cannot read file: " + concordanceFile);
            exit();

        }

        writeLog("Parsing columns complete. No errors detected.");


    }


    // check if every file in all subdirectories have the right prefix.
    public void testSubdirectories() {

        for (File dir : subDirList) {

            File[] fileList = dir.listFiles();

            for (File objectDir : fileList) {

                String[] objects = objectDir.list();

                for (String object : objects) {

                    String prefixOfFile = object.split("_")[0];
                    if (!prefixOfFile.equals(prefix)) {

                        writeErrorLog("Error : file " + object + " has incorrect prefix. Expected \"" + prefix + "\". Encountered: \"" + prefixOfFile + "\"");
                        exit();

                    }
                }


            }

        }


        writeLog("Subdirectory prefix test passed. All data files have the right prefix: \"" + prefix + "\"");


    }


    public void testRelationShips() {

        BufferedReader input = null;
        int lineNr = 2;
        String line;

        try {
            input = new BufferedReader(new FileReader(concordanceFile));

            // skip the first line containing column names:
            input.readLine();

            while ((line = input.readLine()) != null) {

                String[] columns = line.split(CSV_SEPARATOR);

                String tifImage = columns[masterColumnNr].split("\\.")[0];
                String jpegImage = columns[jpegColumnNr].split("\\.")[0];

                String[] tifImageArray = tifImage.split("/");

                if (tifImageArray.length < 2) {
                    writeErrorLog("Incorrect path name for master image at line " + lineNr + ", column " + masterColumnNr);
                    relationshipError = true;
                }

                tifImage = tifImageArray[tifImageArray.length - 1];

                String[] jpegImageArray = jpegImage.split("/");

                if (jpegImageArray.length < 2) {
                    writeErrorLog("Incorrect path name for jpeg image at line " + lineNr + ", column " + jpegColumnNr);
                    relationshipError = true;
                }

                jpegImage = jpegImageArray[jpegImageArray.length - 1];


                if (!tifImage.equals(jpegImage)) {
                    writeErrorLog("Warning: Difference in filenames between " + tifImage + " and " + jpegImage + " at line " + lineNr);
                    relationshipError = true;
                }

                if (jpeg2Present) {
                    String jpeg2Image = columns[jpeg2ColumnNr].split("\\.")[0];

                    String[] jpeg2ImageArray = jpeg2Image.split("/");

                    if (jpegImageArray.length < 2) {
                        writeErrorLog("Incorrect path name for jpeg2 image at line " + lineNr + ", column " + jpeg2ColumnNr);
                        relationshipError = true;
                    }

                    jpeg2Image = jpeg2ImageArray[jpeg2ImageArray.length - 1];

                    if (!jpeg2Image.equals(jpegImage)) {
                        writeErrorLog("Warning: Difference in filenames between " + jpeg2Image + " and " + jpegImage + " at line " + lineNr);
                        relationshipError = true;
                    }
                }


                if (ocrPresent) {
                    String ocr = columns[ocrColumnNr].split("\\.")[0];
                    String[] ocrArray = ocr.split("/");

                    if (ocrArray.length < 2) {
                        writeErrorLog("Incorrect path name at line " + lineNr + ", column " + ocrColumnNr);
                        relationshipError = true;
                    }

                    ocr = ocrArray[ocrArray.length - 1];

                    if (!ocr.equals(jpegImage)) {
                        writeErrorLog("Warning: Difference in filenames between " + ocr + " and " + jpegImage + " at line " + lineNr);
                        relationshipError = true;
                    }
                }


                lineNr++;

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!relationshipError) {
            writeLog("Relationship test between columns test passed. All image files denoted in the table correspond to each other.");
        }
    }

    // checks if the volgnummers and objectnummers are in correct order.
    // i.e.: (objectnummer:volgnummer) 1:1, 1:2, 1:3, 2:1, 2:2, 2:3, 3:1, ...,
    public void testVolgnummers() {
        BufferedReader input = null;
        String line;
        int lineNr = 2;
        int expectedVolgNr = 1;
        int expectedObjNr = 1;
        ArrayList<ObjectNumber> numberList = new ArrayList<ObjectNumber>();


        try {
            input = new BufferedReader(new FileReader(concordanceFile));

            // skip the first line containing column names:
            input.readLine();

            while ((line = input.readLine()) != null) {

                String[] columns = line.split(CSV_SEPARATOR);
                String volgNr = columns[volgNrColumnNr];
                String objNr = columns[objectColumnNr];
                int volgNrParsed = 0;
                int objNrParsed = 0;

                // try parsing the volgnummer, throw error if not a number:
                try {
                    volgNrParsed = Integer.parseInt(volgNr);
                } catch (NumberFormatException e) {
                    writeErrorLog("Error: incorrect entry in volgnummer column at line " + lineNr);
                    volgnummerError = true;
                }
                // try parsing the objectnummer, throw error if not a number:
                try {
                    objNrParsed = Integer.parseInt(objNr);
                } catch (NumberFormatException e) {
                    writeErrorLog("Error: incorrect entry in object nummer column at line " + lineNr);
                    volgnummerError = true;
                }

                ObjectNumber combinedNumber = new ObjectNumber(objNrParsed, volgNrParsed, lineNr);
                numberList.add(combinedNumber);

            }

            for (ObjectNumber combinedNumber : numberList) {

                if (combinedNumber.getObjectNumber() != expectedObjNr) {
                    expectedObjNr++;
                    if (combinedNumber.getObjectNumber() == expectedObjNr) {
                        expectedVolgNr = 1;
                    } else {

                        if (!sortedVolgnummerCorrect(numberList)) {

                            writeErrorLog("Error: objectnummer incorrect at line " + lineNr + ". Expected: " + expectedObjNr);
                            volgnummerError = true;

                        } else {

                            writeErrorLog("Warning: objectnummber incorrect at line " + lineNr + ". Expected: " + expectedObjNr);
                            writeErrorLog("After sorting no errors were found.");
                            volgnummerError = true;

                        }
                    }
                }

                if (combinedNumber.getVolgNumber() != expectedVolgNr) {

                    if (!sortedVolgnummerCorrect(numberList)) {

                        writeErrorLog("Error: volgnummer incorrect at line " + lineNr + ". Expected: " + expectedVolgNr);
                        volgnummerError = true;

                    } else {

                        writeErrorLog("Warning: volgnummer incorrect at line " + lineNr + ". Expected: " + expectedVolgNr);
                        writeErrorLog("After sorting no errors were found. This means two (or more) lines in the table are interchanged.");
                        volgnummerError = true;

                    }
                }
                expectedVolgNr++;
                lineNr++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!volgnummerError) {
            writeLog("Volgnummer test passed.");
        }
    }


    private boolean sortedVolgnummerCorrect(ArrayList<ObjectNumber> numberList) {
        int expectedObjNr = 1;
        int expectedVolgNr = 1;

        Collections.sort(numberList, new
                Comparator<ObjectNumber>() {
                    public int compare(ObjectNumber lhs, ObjectNumber rhs) {

                        if (lhs.getObjectNumber() > rhs.getObjectNumber()) return 1;
                        else if (lhs.getObjectNumber() < rhs.getObjectNumber()) return -1;

                        if (lhs.getVolgNumber() > rhs.getVolgNumber()) return 1;
                        else if (lhs.getVolgNumber() < rhs.getVolgNumber()) return -1;

                        return 0;

                    }

                });

        for (ObjectNumber combinedNumber : numberList) {
            if (combinedNumber.getObjectNumber() != expectedObjNr) {
                expectedObjNr++;
                if (combinedNumber.getObjectNumber() == expectedObjNr) {
                    expectedVolgNr = 1;
                } else {
                    return false;
                }
            }

            if (combinedNumber.getVolgNumber() != expectedVolgNr) {

                return false;

            }
            expectedVolgNr++;
        }

        return true;
    }


    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1)
            ext = s.substring(i + 1).toLowerCase();

        if (ext == null)
            return "";
        return ext;
    }

    public void testHeaderAndFilesize(File inputFile, int columnNr) {
        byte[] magicNumber = {};
        String extension = getExtension(inputFile);

        if (extension.equals("tif") || extension.equals("tiff")) {
            magicNumber = MAGIC_NUMBER_TIFF_LITTLE_ENDIAN;
        } else if (extension.equals("jpg") || extension.equals("jpeg")) {
            magicNumber = MAGIC_NUMBER_JPEG;
        } else {
            writeLog("Error: cannot check header of file " + inputFile + ". File does not have a recognizable extension: " + extension);
            headerOrFilesizeError = true;
        }


        try {

            byte[] b = new byte[4];

            FileInputStream fis = new FileInputStream(inputFile);
            if (inputFile.length() < MINIMAL_FILE_SIZE) {

                writeErrorLog("Error: file " + inputFile + " has size smaller than limit of " + MINIMAL_FILE_SIZE + " bytes");
                headerOrFilesizeError = true;
            }

            if (fis.read(b) < 4) {
                writeErrorLog("Error reading first 4 bytes of file " + inputFile);
                headerOrFilesizeError = true;
            }

            if (!Arrays.equals(b, magicNumber)) {

                // if TIF file and magic number incorrect, check Big Endian magic number too:
                if (extension.equals("tif") || extension.equals("tiff") &&
                        !Arrays.equals(b, MAGIC_NUMBER_TIFF_BIG_ENDIAN)) {
                    writeErrorLog("Error: The file " + inputFile + " has extension " + extension + " but does not have the correct header.");
                    headerOrFilesizeError = true;
                }
            }

            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void writeErrorLog(String logString) {

        System.err.println(logString);

    }

    private void writeLog(String logString) {

        System.out.println(logString);

    }

    void exit() {
//        try {
//            reportOutput.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        System.getProperties();
        exitCalled = true;

        if (!isUnitTesting) {
            System.exit(0);
        }
    }


    public int countLines(File concordanceFile) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(concordanceFile));
        String line = "";
        int nrOfLines = 0;

        while ((line = input.readLine()) != null) {
            nrOfLines++;
        }

        return nrOfLines;

    }


    public void testFileExistenceAndTestHeaders(int columnNumber) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(concordanceFile));
        String line;
        String subDir = "";
        String errorString = "";
        boolean fileExists;

        // line 1 contains column names so start with line 2:
        int lineNr = 2;
        double progress = 0;
        int length = 0;

        ArrayList<String> concordanceFileList = new ArrayList<String>();
        ArrayList<String> objectList = new ArrayList<String>();

        // skip the first line containing the column names:
        input.readLine();

        writeLog("Counting number of lines in concordance file...");
        int countedLines = countLines(concordanceFile);
        writeLog("Number of lines in concordance file: " + countedLines);

        // check if file in concordance table exists in directory:
        writeLog("Checking if file in concordance table exists in directory..");
        while ((line = input.readLine()) != null) {

            if (lineNr % 50 == 0) {
                progress = (Double.valueOf(lineNr) / countedLines) * 100.0;
                progress = Math.round(progress / 10) * 10;
                while (length-- > 0) {
                    System.out.print('\b');
                }
                System.out.print(progress + "%");
                length = String.valueOf(progress).length() + 1;

            }

            String[] columns = line.split(CSV_SEPARATOR);
            String fileWithSubdir = columns[columnNumber];


            String[] fileWithSubdirArray = fileWithSubdir.split("/");


            if (lineNr == 2) {
                subDir = "";
                for (int i = 1; i < (fileWithSubdirArray.length - 2); i++) {
                    subDir += fileWithSubdirArray[i] + File.separator;
                }
            }

            concordanceFileList.add(baseDir + File.separator + fileWithSubdir);
            String objectNr = columns[objectColumnNr];
            objectList.add(objectNr);

            File file = new File(baseDir + File.separator + fileWithSubdir);
            if (!file.exists()) {

                errorString += ERROR_FILE_EXISTENCE + ": " + file + "\n";
                errorString += "Concordance file " + file + ", line " + lineNr + " column " + columnNumber + "\n";
                fileOrHeaderError = true;

            } else {

                // test header of image files
                testHeaderAndFilesize(file, columnNumber);

            }
            lineNr++;

        }

        writeLog("");


        // remove duplicates from object list:
        HashSet h = new HashSet(objectList);
        objectList.clear();
        objectList.addAll(h);

        // save the directory for further tests:
        File subDirFile = new File(baseDir + File.separator + subDir);
        subDirList.add(subDirFile);

        // check if the amount of subdirectories (objectnumbers) is the same as in concordance table:
        String[] subdirsCheck = subDirFile.list(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return dir.isDirectory();
            }
        });

        if (subdirsCheck == null) {
            writeErrorLog("\nError during header and filesize test: Trying to list subdirectories of " + subDirFile);
            exit();

        }

        if (subdirsCheck.length != objectList.size()) {

            errorString += "Amount of directories found in " + subDirFile + "(" + subdirsCheck.length + ") is not the same as the amount of objects found in concordance file (" + objectList.size() + ")\n";
            errorString += "baseDir: " + baseDir + ", subDir: " + subDir + "\n";
            fileOrHeaderError = true;

        }


        int nrOfObjectsChecked = 0;
        length = 0;
        // check if all files in the data folders exist in the concordance file:
        writeLog("Checking if all files in the data folders exist in the concordance file..");

        for (String objectNr : objectList) {

            progress = (Double.valueOf(nrOfObjectsChecked) / objectList.size()) * 100.0;
            progress = Math.round(progress / 10) * 10;
            if (progress % 10 == 0) {
                while (length-- > 0) {
                    System.out.print('\b');
                }
                System.out.print(progress + "%");
                length = String.valueOf(progress).length() + 1;
            }

            File files = new File(baseDir + File.separator + subDir + File.separator + objectNr);

            String[] filesInDir = files.list();
            for (String fileFromDir : filesInDir) {
                fileExists = false;
                lineNr = 2; // line 1 contains column names
                for (String fileFromConcordance : concordanceFileList) {

                    String fileFromDirPath = subDir + "/" + fileFromDir;
                    File tmp = new File(fileFromDirPath);
                    fileFromDirPath = tmp.getName();

                    File tmp2 = new File(fileFromConcordance);
                    fileFromConcordance = tmp2.getName();

                    // check for duplicates:
                    if (fileExists && fileFromDirPath.equals(fileFromConcordance)) {
                        errorString += ERROR_CONCORDANCE_FILE_DUPLICATE + "\n";
                        errorString += "Line number: " + lineNr + ", entry: " + fileFromConcordance + "\n";
                        fileOrHeaderError = true;
                    }

                    if (fileFromDirPath.equals(fileFromConcordance)) {
                        fileExists = true;
                    }

                    lineNr++;

                }

                if (!fileExists) {
                    errorString += ERROR_CONCORDANCE_FILE_MISSING + "\n";
                    errorString += "File: " + files + File.separator + fileFromDir + "\n";
                    fileOrHeaderError = true;
                }

            }

            nrOfObjectsChecked++;

        }

        if (!fileOrHeaderError) {
            writeLog("Concordance table " + subDir + " <-> Directory " + subDir + " test passed. All " + subDir + " files in concordance" +
                    "table are present in " + subDir + " directory, and all " + subDir + " files in " + subDir + " directory are present in concordance table.");

            writeLog("Header test passed. All files of type " + subDir + " denoted in the concordance table have the right headers.");
            writeLog("File size test passed. All files of type " + subDir + " denoted in the concordance table have a size bigger than " + MINIMAL_FILE_SIZE + " bytes.");
        } else {
            writeErrorLog(errorString);

        }
    }


}
