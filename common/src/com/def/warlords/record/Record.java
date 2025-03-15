package com.def.warlords.record;

import java.io.IOException;

/**
 * @author wistful23
 * @version 1.23
 */
public interface Record {

    int TYPE_ID_SHIFT = 24;

    void write(RecordOutputStream out) throws IOException;

    void read(RecordInputStream in) throws IOException;

    default int getTypeId() {
        return 0;
    }
}
