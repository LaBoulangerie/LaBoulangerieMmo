package net.laboulangerie.laboulangeriemmo.core.blockus;

import java.io.IOException;

public interface BlockusManager {
    public void writeBlockuses() throws IOException;
    public BlockusDataHolder readBlockuses() throws IOException, ClassNotFoundException;
    public BlockusDataHolder getBlockusDataHolder();
}
