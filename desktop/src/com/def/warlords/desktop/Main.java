package com.def.warlords.desktop;

import javax.swing.JFrame;
import java.awt.EventQueue;

/**
 * @author wistful23
 * @version 1.23
 */
public class Main extends JFrame {

    public static void main(String[] args) {
        new Main().start();
    }

    public void start() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Warlords 2.10");
        final MainComponent mainComponent = new MainComponent();
        setContentPane(mainComponent);
        getContentPane().setPreferredSize(mainComponent.getSize());
        pack();
        EventQueue.invokeLater(mainComponent::start);
        setVisible(true);
    }
}
