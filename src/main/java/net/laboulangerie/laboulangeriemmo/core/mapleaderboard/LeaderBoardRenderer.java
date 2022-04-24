package net.laboulangerie.laboulangeriemmo.core.mapleaderboard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;

public class LeaderBoardRenderer extends MapRenderer {
    private boolean done = false;
    private HashMap<String, Double> elements;
    private String title;
    private String suffix;
    /**
     * 
     * @param elements Map of the elements to sort, {@code value} will be rounded when displayed
     * @param title First line of the map
     * @param suffix Unit of the {@code value}, will be displayed behind the {@code value}
     */
    public LeaderBoardRenderer(HashMap<String, Double> elements, String title, String suffix) {
        this.elements = elements;
        this.title = title;
        this.suffix = suffix;
    }

    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas canvas, @NotNull Player player) {
        if (done) return;
        done = true;
        for (int x = 0; x < 128; ++x) //clear the canvas
            for (int y = 0; y < 128; ++y)
                canvas.setPixel(x, y, (byte) 0);
    
        HashMap<String, Double> sortedElmt = elements.entrySet().stream() // Sort by value
            .sorted(Entry.comparingByValue((a, b) -> Double.compare(b, a)))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        canvas.drawText(0, 0, MinecraftFont.Font, title);

        int i = 1;
        for (Iterator<Entry<String, Double>> iterator = sortedElmt.entrySet().iterator(); iterator.hasNext();) {
            Entry<String, Double> entry = iterator.next();
            canvas.drawText(0, i*16, MinecraftFont.Font, "§32;" + entry.getKey() + " §92;-§120; " + String.valueOf(Math.round(entry.getValue())) + " §92;" + suffix);
            i++;
            if (i > 7) break;
        }
    }

    public void update(HashMap<String, Double> elements) {
        this.elements = elements;
        done = false;
    }
}