package com.def.warlords.control.form;

import com.def.warlords.graphics.Cursor;

import javax.swing.Timer;
import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class TimedMessageForm extends MessageForm {

    private static final int DELAY = 3000;

    private Timer timer;

    public TimedMessageForm(FormController controller, String text) {
        super(controller, text);
    }

    @Override
    void init() {
        super.init();
        timer = createTimer(DELAY, e -> deactivate());
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    void close() {
        timer.stop();
    }

    @Override
    public Cursor getCursor(MouseEvent e) {
        return Cursor.EMPTY;
    }
}
