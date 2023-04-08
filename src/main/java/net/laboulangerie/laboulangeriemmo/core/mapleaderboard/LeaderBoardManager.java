package net.laboulangerie.laboulangeriemmo.core.mapleaderboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class LeaderBoardManager implements Listener {
    private static LeaderBoardManager instance = null;
    private RendererProvider rendererProvider;
    private MapDataSave dataFile;
    private List<Integer> usedMaps;
    private List<Integer> unusedMaps;

    public LeaderBoardManager(File dataFolder) {
        if (!dataFolder.exists()) dataFolder.mkdir();
        rendererProvider = new RendererProvider(dataFolder);
        try {
            dataFile = new MapDataSave(dataFolder.getPath() + "/mapdata.yml");
        } catch (IOException e) {
            LaBoulangerieMmo.PLUGIN.getLogger()
                    .severe("Unable to load save data of the leaderboard maps: " + e.getMessage());
            return;
        }
        loadMaps();
    }

    public static LeaderBoardManager getInstance() {
        if (instance == null) instance =
                new LeaderBoardManager(new File(LaBoulangerieMmo.PLUGIN.getDataFolder(), "/maps/"));
        return instance;
    }

    @SuppressWarnings("deprecation")
    public List<Integer> createLeaderBoard(HashMap<String, Double> pretenders, String title,
            String unit, int width, int height) throws IOException {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Dimensions cannot be negative or null");
        if (width > 10 || height > 10)
            throw new IllegalArgumentException("Dimensions cannot be greater than 10");
        List<Integer> createdMaps = new ArrayList<>();

        for (byte x = 0; x < width; x++) {
            for (byte y = 0; y < height; y++) {
                MapView mapView = null;
                if (unusedMaps.size() > 0) {// Maps are in limited amount so we reuse maps has much
                                            // has possible
                    mapView = Bukkit.getMap(unusedMaps.get(0));
                    unusedMaps.remove(0);
                } else {
                    mapView = Bukkit.createMap(Bukkit.getWorlds().get(0));
                }

                for (MapRenderer defaultRenderer : mapView.getRenderers())// Clean potential ancient
                                                                          // Renderers
                    mapView.removeRenderer(defaultRenderer);

                mapView.addRenderer(new LeaderBoardRenderer(pretenders, title, unit, x, y));
                mapView.setScale(Scale.FARTHEST);
                mapView.setTrackingPosition(false);

                createdMaps.add(mapView.getId());
                rendererProvider.store(mapView.getId(),
                        (LeaderBoardRenderer) mapView.getRenderers().get(0)); // Store the rendered
                                                                              // to restore it on
                                                                              // restart
            }
        }
        usedMaps.addAll(createdMaps);
        saveData();
        return createdMaps;
    }

    /**
     * Frees a map so we can reuse it later because minecraft has a limited number of maps
     * 
     * @param id
     * @throws IOException
     */
    public void freeMap(Integer id) throws IOException {
        if (!usedMaps.contains(id)) throw new IllegalArgumentException("No map with id: " + id);
        usedMaps.remove(id);
        unusedMaps.add(id);
        rendererProvider.remove(id);
        saveData();
    }

    /**
     * Update the renderer of the map
     * 
     * @param id
     * @throws IllegalArgumentException if {@code id} is invalid
     */
    @SuppressWarnings("deprecation")
    public void updateMap(Integer id, HashMap<String, Double> pretenders)
            throws IllegalArgumentException {
        if (!usedMaps.contains(id)) throw new IllegalArgumentException("No map with id: " + id);

        for (MapRenderer renderer : Bukkit.getMap(id).getRenderers()) {
            if (renderer instanceof LeaderBoardRenderer) {
                ((LeaderBoardRenderer) renderer).update(pretenders);
                break;
            }
        }
    }

    @SuppressWarnings("deprecation")
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
     * 
     * @throws IOException
     */
    public void freeAllMaps() throws IOException {
        unusedMaps.addAll(usedMaps);
        usedMaps.forEach((map) -> rendererProvider.remove(map));
        usedMaps.clear();
        saveData();
    }

    private void loadMaps() {
        if (getData().contains("used")) usedMaps = getData().getIntegerList("used");
        else
            usedMaps = new ArrayList<>();

        if (getData().contains("unused")) unusedMaps = getData().getIntegerList("unused");
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

    @EventHandler
    public void onMapInit(MapInitializeEvent event) {
        if (usedMaps.contains(event.getMap().getId())) {
            for (MapRenderer defaultRenderer : event.getMap().getRenderers())// Clean potential
                                                                             // ancient Renderers
                event.getMap().removeRenderer(defaultRenderer);
            event.getMap().addRenderer(rendererProvider.provide(event.getMap().getId()));
        }
    }

    class MapDataSave {
        private FileConfiguration dataYAML;
        private File dataFile;
        private final String name;

        public MapDataSave(String name) throws IOException {
            this.name = name;
            ensureFileExists();
            dataYAML = YamlConfiguration.loadConfiguration(dataFile);
        }

        private void ensureFileExists() throws IOException {
            dataFile = new File(name);
            if (!dataFile.exists()) dataFile.createNewFile();
        }

        public FileConfiguration getSave() {
            return dataYAML;
        }

        public void save() throws IOException {
            dataYAML.save(dataFile);
        }
    }
}
