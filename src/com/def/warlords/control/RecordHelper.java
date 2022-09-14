package com.def.warlords.control;

import com.def.warlords.game.Game;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;
import com.def.warlords.util.Logger;

import java.io.*;

/**
 * @author wistful23
 * @version 1.23
 */
public final class RecordHelper {

    private static final int RECORD_FORMAT_VERSION = 1;

    private static final int RECORD_COUNT = 8;

    public static String[] loadRecordHeadlines() {
        final String[] headlines = new String[RECORD_COUNT];
        for (int index = 0; index < RECORD_COUNT; ++index) {
            final String fileName = getSaveFileName(index);
            try (final RecordInputStream in =
                         new RecordInputStream(new BufferedInputStream(new FileInputStream("./" + fileName)))) {
                final int version = in.readInt();
                if (version == RECORD_FORMAT_VERSION) {
                    headlines[index] = in.readString();
                } else {
                    Logger.warn("Saved game " + fileName + " has incompatible format version: " +
                            version + " != " + RECORD_FORMAT_VERSION);
                }
            } catch (FileNotFoundException ignore) {
            } catch (IOException e) {
                Logger.error("Failed to read headline for saved game " + fileName);
                e.printStackTrace();
            }
        }
        return headlines;
    }

    public static boolean save(int index, String headline, int posX, int posY, Game game) {
        final String fileName = getSaveFileName(index);
        try (final RecordOutputStream out =
                     new RecordOutputStream(new BufferedOutputStream(new FileOutputStream("./" + fileName)))) {
            out.writeInt(RECORD_FORMAT_VERSION);
            out.writeString(headline);
            out.writeInt(posX);
            out.writeInt(posY);
            out.writeRecord(game);
            Logger.info("Successfully saved game '" + headline + "' to " + fileName);
        } catch (IOException e) {
            Logger.error("Failed to save game '" + headline + "' to " + fileName);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static RecordData load(int index) {
        final String fileName = getSaveFileName(index);
        final RecordData recordData;
        try (final RecordInputStream in =
                     new RecordInputStream(new BufferedInputStream(new FileInputStream("./" + fileName)))) {
            final int version = in.readInt();
            if (version != RECORD_FORMAT_VERSION) {
                throw new IOException("Incompatible version: " + version + " != " + RECORD_FORMAT_VERSION);
            }
            final String headline = in.readString();
            recordData = new RecordData(in.readInt(), in.readInt(), in.readRecord(Game::new));
            Logger.info("Successfully loaded game '" + headline + "' from " + fileName);
        } catch (IOException e) {
            Logger.error("Failed to load game from " + fileName);
            e.printStackTrace();
            return null;
        }
        return recordData;
    }

    private static String getSaveFileName(int index) {
        return "war" + (index + 1) + ".sav";
    }

    private RecordHelper() {
    }
}
