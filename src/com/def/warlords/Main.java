package com.def.warlords;

import com.def.warlords.control.MainController;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Warlords 2.10");
        final MainController mainComponent = new MainController();
        setContentPane(mainComponent);
        getContentPane().setPreferredSize(mainComponent.getSize());
        pack();
        setVisible(true);
        EventQueue.invokeLater(mainComponent::start);
    }
}
