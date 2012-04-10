import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

    private static final String ERROR_TIFF_EXISTENCE = "Error: TIFF entry in concordance file does not exist.";

    int objectColumnNr;
    int tifColumnNr;
    int volgNrColumnNr;
    int pidColumnNr;


    private String[] tiffImageArray;

    public ConcordanceValidator() {

    }

    private void setColumnNumbers(File concordanceFile) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(concordanceFile));

        String line = input.readLine();

        String[] columnNames = line.split(";");

        for (int i = 0; i < columnNames.length; i++) {

            if (columnNames[i].equals(OBJECT_COLUMN_NAME)) {
                objectColumnNr = i;
            } else if (columnNames[i].equals(TIF_COLUMN_NAME)) {
                tifColumnNr = i;
            } else if (columnNames[i].equals(VOLGNR_COLUMN_NAME)) {
                volgNrColumnNr = i;
            } else if (columnNames[i].equals(PID_COLUMN_NAME)) {
                pidColumnNr = i;
            }

        }

    }

    public ConcordanceValidator(File concordanceFile, String[] tiffImageArray) {
        this.tiffImageArray = tiffImageArray;

        try {
            setColumnNumbers(concordanceFile);


            if (!testTiffExistence(concordanceFile)) {
                System.out.println(ERROR_TIFF_EXISTENCE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    private boolean testTiffExistence(File concordanceFile) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(concordanceFile));
        String line;

        input.readLine(); // skip the first line containing the column names

        while ((line = input.readLine()) != null) {

            String[] columns = line.split(";");
            String tifFile = columns[tifColumnNr];
            System.out.println(line);

        }

        return true;
    }

}
