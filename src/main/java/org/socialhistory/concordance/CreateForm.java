package org.socialhistory.concordance;

import org.socialhistory.ead.XsltConversion;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CreateForm extends JFrame {
    private JButton chooseDocument;
    private JTextField filename;
    private JPanel panel1;
    private JButton startTransformation;
    private JLabel label2;
    private JComboBox comboBox1;
    private JLabel label1;
    private File selectedFile;

    public CreateForm() {
        initUIComponents();
    }

    public Container getMainPanel() {
        return panel1;
    }

    private void initUIComponents() {

        final java.util.List<String> list = XsltConversion.getXsltDocuments();
        for (String item : list) {
            comboBox1.addItem(item);
        }

        chooseDocument.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final JFileChooser c = new JFileChooser();
                c.addChoosableFileFilter(new FileNameExtensionFilter("XML files", "xml"));
                c.setAcceptAllFileFilterUsed(false);
                int rVal = c.showOpenDialog(CreateForm.this);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    selectedFile = c.getSelectedFile();
                    filename.setText(c.getSelectedFile().getName());
                }
                if (rVal == JFileChooser.CANCEL_OPTION)
                    selectedFile = null;
            }
        });

        startTransformation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (selectedFile != null && selectedFile.exists()) {
                    final File targetFile = new File(selectedFile.getParent(),
                            XsltConversion.removeExtension(selectedFile.getName()) + ".result.xml");
                    try {
                        XsltConversion.write(selectedFile, targetFile, String.valueOf(comboBox1.getSelectedItem()));
                    } catch (Exception e) {
                        label2.setText(e.getMessage());
                    } finally {
                        if (targetFile.exists())
                            label2.setText("See " + targetFile.getAbsolutePath());
                        else
                            label2.setText("Failed to create " + targetFile.getAbsolutePath());
                    }
                }
            }
        });
    }
}
