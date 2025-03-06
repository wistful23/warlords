package com.def.warlords.control.form;

/**
 * @author wistful23
 * @version 1.23
 */
public enum RecordType {

    SAVE("Save"),
    LOAD("Load");

    private final String name;

    RecordType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
