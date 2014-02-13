package org.socialhistory.ead;

import org.socialhistory.concordance.CreateForm;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.net.URL;

/**
 * XsltConversion
 * <p/>
 * Render the xml with the default xsl stylesheet
 */
public class XsltConversion {

    public static void write(File sourceFile, File targetFile, String stylesheet) throws Exception {
        final TransformerFactory tf = TransformerFactory.newInstance();
        final URL resource = XsltConversion.class.getResource("/" + stylesheet);
        Source source = new StreamSource(resource.openStream());
        source.setSystemId(resource.toString());

        final Transformer transformer = tf.newTransformer(source);
        transformer.setParameter("archivalID", CreateForm.removeExtension(sourceFile.getName()));

        if (targetFile.exists())
            targetFile.delete();

        transformer.transform(new StreamSource(sourceFile), new StreamResult(targetFile));
    }

}
