import javax.sound.midi.SysexMessage;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

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
    private static final String JPEG_COLUMN_NAME = "JPEG-10";

    private static final String TIFF_EXTENSION = "tif";
    private static final String JPEG_EXTENSION = "jpg";

    private static final String CSV_SEPARATOR = ";";

    private static final String ERROR_FILE_EXISTENCE = "Error: file entry in concordance table does not exist in directory";
    private static final String ERROR_CONCORDANCE_FILE_MISSING = "Error: file in directory is not listed in the concordance table";
    private static final String ERROR_CONCORDANCE_FILE_DUPLICATE = "Duplicate entry in concordance table.";

    private static final String MAGIC_NUMBER_TIFF_LITTLE_ENDIAN = "49492a00";
    private static final String MAGIC_NUMBER_TIFF_BIG_ENDIAN = "4d4d002a";

    int objectColumnNr;
    int tifColumnNr;
    int volgNrColumnNr;
    int pidColumnNr;

    int jpegColumnNr;
    int jpeg2ColumNr;
    int jpeg3ColumNr;

    private String dataDirLoc;
    private String tifDirLoc;
    private String jpegDirLoc;

    private String[] tiffImageDirectoryArray;
    private String[] jpegImageDirectorayArray;

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
                tifColumnNr = i;
                String[] tifColumnSplit = columnNames[i].split("/");
                tifDirLoc = tifColumnSplit[0];
            } else if (columnNames[i].equals(VOLGNR_COLUMN_NAME)) {
                volgNrColumnNr = i;
            } else if (columnNames[i].equals(PID_COLUMN_NAME)) {
                pidColumnNr = i;
            } else if(columnNames[i].equals(JPEG_COLUMN_NAME)) {
                jpegColumnNr = i;
            }

        }



    }


    // makes an array of the files ending with 'extension'.
    private String[] makeArrayFromImageDir(String dataDirLoc, final String extension) {

        String imageDir = "";

        if (extension.equals(TIFF_EXTENSION)) {
            imageDir = tifDirLoc;
        } else if (extension.equals(JPEG_EXTENSION)) {
            imageDir = jpegDirLoc;
        }

        File dir = new File(dataDirLoc + File.separator + imageDir);

        String[] result = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(extension);
            }
        });

        return result;
    }

    public ConcordanceValidator(File concordanceFile, String dataDirLoc) {
        this.dataDirLoc = dataDirLoc;

        try {

            parseColumns(concordanceFile);


//            jpegImageDirectorayArray = makeArrayFromImageDir(dataDirLoc, JPEG_EXTENSION);

            testVolgnummers(concordanceFile);

//            testTiffExistence(concordanceFile);
            testFileExistence(concordanceFile,tifColumnNr);
            testFileExistence(concordanceFile,jpegColumnNr);


//            testTiffHeaders();

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

                String[] columns = line.split(";");
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

    private void testTiffHeaders() {

        for (String tiffImage : tiffImageDirectoryArray) {

            String tiffPath = dataDirLoc + File.separator + tifDirLoc + File.separator + tiffImage;
            File inputFile = new File(tiffPath);

            try {

                byte[] b = new byte[4];

                FileInputStream fis = new FileInputStream(inputFile);

                if (fis.read(b) < 4) {
                    System.err.println("Error reading first 4 bytes of file " + tiffPath);
                    return;
                }

                String magicNumber = new BigInteger(b).toString(16);
                if (!magicNumber.equals(MAGIC_NUMBER_TIFF_LITTLE_ENDIAN) &&
                        !magicNumber.equals(MAGIC_NUMBER_TIFF_BIG_ENDIAN)) {
                    System.err.println("Error: The file " + tiffPath + " has extension \".tif\" but does not seem to be a correct TIF file.");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("TIF file header test passed. All TIF files seem to be of the correct format.");

    }


    private void testFileExistence(File concordanceFile, int columnNumber) throws IOException {

        BufferedReader input = new BufferedReader(new FileReader(concordanceFile));
        String line;
        String fileDir = "";
        boolean fileExists;

        // line 1 contains column names so start with line 2:
        int lineNr = 2;

        ArrayList<String> concordanceFileList = new ArrayList<String>();

        // skip the first line containing the column names:
        input.readLine();

        // check if file in concordance table exists in directory:
        while ((line = input.readLine()) != null) {

            String[] columns = line.split(CSV_SEPARATOR);
            String fileFromConcordance = columns[columnNumber];
            String fileDirTemp = columns[columnNumber].split("/")[0];

            if(!fileDir.equals("") && !fileDir.equals(fileDirTemp) ){
                System.err.println("Error: incorrect directory at line " + lineNr);
                return;
            } else {
                fileDir = fileDirTemp;
            }


            concordanceFileList.add(fileFromConcordance);

            File file = new File(dataDirLoc + File.separator + fileFromConcordance);
            System.out.println(file);
            if (!file.exists()) {
                System.err.println(ERROR_FILE_EXISTENCE);
                System.err.println("Concordance file line " + lineNr + " column " + columnNumber);
                return;
            }
            lineNr++;

        }

        File dir = new File(dataDirLoc + File.separator + fileDir);

        String[] filesInDir = dir.list();

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
                System.err.println("File: " + fileFromDir);
                return;
            }

        }

        System.out.println("Concordance table "+ fileDir + " <-> Directory "+ fileDir + " test passed. All "+ fileDir + " files in concordance" +
                "table are present in "+ fileDir + " directory, and all "+ fileDir + " files in "+ fileDir + " directory are present in concordance table.");


    }

    private void testTiffExistence(File concordanceFile) throws IOException {

        BufferedReader input = new BufferedReader(new FileReader(concordanceFile));
        String line;

        // line 1 contains column names so start with line 2
        int lineNr = 2;
        boolean imageExists;

        ArrayList<String> tiffImageConcordanceArray = new ArrayList<String>();

        tiffImageDirectoryArray = makeArrayFromImageDir(dataDirLoc, TIFF_EXTENSION);

        // skip the first line containing the column names
        input.readLine();

        // check if image in concordance table exists in directory:
        while ((line = input.readLine()) != null) {

            String[] columns = line.split(";");
            String tifFile = columns[tifColumnNr];

            tiffImageConcordanceArray.add(tifFile);

            imageExists = false;
            for (String aTiffImage : tiffImageDirectoryArray) {
                String tiffPath = tifDirLoc + "/" + aTiffImage;
                if (tiffPath.equals(tifFile)) imageExists = true;

            }

            if (!imageExists) {
                System.err.println(ERROR_FILE_EXISTENCE);
                System.err.println("Line number: " + lineNr + ", " + tifFile);
                return;
            }

            lineNr++;
        }

        // if number of tif entries in concordance table equals the number of tif files in directory,
        // and all tif entries are correct, omit the next test:
        if (tiffImageDirectoryArray.length == lineNr) return;

        //  check if image in directory exists only once in concordance table:
        for (String aTiffImage : tiffImageDirectoryArray) {

            imageExists = false;
            lineNr = 2; // line 1 contains column names
            for (String tiffFromConcordance : tiffImageConcordanceArray) {

                // check for duplicates:
                if (imageExists && aTiffImage.equals(tiffFromConcordance)) {
                    System.err.println(ERROR_CONCORDANCE_FILE_DUPLICATE);
                    System.err.println("Line number: " + lineNr + ", entry: " + tiffFromConcordance);
                    return;
                }

                String tiffPath = tifDirLoc + "/" + aTiffImage;

                if (tiffPath.equals(tiffFromConcordance)) imageExists = true;

            }

            if (!imageExists) {
                System.err.println(ERROR_CONCORDANCE_FILE_MISSING);
                System.err.println("File: " + aTiffImage);
                return;
            }

        }

        System.out.println("Concordance table TIF <-> Directory TIF test passed. All TIF files in concordance" +
                "table are present in TIF directory, and all TIF files in TIF directory are present in concordance table.");


    }

}
