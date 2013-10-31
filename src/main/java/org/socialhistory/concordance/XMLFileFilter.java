package org.socialhistory.concordance;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class XMLFileFilter extends FileFilter {
    @Override
    public boolean accept(File file) {
        return file.getName().toLowerCase().endsWith(".xml") ;
    }

    @Override
    public String getDescription() {
        return "Ead files";
    }
}
