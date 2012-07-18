package org.objectrepository.validation;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.URL;

import static org.junit.Assert.assertTrue;


public class ConcordanceValidatorTest {

    static String dataDirLoc;
    static String prefix;
    static String pidPrefix;

    private static String testdataLocation = "";

    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        //

    }


    @Test
    public void resources() {
        URL url = getClass().getResource("/");
        testdataLocation = url.getFile() + "data" + File.separator;
        System.out.println(testdataLocation);

    }

    @Test
    public void createPidColumnTest() {
        dataDirLoc = testdataLocation + "dorarussel";
        prefix = "MMIISG01";
        pidPrefix = "10662";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);

        concordanceValidator.createPidColumn();

        File file = new File(dataDirLoc + File.separator + prefix + File.separator + "concordanceValidWithPID.csv");
        BufferedReader input = null;
        String inputLine = "";
        try {
            input = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inputLine = input.readLine();

            String[] inputSplit = inputLine.split(",");

            assertTrue(inputSplit[inputSplit.length - 1].equals("PID"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Dirs 1, 2, 3
    @Test
    public void parseColumnsTest() {

        // CSV separator test:
        dataDirLoc = testdataLocation + "dorarussel_test1" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();

        assertTrue(ConcordanceValidator.exitCalled);


        // Missing column test:
        dataDirLoc = testdataLocation + "dorarussel_test2" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();

        assertTrue(ConcordanceValidator.exitCalled);


        // Missing concordance table:
        dataDirLoc = testdataLocation + "dorarussel_test3" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();


        assertTrue(ConcordanceValidator.exitCalled);

    }


    // Dir 4
    @Test
    public void testSubdirectoriesTest() {

        // wrong prefix test:
        dataDirLoc = testdataLocation + "dorarussel_test4" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        try {
            concordanceValidator.parseColumns();
            concordanceValidator.testFileExistenceAndTestHeaders(11);
        } catch (IOException e) {
            e.printStackTrace();
        }
        concordanceValidator.testSubdirectories();

        assertTrue(ConcordanceValidator.exitCalled);
    }


    // Dir 5
    @Test
    public void testRelationshipsTest() {

        // difference in filename test;
        dataDirLoc = testdataLocation + "dorarussel_test5" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();

        concordanceValidator.testRelationShips();

        assertTrue(ConcordanceValidator.warning);

    }

    @Test
    public void testVolgnummersTest1() {
        resources();

        System.out.println("volgnummer not a number:");
        dataDirLoc = testdataLocation + "dorarussel_test6" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();
        concordanceValidator.testVolgnummers();

        assertTrue(ConcordanceValidator.warning);

    }

    @Test
    public void testVolgnummersTest2() {
        resources();

        System.out.println("objectnummer not a number:");// objectnummer not a number:
        dataDirLoc = testdataLocation + "dorarussel_test7" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();
        concordanceValidator.testVolgnummers();

        assertTrue(ConcordanceValidator.warning);
    }

    @Test
    public void testVolgnummersTest3() {
        resources();
        System.out.println("volgnummer incorrect:");// volgnummer incorrect
        dataDirLoc = testdataLocation + "dorarussel_test8" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();
        concordanceValidator.testVolgnummers();

        assertTrue(ConcordanceValidator.warning);

    }

    @Test
    public void testVolgnummersTest4() {
        resources();
        System.out.println("object nummer incorrect:");// object nummer incorrect
        dataDirLoc = testdataLocation + "dorarussel_test9" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();
        concordanceValidator.testVolgnummers();

        assertTrue(ConcordanceValidator.warning);
    }


    @Test
    public void testVolgnummersTest5() {
        resources();


        System.out.println("object nummer and volgnummer incorrect, rows interchanged:");// object nummer and volgnummer incorrect, rows interchanged:
        dataDirLoc = testdataLocation + "dorarussel_test18" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();
        concordanceValidator.testVolgnummers();

        assertTrue(ConcordanceValidator.warning);

    }


    @Test
    public void testHeaderTest() {

        // unknown or incorrect extension of image file:
        dataDirLoc = testdataLocation + "dorarussel_test10" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        File file = new File(dataDirLoc + "MMIISG01\\JPEG-7" + File.separator + "000001" + File.separator + "MMIISG01_AF_000001.dat");
        concordanceValidator.testHeaderAndFilesize(file, 1);


        assertTrue(ConcordanceValidator.warning);


        // filesize incorrect:
        dataDirLoc = testdataLocation + "dorarussel_test11" + File.separator;
        prefix = "MMIISG01";

        file = new File(dataDirLoc + "MMIISG01\\JPEG-10" + File.separator + "000001" + File.separator + "MMIISG01_AF2_000001.jpg");

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.testHeaderAndFilesize(file, 1);

        assertTrue(ConcordanceValidator.warning);

        // incorrect image header:
        dataDirLoc = testdataLocation + "dorarussel_test12" + File.separator;
        prefix = "MMIISG01";

        file = new File(dataDirLoc + "MMIISG01\\JPEG-10" + File.separator + "000001" + File.separator + "MMIISG01_AF2_000001.tif");

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.testHeaderAndFilesize(file, 1);

        assertTrue(ConcordanceValidator.warning);

    }

    @Test
    public void testFileExistenceAndTestHeadersTest() {

        // incorrect prefix:
        dataDirLoc = testdataLocation + "dorarussel_test13" + File.separator;
        prefix = "MMIISG01";

        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.start();

        assertTrue(ConcordanceValidator.warning);


        // File in concordance table does not exist on disk:
        dataDirLoc = testdataLocation + "dorarussel_test14" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();

        try {
            concordanceValidator.testFileExistenceAndTestHeaders(11);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(ConcordanceValidator.warning);


        // different amount of directories than amount of objects in concordance file:
        dataDirLoc = testdataLocation + "dorarussel_test15" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.parseColumns();

        try {
            concordanceValidator.testFileExistenceAndTestHeaders(concordanceValidator.jpeg2ColumnNr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(ConcordanceValidator.warning);

        // duplicate entry in concordance table:
        dataDirLoc = testdataLocation + "dorarussel_test16" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.start();

        assertTrue(ConcordanceValidator.warning);

        // file on disk not in concordance table:
        dataDirLoc = testdataLocation + "dorarussel_test17" + File.separator;
        prefix = "MMIISG01";

        concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix, pidPrefix);
        concordanceValidator.isUnitTesting = true;

        concordanceValidator.start();


        assertTrue(ConcordanceValidator.warning);


    }

}
