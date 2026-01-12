package com.def.warlords.gui;

import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.graphics.Palette;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class Keyboard extends Container {

    public interface Listener {
        void keyPressed(int keyCode, char keyChar);
    }

    private static final String[] lines = {
            "1234567890<",
            "QWERTYUIOP-",
            "ASDFGHJKL,.",
            "^ZXCVBNM' "
    };

    private static final int BUTTON_WIDTH = 28;
    private static final int BUTTON_HEIGHT = 32;

    public static final int WIDTH = BUTTON_WIDTH * lines[0].length() + 2;
    public static final int HEIGHT = BUTTON_HEIGHT * lines.length + 4;

    private Listener listener;
    private boolean upperCaseMode;

    public Keyboard(int x, int y) {
        super(x, y, WIDTH, HEIGHT);
        // Buttons.
        int sx = 0;
        int sy = 0;
        for (final String line : lines) {
            for (int index = 0; index < line.length(); ++index) {
                add(new KeyButton(x + sx + 1, y + sy + 2, line.charAt(index)));
                sx += BUTTON_WIDTH;
            }
            sx = 0;
            sy += BUTTON_HEIGHT;
        }
        hide();
    }

    public void show(Listener listener) {
        this.listener = listener;
        this.upperCaseMode = true;
        setEnabled(true);
        setVisible(true);
    }

    public void hide() {
        setEnabled(false);
        setVisible(false);
    }

    @Override
    public void paint(Graphics g) {
        FramePainter.drawBlackFrame(g, x, y, width, height);
        super.paint(g);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (activeComponent != null) {
            activeComponent.setSelected(false);
            activeComponent = null;
        }
        for (final Component component : components) {
            if (component.contains(e.getPoint())) {
                component.setSelected(true);
                activeComponent = component;
                break;
            }
        }
    }

    private final class KeyButton extends Component {

        private final char keyChar;

        private KeyButton(int x, int y, char keyChar) {
            super(x, y, keyChar == ' ' ? BUTTON_WIDTH * 2 : BUTTON_WIDTH, BUTTON_HEIGHT);
            this.keyChar = keyChar;
        }

        @Override
        public boolean mousePressed(MouseEvent e) {
            setSelected(true);
            return true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            setSelected(false);
            if (keyChar == '^') {
                upperCaseMode = !upperCaseMode;
                return;
            }
            if (listener != null) {
                final int keyCode = keyChar == '<' ? KeyEvent.VK_BACK_SPACE : KeyEvent.VK_UNDEFINED;
                listener.keyPressed(keyCode, getFixedKeyChar());
            }
            if (keyChar >= 'A' && keyChar <= 'Z') {
                upperCaseMode = false;
            }
        }

        @Override
        public void paint(Graphics g) {
            if (isSelected()) {
                FramePainter.drawPressedFrame(g, x, y, width, height);
                g.setColor(Palette.YELLOW);
            } else {
                FramePainter.drawReleasedFrame(g, x, y, width, height);
                g.setColor(upperCaseMode && keyChar == '^' ? Palette.GREEN : Palette.GRAY);
            }
            g.fillRect(x + 1, y + 2, width - 2, height - 4);
            // Key char.
            g.setColor(Palette.BLACK);
            final String s = getFixedKeyChar() + "";
            final Font font = FontFactory.getInstance().getMonospacedFont();
            font.drawString(g, x + (width - font.getLength(s)) / 2, y + (height - font.getHeight()) / 2, s);
        }

        private char getFixedKeyChar() {
            if (upperCaseMode || keyChar < 'A' || keyChar > 'Z') {
                return keyChar;
            }
            return (char) (keyChar - 'A' + 'a');
        }
    }
}
