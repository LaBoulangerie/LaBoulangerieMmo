package net.laboulangerie.laboulangeriemmo.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.MarkedBlocksManager;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;

public class ServerListener implements Listener {
    public ServerListener() {
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        boolean hasMetaData = block.hasMetadata("laboulangerie:placed");
        if (!hasMetaData) {
            block.setMetadata("laboulangerie:placed",
                    new FixedMetadataValue(LaBoulangerieMmo.PLUGIN, player.getUniqueId().toString()));
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        MarkedBlocksManager.manager().unmarkBlock(event.getClickedBlock());
    }

    @EventHandler
    public void onExpBottle(ExpBottleEvent event) {
        ItemStack bottle = event.getEntity().getItem();
        ItemMeta meta = bottle.getItemMeta();
        if (meta.hasLore() && PlainTextComponentSerializer.plainText()
                .serialize(meta.lore().get(0)).startsWith("Quantité:")) {

            int lvl = Integer.parseInt(PlainTextComponentSerializer.plainText()
                    .serialize(meta.lore().get(0)).split("Quantité: ")[1].split(" ")[0]);
            event.setExperience(lvl);
        }
    }

    @EventHandler
    public void onShootArrow(EntityShootBowEvent event) {
        if (event.getProjectile().getFireTicks() > 0) {
            EffectRegistry.playEffect("arrow", event.getProjectile());
        }
    }
}
