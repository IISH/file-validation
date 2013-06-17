package org.objectrepository.validation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConcordanceMain {

    private static final String USAGE = "Usage: java org.objectrepository.validation.ConcordanceMain -fileSet [/a/b/c/[naming authority]/root directory containing the dataset" ;

    private static final String[] EXPECT = {
            "-fileSet"
    };


    public ConcordanceMain(File file) {

        new ConcordanceValidator(file).start();
    }


    /**
     * main
     * <p/>
     * Start the validation using the fileSet:
     * /a/b/c/archivalID
     *
     * @param args
     */
    public static void main(String[] args) {

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

        final File fileSet = new File(map.get("-fileSet"));
        new ConcordanceMain(fileSet);
    }
}
