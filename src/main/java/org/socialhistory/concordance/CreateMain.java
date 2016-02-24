package org.socialhistory.concordance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * CreateMain
 * <p/>
 * Creates a concordance table from the EAD
 */
public class CreateMain {

    public static void main(String[] args) throws IOException {

        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.setContentPane(new CreateForm().getMainPanel());
        frame.setTitle("Create transformation options");
        frame.pack();
        frame.setSize(new Dimension(800, 150));
        frame.setVisible(true);
    }

}
