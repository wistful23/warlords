package com.def.warlords.control.form;

import com.def.warlords.gui.Button;
import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.InputBox;
import com.def.warlords.gui.TextButton;
import com.def.warlords.util.Util;

/**
 * @author wistful23
 * @version 1.23
 */
public class RecordForm extends ResultForm<Integer> {

    private final RecordType type;
    // If a record doesn't exist, its headline is null.
    private final String[] initialHeadlines;
    private final int initialIndex;
    private InputBox currentRecord;

    public RecordForm(FormController controller, RecordType type, String[] initialHeadlines, int initialIndex) {
        super(controller);
        this.type = type;
        this.initialHeadlines = initialHeadlines;
        this.initialIndex = initialIndex;
    }

    @Override
    void init() {
        add(new GrayPanel(105, 52, 430, 228));
        final Button okButton = add(new TextButton(139, 76, " " + type.getName() + " ", source -> {
            // `currentRecord` is never null if `okButton` is enabled.
            Util.assertNotNull(currentRecord);
            currentRecord.commitEditing();
            setResult((int) currentRecord.getTag());
        }));
        okButton.setEnabled(false);
        add(new TextButton(439, 76, " Cancel ", source -> {
            if (currentRecord != null) {
                currentRecord.cancelEditing();
            }
            setResult(-1);
        }));
        for (int index = 0; index < initialHeadlines.length; ++index) {
            final int x = index / 4;
            final int y = index % 4;
            final Button recordButton =
                    add(new TextButton(139 + x * 190, 116 + y * 40, " " + (index + 1) + " ", type == RecordType.SAVE));
            final String initialHeadline = initialHeadlines[index];
            final InputBox record = add(new InputBox(187 + x * 190, 118 + y * 40, 125, 15,
                    initialHeadline != null ? initialHeadline : "not used", controller::createTimer, recordButton,
                    source -> {
                        Util.assertTrue(source == currentRecord);
                        if (initialHeadline == null) {
                            okButton.setEnabled(false);
                            currentRecord.setSelected(false);
                            currentRecord = null;
                        }
                    }
            ));
            record.setTag(index);
            recordButton.setListener(source -> {
                if (currentRecord != null) {
                    currentRecord.setSelected(false);
                    currentRecord.cancelEditing();
                    // Here `currentRecord` could be null.
                }
                // Update `currentRecord`.
                currentRecord = record;
                currentRecord.setSelected(true);
                okButton.setEnabled(true);
                if (type == RecordType.SAVE) {
                    if (initialHeadline != null) {
                        currentRecord.startEditing();
                    } else {
                        // NOTE: W has weird logic for not used records.
                        currentRecord.startEditing("Warlords game");
                    }
                }
            });
            if (type == RecordType.LOAD && initialHeadline == null) {
                recordButton.setEnabled(false);
            }
            // Initialize `currentRecord`.
            if (index == initialIndex && initialHeadline != null) {
                currentRecord = record;
                currentRecord.setSelected(true);
                okButton.setEnabled(true);
            }
        }
    }

    public String getCurrentHeadline() {
        if (currentRecord != null) {
            return currentRecord.getText();
        }
        return null;
    }
}
