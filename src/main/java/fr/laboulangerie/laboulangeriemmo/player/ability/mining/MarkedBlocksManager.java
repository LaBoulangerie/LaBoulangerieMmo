package fr.laboulangerie.laboulangeriemmo.player.ability.mining;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MarkedBlocksManager {
    private static MarkedBlocksManager INSTANCE = null;
    private Map<Block, Integer> markedBlocks = new HashMap<Block, Integer>();
    private int idCount = 100000;

    public void markBlock(Block block, Player player) {
        Integer id = markedBlocks.get(block);
        if (id == null) {
            sendShulker(player, block, idCount);
            idCount++;
        }
    }
    private void sendShulker(Player player, Block block, Integer id) {
        // https://github.com/libraryaddict/LibsDisguises/blob/master/src/main/java/me/libraryaddict/disguise/utilities/packets/packethandlers/PacketHandlerSpawn.java
        PacketContainer spawnShulker = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        StructureModifier<Object> mods = spawnShulker.getModifier();

        mods.write(0, id) //Entity ID
            .write(1, UUID.randomUUID()) // entity UUID
            .write(2, 70) // Entity type ID 70 (wiki.vg is saying 69, funny guys...) = shulker TODO change to 75 for 1.17
            .write(3, block.getX()+0.5) // Pos X, Y, Z
            .write(4, block.getY())
            .write(5, block.getZ()+0.5)
            .write(6, (short) 0) // Velocity X, Y, Z
            .write(7, (short) 0)
            .write(8, (short) 0)
            .write(9, (byte) 0) // yaw
            .write(10, (byte) 0) // pitch
            .write(11, (byte) 0); // yaw

        PacketContainer shulkerMetadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        StructureModifier<Object> metadataMods = shulkerMetadata.getModifier();

        metadataMods.write(0, id);

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(new WrappedDataWatcherObject(0, Registry.get(Byte.class)),(byte) (0x20 | 0x40)); //0x20 invisible 0x40 glowing
        shulkerMetadata.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawnShulker);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, shulkerMetadata);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static MarkedBlocksManager manager() {
        if (INSTANCE == null) {
            INSTANCE = new MarkedBlocksManager();
        }
        return INSTANCE;
    }
}
