package com.def.warlords.game.model;

import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;

import java.io.IOException;

/**
 * @author wistful23
 * @version 1.23
 */
public class Building implements Record, Locatable {

    private BuildingType type;
    private String name;

    private boolean explored;

    // Parents.
    private Tile tile;

    public Building() {
    }

    public Building(BuildingType type, String name) {
        this.type = type;
        this.name = name;
    }

    public BuildingType getType() {
        return type;
    }

    public boolean isCrypt() {
        return type.isCrypt();
    }

    public boolean isTemple() {
        return type == BuildingType.TEMPLE;
    }

    public boolean isSage() {
        return type == BuildingType.SAGE;
    }

    public boolean isLibrary() {
        return type == BuildingType.LIBRARY;
    }

    public String getName() {
        return name;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    // Dependencies.
    public Tile getTile() {
        return tile;
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeEnum(type);
        out.writeString(name);
        out.writeBoolean(explored);
        // Parents.
        out.writeRecord(tile);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        type = in.readEnum(BuildingType.values());
        name = in.readString();
        explored = in.readBoolean();
        // Parents.
        tile = in.readRecord(Tile::new);
    }

    @Override
    public int getPosX() {
        return tile.getPosX();
    }

    @Override
    public int getPosY() {
        return tile.getPosY();
    }

    // Registration.
    void setTile(Tile tile) {
        this.tile = tile;
    }
}
