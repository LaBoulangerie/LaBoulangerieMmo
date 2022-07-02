package net.laboulangerie.laboulangeriemmo.core.abilities.mining;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.AbstractStructure;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import net.minecraft.world.entity.EntityType;

public class MarkedBlocksManager {
    private static MarkedBlocksManager INSTANCE = null;
    private Map<Block, BlockWatcher> markedBlocks = new HashMap<Block, BlockWatcher>();
    private int idCount = 100000;
    private String[] teamsIdentifier = { "DIAMOND_ORE", "IRON_ORE", "COAL_ORE", "GOLD_ORE", "LAPIS_ORE", "REDSTONE_ORE",
            "EMERALD_ORE", "COPPER_ORE", "ANCIENT_DEBRIS", "NETHER_GOLD_ORE", "AMETHYST_CLUSTER" };
    private ChatColor[] teamsColor = { ChatColor.AQUA, ChatColor.GRAY, ChatColor.BLACK, ChatColor.YELLOW,
            ChatColor.DARK_BLUE, ChatColor.RED, ChatColor.GREEN, ChatColor.GOLD, ChatColor.DARK_GRAY, ChatColor.YELLOW,
            ChatColor.DARK_PURPLE };

    public void markBlock(Block block, Player player) {
        BlockWatcher blockWatcher = markedBlocks.get(block);
        if (blockWatcher == null) {
            UUID uuid = UUID.randomUUID();
            sendShulker(player, block, idCount, uuid);
            markedBlocks.put(block, new BlockWatcher(idCount, uuid, player));
            idCount++;
        } else {
            if (blockWatcher.isWatching(player))
                return;

            sendShulker(player, block, blockWatcher.getEntityId(), blockWatcher.getUuid());
            blockWatcher.addWatcher(player);
        }
    }

    /**
     * Remove the mark on the block for all watchers, called when the block is
     * destroyed
     */
    public void unmarkBlock(Block block) {
        BlockWatcher blockWatcher = markedBlocks.get(block);
        if (blockWatcher == null)
            return;

        blockWatcher.getWatchers().stream().forEach(p -> removeShulker(p, blockWatcher.getEntityId()));
        markedBlocks.remove(block);
    }

    public void unmarkBlock(Block block, Player player) {
        BlockWatcher blockWatcher = markedBlocks.get(block);
        if (blockWatcher == null)
            return;

        removeShulker(player, blockWatcher.getEntityId());
        if (blockWatcher.removeWatcher(player))
            markedBlocks.remove(block);
    }

    private void sendShulker(Player player, Block block, Integer id, UUID uuid) {
        // https://github.com/libraryaddict/LibsDisguises/blob/master/plugin/src/main/java/me/libraryaddict/disguise/utilities/packets/packethandlers/PacketHandlerSpawn.java
        PacketContainer spawnShulker = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        StructureModifier<Object> mods = spawnShulker.getModifier();

        mods.write(0, id) // Entity ID
                .write(1, uuid) // entity UUID
                .write(2, EntityType.SHULKER)
                .write(3, block.getX() + 0.5) // Pos X, Y, Z
                .write(4, block.getY())
                .write(5, block.getZ() + 0.5)
                .write(6, (short) 0) // Velocity X, Y, Z
                .write(7, (short) 0)
                .write(8, (short) 0)
                .write(9, (byte) 0) // pitch
                .write(10, (byte) 0) // yaw
                .write(11, (byte) 0); // yaw

        PacketContainer shulkerMetadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        StructureModifier<Object> metadataMods = shulkerMetadata.getModifier();

        metadataMods.write(0, id);

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(new WrappedDataWatcherObject(0, Registry.get(Byte.class)), (byte) (0x20 | 0x40)); // 0x20
                                                                                                            // invisible
                                                                                                            // 0x40
                                                                                                            // glowing
        shulkerMetadata.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawnShulker);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, shulkerMetadata);
    }

    private void removeShulker(Player player, int id) {
        PacketContainer destroyShulker = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyShulker.getIntLists().write(0, Arrays.asList(id));

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroyShulker);
    }

    public void colorize(Block block, Player player) {
        BlockWatcher blockWatcher = markedBlocks.get(block);
        if (blockWatcher == null || block.getType() == Material.NETHER_QUARTZ_ORE)
            return; // Quartz is white no need for a team
        String teamId = block.getType().toString().startsWith("DEEPSLATE_") ? block.getType().toString().substring(10)
                : block.getType().toString();

        PacketContainer updateTeam = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        updateTeam.getStrings().write(0, teamId);
        updateTeam.getIntegers()
                .write(0, 3); // Mode: 3 = ADD ENTITIES
        updateTeam.getSpecificModifier(Collection.class).write(0,
                Collections.singletonList(blockWatcher.getUuid().toString()));

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, updateTeam);
    }

    public void setupTeams(Player player) { // https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/scoreboard/PacketScoreboardTeam.java
        for (int i = 0; i < teamsIdentifier.length; i++) {
            PacketContainer team = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            AbstractStructure struct = team.getOptionalStructures().readSafely(0).get();

            team.getStrings().write(0, teamsIdentifier[i]); // team id
            team.getIntegers().write(0, 0x0);// Mode: 0 = CREATE

            struct.getStrings().write(0, "always") // name tag visibility rules
                    .write(1, "never"); // collisions rules
            struct.getIntegers().write(0, 0x02); // flags
            struct.getChatComponents()
                    .write(0, WrappedChatComponent.fromText(""))
                    .write(1, WrappedChatComponent.fromText(""))
                    .write(2, WrappedChatComponent.fromText(""));
            struct.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,
                    teamsColor[i]);

            team.getOptionalStructures().write(0, Optional.of((InternalStructure) struct));
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, team);
        }
    }

    public static MarkedBlocksManager manager() {
        if (INSTANCE == null) {
            INSTANCE = new MarkedBlocksManager();
        }
        return INSTANCE;
    }
}
