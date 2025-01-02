package com.def.warlords.gui;

import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.graphics.Palette;
import com.def.warlords.util.Timer;
import com.def.warlords.util.Util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.function.Function;

/**
 * @author wistful23
 * @version 1.23
 */
public class InputBox extends Component {

    public interface Listener {
        void editingCanceled(InputBox source);
    }

    private static final int DELAY_CURSOR_ANIMATION = 400;

    private static final Font font = FontFactory.getInstance().getMonospacedFont();

    private final int charCount;
    private String text;
    private final Button editButton;
    private final Listener listener;

    private final StringBuilder input;
    private int currentIndex;
    private boolean editing;

    private final Timer cursorTimer;
    private Color cursorColor = Palette.WHITE;

    public InputBox(int x, int y, int width, int charCount, String text,
                    Function<Runnable, Timer> timerCreator, Button editButton, Listener listener) {
        super(x, y, width, font.getHeight() + 6);
        this.charCount = charCount;
        this.text = text;
        this.editButton = editButton;
        this.listener = listener;
        this.input = new StringBuilder(charCount);
        this.cursorTimer = timerCreator.apply(() -> cursorColor = cursorColor == Palette.WHITE ? Palette.BLACK
                                                                                               : Palette.WHITE);
        editButton.setListener(source -> startEditing());
    }

    public String getText() {
        return text;
    }

    public void startEditing() {
        if (!editButton.isSelected()) {
            editButton.click();
            return;
        }
        startEditing(text);
    }

    public void startEditing(String text) {
        input.setLength(0);
        for (int index = 0; index < charCount; ++index) {
            char charToAppend = ' ';
            if (index < text.length()) {
                final char textChar = text.charAt(index);
                if (textChar >= ' ' && textChar <= '~') {
                    charToAppend = textChar;
                }
            }
            input.append(charToAppend);
        }
        currentIndex = 0;
        editing = true;
        cursorTimer.start(DELAY_CURSOR_ANIMATION, true);
    }

    public void commitEditing() {
        if (!editing) {
            return;
        }
        if (currentIndex > 0 && currentIndex < charCount - 1) {
            text = Util.trimStringSuffix(input.substring(0, currentIndex));
        } else {
            text = Util.trimStringSuffix(input.toString());
        }
        editing = false;
        cursorTimer.stop();
        editButton.release();
    }

    public void cancelEditing() {
        if (!editing) {
            return;
        }
        editing = false;
        cursorTimer.stop();
        editButton.release();
        if (listener != null) {
            listener.editingCanceled(this);
        }
    }

    @Override
    public void paint(Graphics g) {
        FramePainter.drawPressedFrame(g, x, y, width, height);
        // Background.
        g.setColor(Palette.GRAY);
        g.fillRect(x + 1, y + 2, width - 2, height - 4);
        // Text.
        if (editing) {
            g.setColor(Palette.BLACK);
            font.drawString(g, x + 3, y + 4, input.toString());
            // Cursor.
            char cursorChar = input.charAt(currentIndex);
            if (cursorColor == Palette.WHITE && cursorChar == ' ') cursorChar = '`';
            final int offset = font.getLength(input.substring(0, currentIndex));
            g.setColor(cursorColor);
            font.drawString(g, x + offset + 3, y + 4, cursorChar + "");
        } else {
            g.setColor(selected ? Palette.WHITE : Palette.BLACK);
            font.drawString(g, x + 3, y + 4, text);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!editing) {
            return;
        }
        final int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            if (currentIndex > 0) {
                --currentIndex;
                resetCursor();
            }
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            if (currentIndex + 1 < charCount) {
                ++currentIndex;
                resetCursor();
            }
        } else if (keyCode == KeyEvent.VK_DELETE) {
            input.deleteCharAt(currentIndex);
            input.append(' ');
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (currentIndex > 0) {
                input.deleteCharAt(--currentIndex);
                input.append(' ');
                resetCursor();
            }
        } else if (keyCode == KeyEvent.VK_ENTER) {
            commitEditing();
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            cancelEditing();
        } else {
            final char keyChar = e.getKeyChar();
            if (keyChar >= ' ' && keyChar <= '~') {
                input.setCharAt(currentIndex, keyChar);
                if (currentIndex + 1 < charCount) {
                    ++currentIndex;
                    resetCursor();
                }
            }
        }
    }

    private void resetCursor() {
        cursorTimer.start(DELAY_CURSOR_ANIMATION, true);
        cursorColor = Palette.WHITE;
    }
}
