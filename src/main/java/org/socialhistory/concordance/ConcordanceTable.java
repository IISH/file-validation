package org.socialhistory.concordance;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ConcordanceTable {

    public static void write(File sourceFile, File targetFile) throws Exception {

        final TransformerFactory tf = TransformerFactory.newInstance();
        final URL resource = ConcordanceTable.class.getResource("/ead2concordance.xsl");
        Source source = new StreamSource(resource.openStream());
        source.setSystemId(resource.toString());

        final Transformer transformer = tf.newTransformer(source);
        transformer.setParameter("archivalID", sourceFile.getName());

        if (targetFile.exists())
            targetFile.delete();

        transformer.transform(new StreamSource(sourceFile), new StreamResult(targetFile));
    }

}
