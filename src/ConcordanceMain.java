import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cro
 * Date: 5-4-12
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */


public class ConcordanceMain {

    private static final String TIFF_EXTENSION = "tif";


    private static final String[] EXPECT = {
            "-concordanceFile",
            "-imageDir",
    };


    public ConcordanceMain(String concordanceFileLoc, String imageDirLoc) {

        // create array of strings of all tiff images in the selected directory:
        String[] tiffImageArray = makeArrayFromImageDir(imageDirLoc);

        File concordanceFile = new File(concordanceFileLoc);
        ConcordanceValidator concordanceValidator = new ConcordanceValidator(concordanceFile, tiffImageArray);

    }

    private String[] makeArrayFromImageDir(String imageDirLoc) {

        File dir = new File(imageDirLoc);

        return dir.list(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(TIFF_EXTENSION);
            }
        });

    }


    public static void main(String[] args) {

        if (args.length < 4) {

            System.out.println("Usage: java -concordanceFile [concordanceFile] -imageDir [directory containing images]");
            System.exit(-1);
        }

        Map<String, String> map = new HashMap(EXPECT.length);
        for (int i = 0; i < args.length; i += 2) {
            map.put(args[i], args[i + 1]);
        }


        for (String key : EXPECT) {
            if (!map.containsKey(key)) {
                System.out.println("Expected case sensitive parameter: " + key + "\n");

                System.out.println("Usage: java -concordanceFile [concordanceFile] -imageDir [directory containing images]");
                System.exit(1);
            }
        }

        ConcordanceMain concordanceMain = new ConcordanceMain(map.get("-concordanceFile"), map.get("-imageDir"));

    }
}
