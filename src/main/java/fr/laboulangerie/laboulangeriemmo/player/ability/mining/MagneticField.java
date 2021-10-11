package fr.laboulangerie.laboulangeriemmo.player.ability.mining;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class MagneticField extends AbilityExecutor {

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.RIGHT_CLICK_AIR;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        ItemStack item = event.getItem();
        return event.getPlayer().isSneaking() && item != null && item.getType() == Material.COMPASS;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Player player = event.getPlayer();
        Location location = player.getLocation();
        // https://github.com/libraryaddict/LibsDisguises/blob/master/src/main/java/me/libraryaddict/disguise/utilities/packets/packethandlers/PacketHandlerSpawn.java
        PacketContainer spawnSlime = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        StructureModifier<Object> mods = spawnSlime.getModifier();

        mods.write(0, 1000000) //Entity ID
            .write(1, UUID.randomUUID()) // entity UUID
            .write(2, 75) // Entity type ID 75 = slime TODO chaange to 80 for 1.17
            .write(3, location.getX()) // Pos X, Y, Z
            .write(4, location.getY())
            .write(5, location.getZ())
            .write(6, (short) 0) // Velocity X, Y, Z
            .write(7, (short) 0)
            .write(8, (short) 0)
            .write(9, (byte) 0) // yaw
            .write(10, (byte) 0) // pitch
            .write(11, (byte) 0); // yaw

        PacketContainer slimeMetadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        StructureModifier<Object> metadataMods = slimeMetadata.getModifier();

        metadataMods.write(0, 1000000);
        WrappedDataWatcher newWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject obj = new WrappedDataWatcherObject(0, new WrappedDataWatcher.Serializer(byte.class, null, false));
        List<WrappedWatchableObject> watchableObjects = new ArrayList<WrappedWatchableObject>();
        slimeMetadata.getWatchableCollectionModifier().write(0, watchableObjects);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawnSlime);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
}
