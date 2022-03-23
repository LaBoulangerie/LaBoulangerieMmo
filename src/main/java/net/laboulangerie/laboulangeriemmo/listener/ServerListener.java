package net.laboulangerie.laboulangeriemmo.listener;

import net.laboulangerie.laboulangeriemmo.core.hiding.InvisibleParticles;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.MarkedBlocksManager;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.player.ability.Abilities;
import net.laboulangerie.laboulangeriemmo.utils.Utils;

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

    /**
     * Apply knockback and damages when hitting an entity withe the DODGE ability
     * @param event
     */
    @EventHandler
    public void onRiptideHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity))
            return;
        Player player = (Player) event.getDamager();

        if (!player.isRiptiding())
            return;

        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);

        if (mmoPlayer == null) { //Shouldn't be possible but why not
            LaBoulangerieMmo.PLUGIN.getLogger().warning("Player : " + player.getName() + " doesn't have a MmoPlayer instance!");
            return;
        }

        if (!mmoPlayer.getCooldowns().hasUsed(Abilities.DODGING) || mmoPlayer.getCooldowns().getCooldown(Abilities.DODGING) >= 1) // 1 is the duration of the spin attack TODO: Change when real cooldown is set
            return;

        if (Utils.getAttackDamage(player, player.getInventory().getItemInMainHand()) > 0)
            event.setDamage(Utils.getAttackDamage(player, player.getInventory().getItemInMainHand()));

        ((LivingEntity) event.getEntity()).setVelocity(player.getLocation().getDirection().multiply(1));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        InvisibleParticles.renderParticles(event.getPlayer());
    }
}
