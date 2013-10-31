package org.socialhistory.concordance;

import org.socialhistory.concordance.ConcordanceTable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CreateConcordance extends JFrame {
    private JButton button1;
    private JButton createConcordanceTableButton;
    private JTextField filename;
    private JPanel panel1;
    private JLabel label1;
    private File selectedFile;

    public CreateConcordance() {

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final JFileChooser c = new JFileChooser();
                c.addChoosableFileFilter(new FileNameExtensionFilter("EAD files", "xml"));
                c.setAcceptAllFileFilterUsed(false);
                int rVal = c.showOpenDialog(CreateConcordance.this);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    selectedFile = c.getSelectedFile();
                    filename.setText(c.getSelectedFile().getName());
                }
                if (rVal == JFileChooser.CANCEL_OPTION)
                    selectedFile = null;
            }
        });

        createConcordanceTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (selectedFile != null && selectedFile.exists()) {
                    File targetFile = new File(selectedFile.getParent(),
                            removeExtension(selectedFile.getName()) + ".csv");
                    try {
                        ConcordanceTable.write(selectedFile, targetFile);
                    } catch (Exception e) {
                        label1.setText(e.getMessage());
                    } finally {
                        if (targetFile.exists())
                            label1.setText("See " + targetFile.getAbsolutePath());
                        else
                            label1.setText("Failed to create " + targetFile.getAbsolutePath());
                    }
                }
            }
        });
    }

    public Container getMainPanel() {
        return panel1;
    }

    private static String removeExtension(String name) {
        int i = name.lastIndexOf(".");
        return (i == -1) ? name : name.substring(0, i);
    }

}
