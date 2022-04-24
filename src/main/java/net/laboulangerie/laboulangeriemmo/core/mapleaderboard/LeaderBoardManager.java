package net.laboulangerie.laboulangeriemmo.core.mapleaderboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class LeaderBoardManager {

    private static LeaderBoardManager instance = null;
    private MapDataSave dataFile;
    private List<Integer> usedMaps;
    private List<Integer> unusedMaps;

    public LeaderBoardManager() {
        try {
            dataFile = new MapDataSave("mapdata.yml");
        } catch (IOException e) {
            LaBoulangerieMmo.PLUGIN.getLogger().severe("Unable to load save data of the leaderboard maps: " + e.getMessage());
            return;
        }
        loadMaps();
    }

    public static LeaderBoardManager getInstance() {
        if (instance == null)
            instance = new LeaderBoardManager();
        return instance;
    }

    public List<Integer> createLeaderBoard(LeaderBoardRenderer renderer, int width, int height, Player player) throws IOException {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Dimensions cannot be negative or null");
        if (width > 10 || height > 10)
            throw new IllegalArgumentException("Dimensions cannot be greater than 10");

        MapView mapView = null;
        if (unusedMaps.size() > 0) {// Maps are in limited amount so we reuse maps has much has possible
            mapView = Bukkit.getMap(unusedMaps.get(0));
            unusedMaps.remove(0);
        }else {
            mapView = Bukkit.createMap(player.getWorld());
        }

        for (MapRenderer defaultRenderer : mapView.getRenderers())//Clean potential ancient Renderers
            mapView.removeRenderer(defaultRenderer);

        mapView.addRenderer(renderer);
        mapView.setScale(Scale.FARTHEST);
        mapView.setTrackingPosition(false);

        saveMap(mapView.getId());

        return Arrays.asList(mapView.getId());
    }

    /**
     * Frees a map so we can reuse it later because minecraft has a limited number of maps
     * @param id
     * @throws IOException
     */
    public void freeMap(Integer id) throws IOException {
        if (!usedMaps.contains(id)) throw new IllegalArgumentException("No map with id: " + id);
        usedMaps.remove(id);
        unusedMaps.add(id);
        saveData();
    }

    /**
     * Update the renderer of the map 
     * @param id
     * @throws IllegalArgumentException if {@code id} is invalid
     */
    public void updateMap(Integer id, HashMap<String, Double> elements) throws IllegalArgumentException {
        if (!usedMaps.contains(id)) throw new IllegalArgumentException("No map with id: " + id);

        for (MapRenderer renderer : Bukkit.getMap(id).getRenderers()) {            
            if (renderer instanceof LeaderBoardRenderer) {
                ((LeaderBoardRenderer) renderer).update(elements);
                break;
            }
        }
    }

    public ItemStack getMapItem(Integer id) throws IllegalArgumentException {
        if (!usedMaps.contains(id)) throw new IllegalArgumentException("No map with id: " + id);
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) map.getItemMeta();

        meta.setMapView(Bukkit.getMap(id));
        map.setItemMeta(meta);
        return map;
    }

    /**
     * /!\ Frees all maps, debug purpose only
     * @throws IOException
     */
    public void freeAllMaps() throws IOException {
        unusedMaps.addAll(usedMaps);
        usedMaps.clear();
        saveData();
    }

    public void saveMap(Integer id) throws IOException {
        usedMaps.add(id);
        saveData();
    }

    private void loadMaps() {
        if (getData().contains("used"))
            usedMaps = getData().getIntegerList("used");
        else
            usedMaps = new ArrayList<>();
        
        if (getData().contains("unused"))
            unusedMaps = getData().getIntegerList("unused");
        else
            unusedMaps = new ArrayList<>();
    }

    public boolean hasMap(int id) {
        return usedMaps.contains(id);
    }

    private FileConfiguration getData() {
        return dataFile.getSave();
    }

    private void saveData() throws IOException {
        getData().set("used", usedMaps);
        getData().set("unused", unusedMaps);
        dataFile.save();
    }

    class MapDataSave {
        private FileConfiguration dataYAML;
        private File dataFile;
        private final String name;

        public MapDataSave(String name) throws IOException {
            this.name = name;
            ensureFileExists();
            this.dataYAML = YamlConfiguration.loadConfiguration(dataFile);
        }

        private void ensureFileExists() throws IOException {
            dataFile = new File(LaBoulangerieMmo.PLUGIN.getDataFolder(), name);
            if (!dataFile.exists())
                dataFile.createNewFile();
        }

        public FileConfiguration getSave() {
            return this.dataYAML;
        }

        public void save() throws IOException {
            dataYAML.save(dataFile);
        }
    }
}