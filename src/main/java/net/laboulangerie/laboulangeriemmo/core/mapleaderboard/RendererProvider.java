package net.laboulangerie.laboulangeriemmo.core.mapleaderboard;

import java.io.File;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.utils.FileUtils;

public class RendererProvider {
    private File dataFaloder;

    public RendererProvider(File folder) {
        dataFaloder = folder;
    }

    public void store(int id, LeaderBoardRenderer renderer) {
        FileUtils.save(new File(dataFaloder, id + ".json"),
                LaBoulangerieMmo.PLUGIN.getSerializer().serialize(renderer));
    }

    public LeaderBoardRenderer provide(int id) {
        return (LeaderBoardRenderer) LaBoulangerieMmo.PLUGIN.getSerializer().deserialize(
                FileUtils.read(new File(dataFaloder, id + ".json")), LeaderBoardRenderer.class);
    }

    public void remove(int id) {
        File save = new File(dataFaloder, id + ".json");
        if (save.exists()) save.delete();
    }
}
