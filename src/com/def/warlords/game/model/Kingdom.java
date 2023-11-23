package com.def.warlords.game.model;

import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;
import com.def.warlords.util.Logger;
import com.def.warlords.util.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class Kingdom implements Record {

    public static final int MAP_WIDTH = 109;
    public static final int MAP_HEIGHT = 156;

    private static final int MAP_SIZE = MAP_WIDTH * MAP_HEIGHT;

    private static final int NEIGHBOR_TILE_COUNT = 8;
    private static final int[] dx = {0, 0, -1, 1, -1, 1, 1, -1};
    private static final int[] dy = {-1, 1, 0, 0, -1, 1, -1, 1};

    private static final int CRYPT_INDEX_OFFSET = 8;

    private static final int MAX_NEUTRAL_GUARD_COUNT = 3;

    private static final ArmyFactory neutralGuardFactory = new ArmyFactory(ArmyType.LT_INF, 0, 0, 1, 0);

    private static final String[] wisdomNotes = {
            "Never leave Lord Bane behind you! - Great Orc",
            "Real power is power over life and death! - Greenbow",
            "Never give a Sirian an even break!",
            "The pen is mightier than the Dwarf! - Lord Biros",
            "Elves do very nasty things to small animals!",
            "Never play leapfrog with a Unicorn!",
            "A mouse with one hole is soon eaten! - Baron de Chat",
            "My kingdom for an Orc! - Richard 111th",
            "War has no rules!",
            "War is the natural extension of nature!",
            "You can never have enough money! - Eldros",
            "For great computer games, go to SSG! - Don Stephano"
    };

    private static final int BORDER_EQUATOR = 74;
    private static final int BORDER_WEST = 24;
    private static final int BORDER_CENTRAL = 51;
    private static final int BORDER_EAST = 78;

    public static String getLands(Tile tile) {
        if (tile.getPosY() < BORDER_EQUATOR) {
            if (tile.getPosX() < BORDER_WEST) {
                return "Western Lauredor";
            } else if (tile.getPosX() < BORDER_CENTRAL) {
                return "Central Lauredor";
            } else if (tile.getPosX() < BORDER_EAST) {
                return "Eastern Lauredor";
            } else {
                return "Argundor";
            }
        } else {
            if (tile.getPosX() < BORDER_WEST) {
                return "Western Sulador";
            } else if (tile.getPosX() < BORDER_CENTRAL) {
                return "Central Sulador";
            } else if (tile.getPosX() < BORDER_EAST) {
                return "Eastern Sulador";
            } else {
                return "Huinedor";
            }
        }
    }

    public static String getRandomWisdomNote() {
        return wisdomNotes[Util.randomInt(wisdomNotes.length)];
    }

    private final Empire[] empires = new Empire[EmpireType.COUNT];
    private final Tile[] tiles = new Tile[MAP_SIZE];
    private final List<City> cities = new ArrayList<>();
    private final List<Building> buildings = new ArrayList<>();
    private final List<Artifact> artifacts = new ArrayList<>();
    private final List<ArmyFactory> allyFactories = new ArrayList<>();
    private final List<String> heroNames = new ArrayList<>();

    public boolean init() {
        initEmpires();
        return initMap() && initCities() && initBuildings() && initArtifacts() &&
                initAllyFactories() && initHeroNames();
    }

    public Empire getEmpire(EmpireType empireType) {
        return empires[empireType.ordinal()];
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
            return tiles[y * MAP_WIDTH + x];
        }
        return null;
    }

    public Tile getNeighborTile(Tile tile, int index, int direction) {
        return getTile(tile.getPosX() + direction * dx[index], tile.getPosY() + direction * dy[index]);
    }

    public List<Tile> getNeighborTiles(Tile tile, boolean includeSelf) {
        final List<Tile> neighborTiles = new ArrayList<>(NEIGHBOR_TILE_COUNT + (includeSelf ? 1 : 0));
        if (includeSelf) {
            neighborTiles.add(tile);
        }
        for (int index = 0; index < NEIGHBOR_TILE_COUNT; ++index) {
            final Tile neighborTile = getNeighborTile(tile, index, 1);
            if (neighborTile != null) {
                neighborTiles.add(neighborTile);
            }
        }
        return neighborTiles;
    }

    public int getCityCount() {
        return cities.size();
    }

    public List<City> getCities() {
        return new ArrayList<>(cities);
    }

    public List<Building> getBuildings() {
        return new ArrayList<>(buildings);
    }

    public Crypt getRandomCrypt() {
        return (Crypt) buildings.get(CRYPT_INDEX_OFFSET + Util.randomInt(buildings.size() - CRYPT_INDEX_OFFSET));
    }

    public List<Artifact> getArtifacts() {
        return new ArrayList<>(artifacts);
    }

    public Artifact getRandomArtifact() {
        return artifacts.get(Util.randomInt(artifacts.size()));
    }

    public ArmyFactory getAllyFactory(int index) {
        return allyFactories.get(index);
    }

    public ArmyFactory getAllyFactory(GuardType guard) {
        return allyFactories.get(guard.ordinal());
    }

    public ArmyFactory getRandomAllyFactory() {
        return allyFactories.get(Util.randomInt(allyFactories.size()));
    }

    public String getRandomHeroName() {
        return heroNames.get(Util.randomInt(heroNames.size()));
    }

    private void initEmpires() {
        for (final EmpireType empireType : EmpireType.values()) {
            empires[empireType.ordinal()] = new Empire(empireType);
        }
    }

    private boolean initMap() {
        try (final InputStream in = getClass().getResourceAsStream("/illuria.map")) {
            if (in == null) {
                throw new FileNotFoundException("Map was not found");
            }
            final byte[] map = new byte[MAP_SIZE * 2];
            final int size = in.read(map);
            if (size < map.length) {
                Logger.error("Unexpected map size: " + size + " != " + map.length);
                return false;
            }
            for (int index = 0; index < MAP_SIZE; ++index) {
                tiles[index] = new Tile(index % MAP_WIDTH, index / MAP_WIDTH, map[index * 2 + 1]);
            }
        } catch (IOException e) {
            Logger.error("Failed to read map");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean initCities() {
        try (final BufferedReader br = createBufferedReader("cities.txt")) {
            while (br.ready()) {
                final String s = br.readLine();
                final String name = s.substring(0, 15).trim();
                final int pos = Integer.parseInt(s.substring(16, 21).trim());
                final String portToken = s.substring(23, 28).trim();
                final Tile portTile = !portToken.equals("-") ? tiles[Integer.parseInt(portToken)] : null;
                final int defence = Integer.parseInt(s.substring(31, 32));
                final int income = Integer.parseInt(s.substring(36, 38).trim());
                final String empireName = s.substring(41, 53).trim().toUpperCase().replace(' ', '_');
                final EmpireType empireType =
                        empireName.equals("-") ? EmpireType.NEUTRAL : EmpireType.valueOf(empireName);
                final City city = new City(name, tiles[pos], portTile, defence, income);
                for (int dy = 0; dy < 2; ++dy) {
                    for (int dx = 0; dx < 2; ++dx) {
                        Util.assertTrue(city.registerTile(tiles[pos + dy * MAP_WIDTH + dx]));
                    }
                }
                for (int i = 0, p = 54; i < City.MAX_FACTORY_COUNT && p < s.length(); ++i, p += 24) {
                    Util.assertTrue(city.registerFactory(parseArmyFactory(s, p)));
                }
                // NOTE: W doesn't locate real armies to the neutral cities.
                if (empireType == EmpireType.NEUTRAL) {
                    final ArmyGroup group = new ArmyGroup();
                    final int guardCount = Util.randomInt(MAX_NEUTRAL_GUARD_COUNT) + 1;
                    for (int i = 0; i < guardCount; ++i) {
                        Util.assertTrue(group.registerArmy(neutralGuardFactory.produce()));
                    }
                    Util.assertTrue(tiles[pos].registerGroup(group));
                }
                Util.assertTrue(empires[empireType.ordinal()].registerCity(city));
                cities.add(city);
            }
        } catch (IOException e) {
            Logger.error("Failed to read cities");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean initBuildings() {
        final List<CryptType> cryptTypes = new ArrayList<>();
        for (final CryptType cryptType : CryptType.values()) {
            for (int i = 0; i < cryptType.getCount(); ++i) {
                cryptTypes.add(cryptType);
            }
        }
        Collections.shuffle(cryptTypes);
        try (final BufferedReader br = createBufferedReader("buildings.txt")) {
            int index = 0;
            while (br.ready()) {
                final String s = br.readLine();
                final String name = s.substring(0, 18).trim();
                final int pos = Integer.parseInt(s.substring(20, 25).trim());
                final BuildingType type = BuildingType.valueOf(s.substring(27).toUpperCase());
                final Building building;
                if (index < CRYPT_INDEX_OFFSET) {
                    Util.assertFalse(type.isCrypt());
                    building = new Building(type, name);
                } else {
                    Util.assertTrue(type.isCrypt());
                    final CryptType cryptType = cryptTypes.get(index - CRYPT_INDEX_OFFSET);
                    // Random guard.
                    final int guardCount = cryptType == CryptType.ALLIES ? GuardType.ALLY_COUNT : GuardType.COUNT;
                    final GuardType guardType = GuardType.values()[Util.randomInt(guardCount)];
                    building = new Crypt(type, name, cryptType, guardType);
                    Logger.info(name + " " + cryptType + " " + guardType);
                }
                Util.assertTrue(tiles[pos].registerBuilding(building));
                buildings.add(building);
                ++index;
            }
            Util.assertTrue(index - CRYPT_INDEX_OFFSET == cryptTypes.size());
        } catch (IOException e) {
            Logger.error("Failed to read buildings");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean initArtifacts() {
        final List<Crypt> crypts = new ArrayList<>();
        for (int index = CRYPT_INDEX_OFFSET; index < buildings.size(); ++index) {
            final Crypt crypt = (Crypt) buildings.get(index);
            if (crypt.getCryptType() == CryptType.ARTIFACT) {
                crypts.add(crypt);
            }
        }
        Collections.shuffle(crypts);
        try (final BufferedReader br = createBufferedReader("artifacts.txt")) {
            int index = 0;
            while (br.ready()) {
                final String s = br.readLine();
                final String name = s.substring(0, 17).trim();
                final int battle = Integer.parseInt(s.substring(19, 20));
                final int command = Integer.parseInt(s.substring(22, 23));
                final Artifact artifact = new Artifact(name, battle, command);
                Util.assertTrue(crypts.get(index).registerArtifact(artifact));
                artifacts.add(artifact);
                ++index;
            }
            Util.assertTrue(index == crypts.size());
        } catch (IOException e) {
            Logger.error("Failed to read artifacts");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean initAllyFactories() {
        try (final BufferedReader br = createBufferedReader("allies.txt")) {
            while (br.ready()) {
                allyFactories.add(parseArmyFactory(br.readLine(), 0));
            }
        } catch (IOException e) {
            Logger.error("Failed to read ally factories");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean initHeroNames() {
        try (final BufferedReader br = createBufferedReader("heroes.txt")) {
            while (br.ready()) {
                heroNames.add(br.readLine());
            }
        } catch (IOException e) {
            Logger.error("Failed to read hero names");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private BufferedReader createBufferedReader(String resourceName) throws IOException {
        final InputStream in = getClass().getResourceAsStream("/" + resourceName);
        if (in == null) {
            throw new FileNotFoundException("Resource was not found: " + resourceName);
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        // Skip header.
        if (br.ready()) {
            br.readLine();
        }
        return br;
    }

    private ArmyFactory parseArmyFactory(String s, int offset) {
        final ArmyType type = ArmyType.valueOf(s.substring(offset, offset + 8).trim().toUpperCase().replace(' ', '_'));
        final int time = Integer.parseInt(s.substring(offset + 10, offset + 12).trim());
        final int cost = Integer.parseInt(s.substring(offset + 13, offset + 15).trim());
        final int strength = Integer.parseInt(s.substring(offset + 16, offset + 18).trim());
        final int movement = Integer.parseInt(s.substring(offset + 19, offset + 21).trim());
        return new ArmyFactory(type, time, cost, strength, movement);
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeRecordArray(empires);
        out.writeRecordArray(tiles);
        out.writeRecordList(cities);
        out.writeRecordList(buildings);
        out.writeRecordList(artifacts);
        out.writeRecordList(allyFactories);
        out.writeStringList(heroNames);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        in.readRecordArray(empires, Empire::new);
        in.readRecordArray(tiles, Tile::new);
        in.readRecordList(cities, City::new);
        in.readRecordList(buildings, Building::new, Crypt::new);
        in.readRecordList(artifacts, Artifact::new);
        in.readRecordList(allyFactories, ArmyFactory::new);
        in.readStringList(heroNames);
    }
}
