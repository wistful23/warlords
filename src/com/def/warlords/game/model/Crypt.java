package com.def.warlords.game.model;

import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;

import java.io.IOException;

/**
 * @author wistful23
 * @version 1.23
 */
public class Crypt extends Building {

    private CryptType cryptType;
    private GuardType guardType;

    // Children.
    private Artifact artifact;

    public Crypt() {
    }

    public Crypt(BuildingType type, String name, CryptType cryptType, GuardType guardType) {
        super(type, name);
        this.cryptType = cryptType;
        this.guardType = guardType;
    }

    public CryptType getCryptType() {
        return cryptType;
    }

    public GuardType getGuardType() {
        return guardType;
    }

    // Dependencies.
    public Artifact getArtifact() {
        return artifact;
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        super.write(out);
        out.writeEnum(cryptType);
        out.writeEnum(guardType);
        // Children.
        out.writeRecord(artifact);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        super.read(in);
        cryptType = in.readEnum(CryptType.values());
        guardType = in.readEnum(GuardType.values());
        // Children.
        artifact = in.readRecord(Artifact::new);
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    // Registration.
    boolean registerArtifact(Artifact artifact) {
        // The artifact has already registered in this crypt.
        if (this.artifact == artifact) {
            return true;
        }
        // This crypt has the registered artifact.
        if (this.artifact != null) {
            return false;
        }
        // Unregister the artifact in the crypt.
        final Crypt artifactCrypt = artifact.getCrypt();
        if (artifactCrypt != null) {
            artifactCrypt.unregisterArtifact(artifact);
        }
        // Unregister the artifact in the tile.
        final Tile artifactTile = artifact.getTile();
        if (artifactTile != null) {
            artifactTile.unregisterArtifact(artifact);
        }
        // Unregister the artifact in the hero.
        final Hero artifactHero = artifact.getHero();
        if (artifactHero != null) {
            artifactHero.unregisterArtifact(artifact);
        }
        // Set this crypt in the artifact.
        artifact.setCrypt(this);
        // Add the artifact in this crypt.
        this.artifact = artifact;
        return true;
    }

    void unregisterArtifact(Artifact artifact) {
        // The artifact is not registered in this crypt.
        if (this.artifact != artifact) {
            return;
        }
        // Unset this crypt in the artifact.
        artifact.setCrypt(null);
        // Remove the artifact from this crypt.
        this.artifact = null;
    }
}
