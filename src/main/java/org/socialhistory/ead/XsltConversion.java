package org.socialhistory.ead;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * XsltConversion
 * <p/>
 * Render the xml with the default xsl stylesheet
 */
public class XsltConversion {

    public static List<String> getXsltDocuments() {

        final List<String> list = new ArrayList();
        Enumeration<URL> resources;
        try {
            resources = XsltConversion.class.getClassLoader().getResources("");
        } catch (IOException e) {
            e.printStackTrace();
            return list;
        }
        while (resources.hasMoreElements()) {
            final URL url = resources.nextElement(); // The main class folder
            final File folder = new File(url.getFile());
            final File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xsl");
                }
            });
            for (File file : files) {
                list.add(file.getName());
            }
        }
        return list ;
    }

    public static String removeExtension(String name) {
        int i = name.lastIndexOf(".");
        return (i == -1) ? name : name.substring(0, i);
    }

    public static void write(File sourceFile, File targetFile, String stylesheet) throws Exception {
        final TransformerFactory tf = TransformerFactory.newInstance();
        final URL resource = XsltConversion.class.getResource("/" + stylesheet);
        Source source = new StreamSource(resource.openStream());
        source.setSystemId(resource.toString());

        final Transformer transformer = tf.newTransformer(source);
        transformer.setParameter("archivalID", removeExtension(sourceFile.getName()));

        if (targetFile.exists())
            targetFile.delete();

        transformer.transform(new StreamSource(sourceFile), new StreamResult(targetFile));
    }

}
