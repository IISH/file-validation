package org.objectrepository.validation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConcordanceMain {

    private static final String USAGE = "Usage: java -dataDir [root directory containing tif, jpeg etc. directories] -prefix [project prefix]";

    private static final String[] EXPECT = {
            "-dataDir",
            "-prefix",
    };


    public ConcordanceMain(String dataDirLoc, String prefix) {

        // create array of strings of all tiff images in the selected directory:

        String concordanceFileLocation = dataDirLoc + File.separator + prefix + ".csv";
        File concordanceFile = new File(concordanceFileLocation);


        ConcordanceValidator concordanceValidator = new ConcordanceValidator(dataDirLoc, prefix);
        concordanceValidator.start();

    }


    public static void main(String[] args) {

        if (args.length < 4) {

            System.out.println(USAGE);
            System.exit(-1);
        }

        Map<String, String> map = new HashMap(EXPECT.length);

        for (int i = 0; i < args.length; i += 2) {
            map.put(args[i], args[i + 1]);
        }


        for (String key : EXPECT) {
            if (!map.containsKey(key)) {
                System.out.println("Expected case sensitive parameter: " + key + "\n");

                System.out.println(USAGE);
                System.exit(1);
            }
        }

        ConcordanceMain concordanceMain = new ConcordanceMain(map.get("-dataDir"), map.get("-prefix"));

    }
}
