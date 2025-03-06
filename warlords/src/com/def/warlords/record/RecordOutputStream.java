package com.def.warlords.record;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wistful23
 * @version 1.23
 */
public class RecordOutputStream implements Closeable {

    private final OutputStream out;

    private final Map<Record, Integer> ids = new HashMap<>();
    private int id;

    public RecordOutputStream(OutputStream out) {
        this.out = out;
    }

    public void writeInt(int val) throws IOException {
        while ((val & ~0x7f) != 0) {
            out.write(val & 0x7f | 0x80);
            val >>>= 7;
        }
        out.write(val);
    }

    public void writeBoolean(boolean val) throws IOException {
        out.write(val ? 1 : 0);
    }

    public <E extends Enum<E>> void writeEnum(E e) throws IOException {
        writeInt(e.ordinal());
    }

    public <E extends Enum<E>> void writeEnumArray(E[] array) throws IOException {
        for (final E e : array) {
            writeEnum(e);
        }
    }

    public void writeString(String str) throws IOException {
        writeInt(str.length());
        out.write(str.getBytes());
    }

    public void writeStringList(List<String> list) throws IOException {
        writeInt(list.size());
        for (final String str : list) {
            writeString(str);
        }
    }

    public void writeRecord(Record record) throws IOException {
        if (record == null) {
            writeInt(0);
            return;
        }
        if (ids.containsKey(record)) {
            writeInt(ids.get(record));
            return;
        }
        ids.put(record, ++id);
        writeInt(id | record.getTypeId() << Record.TYPE_ID_SHIFT);
        record.write(this);
    }

    public void writeRecordArray(Record[] array) throws IOException {
        for (final Record record : array) {
            writeRecord(record);
        }
    }

    public void writeRecordList(List<? extends Record> list) throws IOException {
        writeInt(list.size());
        for (final Record record : list) {
            writeRecord(record);
        }
    }

    public void close() throws IOException {
        out.close();
    }
}
