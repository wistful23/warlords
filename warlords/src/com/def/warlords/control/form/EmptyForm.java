package com.def.warlords.control.form;

import com.def.warlords.graphics.Cursor;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class EmptyForm extends Form {

    public EmptyForm(FormController controller) {
        super(controller);
    }

    @Override
    public Cursor getCursor(MouseEvent e) {
        return Cursor.MODAL;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        deactivate();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        deactivate();
    }
}
