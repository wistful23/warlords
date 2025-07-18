package com.def.warlords.desktop;

import com.def.warlords.control.MainController;
import com.def.warlords.platform.Platform;
import com.def.warlords.platform.PlatformHolder;
import com.def.warlords.sound.Sound;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class MainComponent extends JComponent implements Platform {

    private final MainController controller = new MainController();

    private final Mouse mouse = new Mouse();

    private SecondaryLoop secondaryLoop;

    static {
        ImageIO.setUseCache(false);
    }

    public MainComponent() {
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setFocusable(true);
        addKeyListener(new Keyboard());
    }

    public void start() {
        PlatformHolder.setPlatform(this);
        controller.start();
    }

    @Override
    public InputStream getResourceAsStream(String fileName) throws IOException {
        final InputStream in = getClass().getResourceAsStream("/" + fileName);
        if (in == null) {
            throw new FileNotFoundException("Resource is not found: " + fileName);
        }
        return in;
    }

    @Override
    public BufferedImage getBufferedImage(String fileName) throws IOException {
        return ImageIO.read(getResourceAsStream(fileName));
    }

    @Override
    public Sound getSound(String fileName, Runnable listener) throws IOException {
        final SimpleSound sound = new SimpleSound();
        sound.init(getResourceAsStream(fileName), listener, this::repaint);
        return sound;
    }

    @Override
    public void startSecondaryLoop() {
        if (secondaryLoop != null) {
            throw new IllegalStateException("Secondary loop is already started");
        }
        secondaryLoop = getToolkit().getSystemEventQueue().createSecondaryLoop();
        if (!secondaryLoop.enter()) {
            throw new IllegalStateException("Cannot start secondary loop");
        }
    }

    @Override
    public void stopSecondaryLoop() {
        if (secondaryLoop == null) {
            throw new IllegalStateException("Secondary loop is not started");
        }
        if (!secondaryLoop.exit()) {
            throw new IllegalStateException("Cannot stop secondary loop");
        }
        secondaryLoop = null;
    }

    @Override
    public void invokeLater(Runnable action, int delay) {
        if (delay > 0) {
            final Timer timer = new Timer(delay, (ActionEvent e) -> {
                repaint();
                action.run();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            EventQueue.invokeLater(action);
            repaint();
        }
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
