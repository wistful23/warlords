package com.def.warlords.game.model;

import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;

import java.io.IOException;

/**
 * @author wistful23
 * @version 1.23
 */
public class Artifact implements Record {

    private String name;
    private int battle;
    private int command;

    // Parents.
    private Crypt crypt;
    private Tile tile;
    private Hero hero;

    public Artifact() {
    }

    public Artifact(String name, int battle, int command) {
        this.name = name;
        this.battle = battle;
        this.command = command;
    }

    public String getName() {
        return name;
    }

    public int getBattle() {
        return battle;
    }

    public int getCommand() {
        return command;
    }

    // Dependencies.
    public Crypt getCrypt() {
        return crypt;
    }

    public Tile getTile() {
        return tile;
    }

    public Hero getHero() {
        return hero;
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeString(name);
        out.writeInt(battle);
        out.writeInt(command);
        // Parents.
        out.writeRecord(crypt);
        out.writeRecord(tile);
        out.writeRecord(hero);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        name = in.readString();
        battle = in.readInt();
        command = in.readInt();
        // Parents.
        crypt = in.readRecord(null, Crypt::new);
        tile = in.readRecord(Tile::new);
        hero = in.readRecord(null, Hero::new);
    }

    // Registration.
    void setCrypt(Crypt crypt) {
        this.crypt = crypt;
    }

    void setTile(Tile tile) {
        this.tile = tile;
    }

    void setHero(Hero hero) {
        this.hero = hero;
    }
}
