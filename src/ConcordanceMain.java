import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

public class ConcordanceMain {

    private static final String USAGE = "Usage: java -concordanceFile [concordanceFile] -dataDir [root directory containing tif, jpeg etc. directories]";

    private static final String[] EXPECT = {
            "-concordanceFile",
            "-dataDir",
    };


    public ConcordanceMain(String concordanceFileLoc, String dataDirLoc) {

        // create array of strings of all tiff images in the selected directory:

        File concordanceFile = new File(concordanceFileLoc);
        ConcordanceValidator concordanceValidator = new ConcordanceValidator(concordanceFile, dataDirLoc);

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

        ConcordanceMain concordanceMain = new ConcordanceMain(map.get("-concordanceFile"), map.get("-dataDir"));

    }
}
