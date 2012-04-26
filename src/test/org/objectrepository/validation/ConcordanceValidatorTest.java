package org.objectrepository.validation;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;


public class ConcordanceValidatorTest {

    static String dataDirLoc;
    static String prefix;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException {


    }


    @Test
    public void createPidColumnTest() {

        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.createPidColumn();

        File file = new File(dataDirLoc + File.separator + "concordanceFileTemp.csv");
        BufferedReader input = null;
        String inputLine = "";
        try {
            input = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inputLine = input.readLine();

            String[] inputSplit = inputLine.split(";");

            assertTrue(inputSplit[inputSplit.length - 1].equals("PID"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Dirs 1, 2, 3
    @Test
    public void parseColumnsTest() {

        // CSV separator test:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test1" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.parseColumns();

        assertTrue(ConcordanceValidator.exitCalled);


        // Missing column test:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test2" + File.separator;
        prefix = "MMIISG01";

         concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.parseColumns();

        assertTrue(ConcordanceValidator.exitCalled);


        // Missing concordance table:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test3" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.parseColumns();

        assertTrue(ConcordanceValidator.exitCalled);

    }



    // Dir 4
    @Test
    public void testSubdirectoriesTest(){

        // wrong prefix test:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test4" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.testSubdirectories();

        assertTrue(ConcordanceValidator.exitCalled);
    }


    // Dir 5
    @Test
    public void testRelationshipsTest(){

        // difference in filename test;
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test5" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.parseColumns();

        concordanceValidator.testRelationShips();

        assertTrue(ConcordanceValidator.warning);

    }


    // Dir 6
    @Test
    public void testVolgnummersTest(){

        // volgnummer not a number:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test6" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.parseColumns();
        concordanceValidator.testVolgnummers();

        assertTrue(ConcordanceValidator.exitCalled);

        // objectnummer not a number:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test7" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.parseColumns();
        concordanceValidator.testVolgnummers();

        assertTrue(ConcordanceValidator.exitCalled);

        // volgnummer incorrect
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test8" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.parseColumns();
        concordanceValidator.testVolgnummers();

        assertTrue(ConcordanceValidator.exitCalled);


        // object nummer incorrect
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test9" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.parseColumns();
        concordanceValidator.testVolgnummers();

        assertTrue(ConcordanceValidator.exitCalled);


    }


    @Test
    public void testHeaderTest(){

        // unknown or incorrect extension of image file:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test10" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        File file = new File("C:\\data\\data\\dorarussel_test10\\JPEG-7\\000001\\MMIISG01_AF_000001.dat");
        concordanceValidator.testHeaderAndFilesize(file,1);


        assertTrue(ConcordanceValidator.exitCalled);


        // filesize incorrect:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test11" + File.separator;
        prefix = "MMIISG01";

        file = new File("C:\\data\\data\\dorarussel_test11\\JPEG-10\\000001\\MMIISG01_AF2_000001.jpg");

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.testHeaderAndFilesize(file, 1);

        assertTrue(ConcordanceValidator.exitCalled);

        // incorrect image header:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test12" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.start();

        assertTrue(ConcordanceValidator.exitCalled);

    }

    @Test
    public void testFileExistenceAndTestHeadersTest(){

        // incorrect subdirectory:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test13" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.start();

        assertTrue(ConcordanceValidator.exitCalled);


        // File in concordance table does not exist on disk:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test14" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.parseColumns();

        try {
            concordanceValidator.testFileExistenceAndTestHeaders(concordanceValidator.jpeg2ColumnNr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(ConcordanceValidator.exitCalled);


        // different amount of directories than amount of objects in concordance file:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test15" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.parseColumns();

        try {
            concordanceValidator.testFileExistenceAndTestHeaders(concordanceValidator.jpegColumnNr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(ConcordanceValidator.exitCalled);

        // duplicate entry in concordance table:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test16" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.start();

        assertTrue(ConcordanceValidator.exitCalled);

        // file on disk not in concordance table:
        dataDirLoc = "C:" + File.separator + "data" + File.separator + "data" + File.separator + "dorarussel_test17" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);

        concordanceValidator.start();

        assertTrue(ConcordanceValidator.exitCalled);



    }

}
