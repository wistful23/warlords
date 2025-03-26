package com.def.warlords.desktop;

import com.def.warlords.control.MainController;
import com.def.warlords.control.Platform;
import com.def.warlords.graphics.BitmapFactory;
import com.def.warlords.sound.SoundFactory;

import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class MainComponent extends JComponent implements Platform {

    private final MainController controller;

    private final Mouse mouse = new Mouse();

    public MainComponent() {
        BitmapFactory.createInstance(this);
        SoundFactory.createInstance(this);
        this.controller = new MainController(this);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setFocusable(true);
        addKeyListener(new Keyboard());
    }

    public void start() {
        controller.start();
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return getClass().getResourceAsStream("/" + name);
    }

    @Override
    public void paint(Graphics g) {
        controller.paint(g);
        mouse.paint(g);
    }

    private final class Mouse extends MouseAdapter {

        private static final int BUTTON_DOWN_MASK =
                MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;

        private MouseEvent e;

        private Mouse() {
            final BufferedImage emptyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            setCursor(getToolkit().createCustomCursor(emptyImage, new Point(), ""));
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public boolean isReleased() {
            return e == null || (e.getModifiersEx() & BUTTON_DOWN_MASK) == 0;
        }

        public void paint(Graphics g) {
            if (e == null) {
                return;
            }
            controller.drawCursor(g, e);
        }

        public void updateKeyModifiers(int modifiers) {
            if (e != null) {
                e = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(),
                        e.getModifiersEx() & BUTTON_DOWN_MASK | modifiers,
                        e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            repaint();
            this.e = e;
            controller.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            repaint();
            this.e = e;
            controller.mouseReleased(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            repaint();
            this.e = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            repaint();
            this.e = e;
            controller.mouseDragged(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            repaint();
            this.e = e;
        }
    }

    private final class Keyboard extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            repaint();
            mouse.updateKeyModifiers(e.getModifiersEx());
            controller.keyPressed(e, mouse.isReleased());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            repaint();
            mouse.updateKeyModifiers(e.getModifiersEx());
        }
    }
}
