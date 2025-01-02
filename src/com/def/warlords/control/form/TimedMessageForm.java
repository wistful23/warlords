package com.def.warlords.control.form;

import com.def.warlords.graphics.Cursor;
import com.def.warlords.util.Timer;

import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class TimedMessageForm extends MessageForm {

    private static final int DELAY_MESSAGE = 3000;

    private Timer timer;

    public TimedMessageForm(FormController controller, String text) {
        super(controller, text);
    }

    @Override
    void init() {
        super.init();
        timer = createTimer(this::deactivate);
        timer.start(DELAY_MESSAGE);
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
