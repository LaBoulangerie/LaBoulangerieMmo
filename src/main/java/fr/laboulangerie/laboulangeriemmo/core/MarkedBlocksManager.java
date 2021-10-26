package fr.laboulangerie.laboulangeriemmo.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MarkedBlocksManager {
    private static MarkedBlocksManager INSTANCE = null;
    private Map<Block, BlockWatcher> markedBlocks = new HashMap<Block, BlockWatcher>();
    private int idCount = 100000;
    private String[] teamsIdentifier = {"DIAMOND_ORE", "IRON_ORE", "COAL_ORE", "GOLD_ORE", "LAPIS_ORE", "EMERALD_ORE", "ANCIENT_DEBRIS", "REDSTONE_ORE"};
    private ChatColor[] teamsColor = {ChatColor.AQUA, ChatColor.GRAY, ChatColor.BLACK, ChatColor.YELLOW, ChatColor.DARK_BLUE, ChatColor.GREEN, ChatColor.GOLD, ChatColor.RED};

    public void markBlock(Block block, Player player) {
        BlockWatcher blockWatcher = markedBlocks.get(block);
        if (blockWatcher == null) {
            UUID uuid = UUID.randomUUID();
            sendShulker(player, block, idCount, uuid);
            markedBlocks.put(block, new BlockWatcher(idCount, uuid, player));
            idCount++;
        }else {
            if (blockWatcher.isWatching(player)) return;

            sendShulker(player, block, blockWatcher.getEntityId(), blockWatcher.getUuid());
            blockWatcher.addWatcher(player);
        }
    }

    /**
     * Remove the mark on the block for all watchers, called when the block is destroyed
     */
    public void unmarkBlock(Block block) {
        BlockWatcher blockWatcher = markedBlocks.get(block);
        if (blockWatcher == null) return;

        blockWatcher.getWatchers().stream().forEach(p -> removeShulker(p, blockWatcher.getEntityId()));
        markedBlocks.remove(block);
    }

    public void unmarkBlock(Block block, Player player) {
        BlockWatcher blockWatcher = markedBlocks.get(block);
        if (blockWatcher == null) return;

        removeShulker(player, blockWatcher.getEntityId());
        if (blockWatcher.removeWatcher(player)) markedBlocks.remove(block);
    }
    private void sendShulker(Player player, Block block, Integer id, UUID uuid) {
        // https://github.com/libraryaddict/LibsDisguises/blob/master/src/main/java/me/libraryaddict/disguise/utilities/packets/packethandlers/PacketHandlerSpawn.java
        PacketContainer spawnShulker = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        StructureModifier<Object> mods = spawnShulker.getModifier();

        mods.write(0, id) //Entity ID
            .write(1, uuid) // entity UUID
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

    private void removeShulker(Player player, int id) {
        PacketContainer destroyShulker = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyShulker.getIntegerArrays().write(0, new int[]{id});

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroyShulker);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void colorize(Block block, Player player) {
        BlockWatcher blockWatcher = markedBlocks.get(block);
        if (blockWatcher == null) return;

        PacketContainer updateTeam = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        updateTeam.getStrings().write(0, block.getType().toString());
        updateTeam.getIntegers()
            .write(0, 3); //Mode: 3 = ADD ENTITIES
        updateTeam.getSpecificModifier(Collection.class).write(0, Collections.singletonList(blockWatcher.getUuid().toString()));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, updateTeam);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void setupTeams(Player player) { //https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/scoreboard/PacketScoreboardTeam.java
        for (int i = 0; i < teamsIdentifier.length; i++) {
            PacketContainer team = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            team.getStrings()
                .write(0, teamsIdentifier[i]) //team id
                .write(1, "always") //name tag visibility rules
                .write(2, "never"); //collisions rules
            team.getIntegers()
                .write(0, 0x0) //Mode: 0 = CREATE
                .write(1, 0x02); //flags
            team.getChatComponents()
                .write(0, WrappedChatComponent.fromText(""))
                .write(1, WrappedChatComponent.fromText(""))
                .write(2, WrappedChatComponent.fromText(""));
            team.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, teamsColor[i]);
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, team);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static MarkedBlocksManager manager() {
        if (INSTANCE == null) {
            INSTANCE = new MarkedBlocksManager();
        }
        return INSTANCE;
    }
}
