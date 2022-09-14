package com.def.warlords.record;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author wistful23
 * @version 1.23
 */
public class RecordInputStream implements Closeable {

    private final InputStream in;

    private final Map<Integer, Record> records = new HashMap<>();

    public RecordInputStream(InputStream in) {
        this.in = in;
    }

    public int readInt() throws IOException {
        int val = 0;
        int l = 0;
        int b;
        do {
            b = in.read();
            if (b == -1) {
                throw new IOException("unexpected end of stream");
            }
            val |= (b & 0x7f) << l;
            l += 7;
        } while ((b & 0x80) != 0);
        return val;
    }

    public boolean readBoolean() throws IOException {
        final int b = in.read();
        if (b == -1) {
            throw new IOException("unexpected end of stream");
        }
        return b != 0;
    }

    public <E extends Enum<E>> E readEnum(E[] values) throws IOException {
        return values[readInt()];
    }

    public <E extends Enum<E>> void readEnumArray(E[] array, E[] values) throws IOException {
        for (int index = 0; index < array.length; ++index) {
            array[index] = readEnum(values);
        }
    }

    public String readString() throws IOException {
        final int len = readInt();
        if (len < 0) {
            throw new IOException("negative string length");
        }
        final byte[] b = new byte[len];
        final int num = in.read(b);
        if (num != len) {
            throw new IOException("unexpected end of stream");
        }
        return new String(b);
    }

    public void readStringList(List<String> list) throws IOException {
        final int size = readInt();
        for (int i = 0; i < size; ++i) {
            list.add(readString());
        }
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T extends Record> T readRecord(Supplier<T>... suppliers) throws IOException {
        final int id = readInt();
        if (id == 0) {
            return null;
        }
        if (records.containsKey(id)) {
            return (T) records.get(id);
        }
        final T record = suppliers[id >>> Record.TYPE_ID_SHIFT].get();
        records.put(id & (1 << Record.TYPE_ID_SHIFT) - 1, record);
        record.read(this);
        return record;
    }

    @SafeVarargs
    public final <T extends Record> void readRecordArray(T[] array, Supplier<T>... suppliers) throws IOException {
        for (int index = 0; index < array.length; ++index) {
            array[index] = readRecord(suppliers);
        }
    }

    @SafeVarargs
    public final <T extends Record> void readRecordList(List<T> list, Supplier<T>... suppliers) throws IOException {
        final int size = readInt();
        for (int i = 0; i < size; ++i) {
            list.add(readRecord(suppliers));
        }
    }

    public void close() throws IOException {
        in.close();
    }
}
