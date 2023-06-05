package net.laboulangerie.laboulangeriemmo.core.mapleaderboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.awt.Color;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriemmo.json.GsonSerializable;

public class LeaderBoardRenderer extends MapRenderer implements GsonSerializable {
    private transient boolean done = false;
    private HashMap<String, Double> elements;
    private String title;
    private String suffix;
    private byte x;
    private byte y;

    /**
     * @param elements Map of the elements to sort, {@code value} will be rounded when displayed
     * @param title First line of the map
     * @param suffix Unit of the {@code value}, will be displayed behind the {@code value}
     * @param x Coordinate of the map in the map combination
     * @param y Coordinate of the map in the map combination
     */
    public LeaderBoardRenderer(HashMap<String, Double> elements, String title, String suffix,
            byte x, byte y) {
        this.elements = elements;
        this.title = title;
        this.suffix = suffix;
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas canvas,
            @NotNull Player player) {
        if (done) return; // Avoid rendering each tick
        done = true;
        for (int x = 0; x < 128; ++x) // clear the canvas
            for (int y = 0; y < 128; ++y)
                canvas.setPixelColor(x, y, Color.BLACK);

        HashMap<String, Double> sortedElmts = elements.entrySet().stream() // Sort by value
                .sorted(Entry.comparingByValue((a, b) -> Double.compare(b, a))).collect(Collectors
                        .toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<String> formattedLines = sortedElmts.entrySet().stream()
                .map(entry -> "§32;" + entry.getKey() + " §92;-§120; "
                        + String.valueOf(Math.round(entry.getValue())) + " §92;" + suffix)
                .collect(Collectors.toList());

        int i = 0;
        if (y == 0) {
            canvas.drawText(0, 0, MinecraftFont.Font, splitPart(title, x));
            i = 1; // Avoid writing in top of the title later
        }

        for (String line : getMapPartText(formattedLines, x, y)) {
            if (!line.equals("")) canvas.drawText(0, i * 16, MinecraftFont.Font, line);
            i++;
            if (i > 7) break; // Max number of line in a map, shouldn't be called getPartText should
                              // handle this
        }
    }

    public void update(HashMap<String, Double> elements) {
        this.elements = elements;
        done = false; // Render will be done next tick
    }

    private List<String> getMapPartText(List<String> lines, byte x, byte y) { // TODO verify if last
                                                                              // line is well
                                                                              // displayed due to
                                                                              // some maps having a
                                                                              // title or not
        if (lines.size() <= y * 8) return Arrays.asList(""); // Nothing to display in this zone

        List<String> subLines =
                lines.subList(y * 7, (y * 7 + 7 > lines.size() ? lines.size() : y * 7 + 7));
        subLines = subLines.stream().map(line -> splitPart(line, x)).collect(Collectors.toList());

        return subLines;
    }

    /**
     * @param line
     * @param x
     * @return part of line that will be rendered in this map
     */
    private String splitPart(String line, byte x) { // TODO can probably be simplified, story for
                                                    // another time
        int totalIndex = 0; // Will store the start of the text to display in this map
        String text = "";
        String lastColor = ""; // Used to remember text's color between maps
        boolean inColorCode = false;

        // We face extra headaches because all letter doesn't have the same width and we have to
        // consider that color codes won't be rendered
        // MinecraftFont.Font.getWidth(text) returns the pixels taken by "text", but seems to
        // not be very precise (missing one or two pixels, need too look in more depth later)
        while (inColorCode
                || (MinecraftFont.Font.getWidth(text) <= x * 128 && totalIndex < line.length())) {
            String chr = line.substring(totalIndex, totalIndex + 1);
            if (chr.equals("§")) {
                inColorCode = true;
                lastColor = chr;
            } else if (inColorCode && chr.equals(";")) {
                inColorCode = false;
                lastColor += chr;
            } else if (inColorCode) {
                lastColor += chr;
            }
            text += chr;
            totalIndex++;
        }
        if (MinecraftFont.Font.getWidth(text) < x * 128) return ""; // The line is too short to
                                                                    // reach the researched section

        int totalIndexEnd = totalIndex;
        text = "";

        while (inColorCode
                || (MinecraftFont.Font.getWidth(text) < 128 && totalIndexEnd < line.length())) {
            String chr = line.substring(totalIndexEnd, totalIndexEnd + 1);
            if (chr.equals("§")) {
                inColorCode = true;
            } else if (inColorCode && chr.equals(";")) {
                inColorCode = false;
            }
            text += chr;
            totalIndexEnd++;
        }
        return lastColor + text;
    }
}
