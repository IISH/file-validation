import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: cro
 * Date: 5-4-12
 * Time: 14:23
 * To change this template use File | Settings | File Templates.
 */
public class ConcordanceValidator {


    private static final String OBJECT_COLUMN_NAME = "Objnr";
    private static final String VOLGNR_COLUMN_NAME = "volgnr";
    private static final String TIF_COLUMN_NAME = "TIFF";
    private static final String PID_COLUMN_NAME = "Uniek ID";
    private static final String JPEG_COLUMN_NAME = "JPEG-7";
    private static final String JPEG2_COLUMN_NAME = "JPEG-10";
    private static final String OCR_COLUMN_NAME = "OCR";

    private static final String CSV_SEPARATOR = ";";

    private static final String ERROR_FILE_EXISTENCE = "Error: file entry in concordance table does not exist in directory";
    private static final String ERROR_CONCORDANCE_FILE_MISSING = "Error: file in directory is not listed in the concordance table";
    private static final String ERROR_CONCORDANCE_FILE_DUPLICATE = "Duplicate entry in concordance table.";

    private static final int MINIMAL_FILE_SIZE = 200;

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

    boolean ocrPresent;
    boolean jpegPresent;
    boolean jpeg2Present;


    private String dataDirLoc;

    public ConcordanceValidator() {

    }

    private void parseColumns(File concordanceFile) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(concordanceFile));

        String line = input.readLine();

        String[] columnNames = line.split(CSV_SEPARATOR);

        if (columnNames.length < 3) {
            System.err.println("Error: incorrect CSV separator. Expected \";\".");
            return;
        }

        //todo; test of alle verplichte kolomnamen aanwezig zijn.
        for (int i = 0; i < columnNames.length; i++) {

            if (columnNames[i].equals(OBJECT_COLUMN_NAME)) {
                objectColumnNr = i;
            } else if (columnNames[i].equals(TIF_COLUMN_NAME)) {
                masterColumnNr = i;
            } else if (columnNames[i].equals(VOLGNR_COLUMN_NAME)) {
                volgNrColumnNr = i;
            } else if (columnNames[i].equals(PID_COLUMN_NAME)) {
                pidColumnNr = i;
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


    }


    public ConcordanceValidator(File concordanceFile, String dataDirLoc) {
        this.dataDirLoc = dataDirLoc;

        try {

            parseColumns(concordanceFile);

            testVolgnummers(concordanceFile);

            testRelationShips(concordanceFile);

            testFileExistenceAndTestHeaders(concordanceFile, masterColumnNr);

            if (jpegPresent) {
                testFileExistenceAndTestHeaders(concordanceFile, jpegColumnNr);
            }

            if (jpeg2Present) {
                testFileExistenceAndTestHeaders(concordanceFile, jpeg2ColumnNr);
            }

            if (ocrPresent) {
                testFileExistenceAndTestHeaders(concordanceFile, ocrColumnNr);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void testRelationShips(File concordanceFile) {

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

                tifImage = tifImage.split("/")[1];
                jpegImage = jpegImage.split("/")[1];

                if (!tifImage.equals(jpegImage)) {
                    System.err.println("Warning: Difference in filenames between " + tifImage + " and " + jpegImage + " at line " + lineNr);
                }

                if (jpeg2Present) {
                    String jpeg2Image = columns[jpeg2ColumnNr].split("\\.")[0];
                    jpeg2Image = jpeg2Image.split("/")[1];
                    if (!jpeg2Image.equals(jpegImage)) {
                        System.err.println("Warning: Difference in filenames between " + jpeg2Image + " and " + jpegImage + " at line " + lineNr);
                    }
                }


                if (ocrPresent) {
                    String ocr = columns[ocrColumnNr].split("\\.")[0];
                    ocr = ocr.split("/")[1];
                    if (!ocr.equals(jpegImage)) {
                        System.err.println("Warning: Difference in filenames between " + ocr + " and " + jpegImage + " at line " + lineNr);
                    }
                }



                lineNr++;

            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // checks if the volgnummers and objectnummers are in correct order.
    // i.e.: (objectnummer:volgnummer) 1:1, 1:2, 1:3, 2:1, 2:2, 2:3, 3:1, ...,
    private void testVolgnummers(File concordanceFile) {
        BufferedReader input = null;
        String line;
        int lineNr = 2;
        int expectedVolgNr = 1;
        int expectedObjNr = 1;

        try {
            input = new BufferedReader(new FileReader(concordanceFile));

            // skip the first line containing column names:
            input.readLine();

            while ((line = input.readLine()) != null) {

                String[] columns = line.split(CSV_SEPARATOR);
                String volgNr = columns[volgNrColumnNr];
                String objNr = columns[objectColumnNr];
                int volgNrParsed;
                int objNrParsed;

                // try parsing the volgnummer, throw error if not a number:
                try {
                    volgNrParsed = Integer.parseInt(volgNr);
                } catch (NumberFormatException e) {
                    System.err.println("Error: incorrect entry in volgnummer column at line " + lineNr);
                    return;
                }
                // try parsing the objectnummer, throw error if not a number:
                try {
                    objNrParsed = Integer.parseInt(objNr);
                } catch (NumberFormatException e) {
                    System.err.println("Error: incorrect entry in object nummer column at line " + lineNr);
                    return;
                }

                if (objNrParsed != expectedObjNr) {
                    expectedObjNr++;
                    if (objNrParsed == expectedObjNr) {
                        expectedVolgNr = 1;
                    } else {
                        System.err.println("Error: objectnummer incorrect at line " + lineNr + ". Expected: " + expectedObjNr);
                        return;
                    }
                }

                if (volgNrParsed != expectedVolgNr) {
                    System.err.println("Error: volgnummer incorrect at line " + lineNr + ". Expected: " + expectedVolgNr);
                    return;
                }
                expectedVolgNr++;
                lineNr++;

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Volgnummer test passed.");

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

    private void testHeader(File inputFile, int columnNr) {
        byte[] magicNumber;
        String extension = getExtension(inputFile);

        if (extension.equals("tif") || extension.equals("tiff")) {
            magicNumber = MAGIC_NUMBER_TIFF_LITTLE_ENDIAN;
        } else if (extension.equals("jpg") || extension.equals("jpeg")) {
            magicNumber = MAGIC_NUMBER_JPEG;
        } else {
            System.out.println("Warning: cannot check header of file in column " + columnNr + ". File does not have an extension.");
            return;
        }


        try {

            byte[] b = new byte[4];

            FileInputStream fis = new FileInputStream(inputFile);

            if (inputFile.length() < MINIMAL_FILE_SIZE) {

                System.err.println("Error: file " + inputFile + " has size smaller than limit of " + MINIMAL_FILE_SIZE + " bytes");
                return;
            }

            if (fis.read(b) < 4) {
                System.err.println("Error reading first 4 bytes of file " + inputFile);
                return;
            }

            if (!Arrays.equals(b, magicNumber)) {

                // if TIF file and magic number incorrect, check Big Endian magic number too:
                if (extension.equals("tif") || extension.equals("tiff") &&
                        !Arrays.equals(b, MAGIC_NUMBER_TIFF_BIG_ENDIAN)) {
                    System.err.println("Error: The file " + inputFile + " has extension " + extension + " but does not have the correct header.");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void testFileExistenceAndTestHeaders(File concordanceFile, int columnNumber) throws IOException {

        BufferedReader input = new BufferedReader(new FileReader(concordanceFile));
        String line;
        String fileDir = "";
        boolean fileExists;

        // line 1 contains column names so start with line 2:
        int lineNr = 2;

        ArrayList<String> concordanceFileList = new ArrayList<String>();
        ArrayList<String> objectList = new ArrayList<String>();

        // skip the first line containing the column names:
        input.readLine();

        // check if file in concordance table exists in directory:
        while ((line = input.readLine()) != null) {

            String[] columns = line.split(CSV_SEPARATOR);
            String fileFromConcordance = columns[columnNumber];
            String fileDirTemp = columns[columnNumber].split("/")[0];

            if (!fileDir.equals("") && !fileDir.equals(fileDirTemp)) {
                System.err.println("Error: incorrect directory at line " + lineNr + " column " + columnNumber);
                return;
            } else {
                fileDir = fileDirTemp;
            }

            concordanceFileList.add(fileFromConcordance);
            String subDirectory = columns[objectColumnNr];
            objectList.add(subDirectory + File.separator + fileDir);

            File file = new File(dataDirLoc + File.separator + subDirectory + File.separator + fileFromConcordance);

            if (!file.exists()) {
                System.err.println(ERROR_FILE_EXISTENCE);
                System.err.println("Concordance file line " + lineNr + " column " + columnNumber);
                return;
            }


            // test header of image files
            testHeader(file, columnNumber);

            lineNr++;

        }


        for (String subDirectory : objectList) {
            File files = new File(dataDirLoc + File.separator + subDirectory);

            String[] filesInDir = files.list();

            for (String fileFromDir : filesInDir) {

                fileExists = false;
                lineNr = 2; // line 1 contains column names
                for (String fileFromConcordance : concordanceFileList) {

                    // check for duplicates:
                    if (fileExists && fileFromDir.equals(fileFromConcordance)) {
                        System.err.println(ERROR_CONCORDANCE_FILE_DUPLICATE);
                        System.err.println("Line number: " + lineNr + ", entry: " + fileFromConcordance);
                        return;
                    }

                    String fileFromDirPath = fileDir + "/" + fileFromDir;
                    if (fileFromDirPath.equals(fileFromConcordance)) fileExists = true;

                }

                if (!fileExists) {
                    System.err.println(ERROR_CONCORDANCE_FILE_MISSING);
                    System.err.println("File: " + files + File.separator + fileFromDir);
                    return;
                }

            }

        }
        System.out.println("Concordance table " + fileDir + " <-> Directory " + fileDir + " test passed. All " + fileDir + " files in concordance" +
                "table are present in " + fileDir + " directory, and all " + fileDir + " files in " + fileDir + " directory are present in concordance table.");

        System.out.println("Header test passed. All files of type " + fileDir + " denoted in the concordance table have the right headers.");
        System.out.println("File size test passed. All files of type " + fileDir + " denoted in the concordance table have a size bigger than " + MINIMAL_FILE_SIZE + " bytes.");


    }


}
