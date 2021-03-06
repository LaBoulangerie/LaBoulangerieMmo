package net.laboulangerie.laboulangeriemmo.listener;

import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.core.abilities.mining.MarkedBlocksManager;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.FireBow;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.firebow.FireArrow;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.hiding.InvisiblePlayer;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
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
                .serialize(meta.lore().get(0)).startsWith("Quantit??:")) {

            int lvl = Integer.parseInt(PlainTextComponentSerializer.plainText()
                    .serialize(meta.lore().get(0)).split("Quantit??: ")[1].split(" ")[0]);
            event.setExperience(lvl);
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

        Optional<Entry<AbilityArchetype, Long>> ability = mmoPlayer.getCooldowns().getArchetypeCooldowns("dodging")
            .entrySet().stream().filter(e -> e.getValue() <= 1).findFirst(); // 1 is the duration of the spin attack

        if (ability.isEmpty()) return;

        if (Utils.getAttackDamage(player, player.getInventory().getItemInMainHand()) > 0)
            event.setDamage(Utils.getAttackDamage(player, player.getInventory().getItemInMainHand()) + (ability.get().getKey().getTier(1) > player.getLevel() ? 1 : 4 ));

        event.getEntity().setVelocity(player.getLocation().getDirection().multiply(ability.get().getKey().getTier(1) > player.getLevel() ? 3 : 5));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        InvisiblePlayer.hidePlayerArmor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        InvisiblePlayer.onJoin(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        InvisiblePlayer.onDamage(event.getEntity());
    }

    @EventHandler
    public void onProjectileShoot(EntityShootBowEvent event) {
        if (event.getProjectile().getFireTicks() > 0) EffectRegistry.playEffect("arrow", event.getProjectile());

        if (event.getEntity() instanceof Player player &&
                event.getProjectile() instanceof Arrow arrow &&
                event.getBow().hasItemMeta() &&
                event.getBow().getItemMeta().hasEnchant(Enchantment.ARROW_FIRE))
            FireArrow.shoot(player, arrow);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player player)
            FireBow.onArrowHit(player, arrow, event.getHitBlock(), event.getHitEntity());
    }
}
