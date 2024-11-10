package net.laboulangerie.laboulangeriemmo.listener;

import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.commands.talenttree.TalentTreeInv;
import net.laboulangerie.laboulangeriemmo.core.abilities.mining.MarkedBlocksManager;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.FireBow;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.firebow.FireArrow;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.hiding.InvisiblePlayer;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.utils.Utils;

public class ServerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK || event.useItemInHand() == Result.DENY) return;

        MarkedBlocksManager.manager().unmarkBlock(event.getClickedBlock());
    }

    @EventHandler
    public void onExpBottle(ExpBottleEvent event) {
        ItemStack bottle = event.getEntity().getItem();
        ItemMeta meta = bottle.getItemMeta();
        if (meta.hasLore()
                && PlainTextComponentSerializer.plainText().serialize(meta.lore().get(0)).startsWith("Quantité:")) {

            int expPoints = Integer.parseInt(
                    PlainTextComponentSerializer.plainText().serialize(meta.lore().get(0)).split("Quantité: ")[1]
                            .split(" ")[0]);
            event.setExperience(expPoints);
        }
    }

    /**
     * Apply knockback and damages when hitting an entity withe the DODGE ability
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRiptideHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getDamager() instanceof Player)
                || !(event.getEntity() instanceof LivingEntity))
            return;
        Player player = (Player) event.getDamager();

        if (!player.isRiptiding()) return;

        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);

        if (mmoPlayer == null) { // Shouldn't be possible but why not
            LaBoulangerieMmo.PLUGIN.getLogger()
                    .warning("Player : " + player.getName() + " doesn't have a MmoPlayer instance!");
            return;
        }

        Optional<Entry<AbilityArchetype, Long>> ability = mmoPlayer.getCooldowns().getArchetypeCooldowns("dodging")
                .entrySet().stream().filter(e -> e.getValue() <= 1).findFirst(); // 1 is the duration of the
                                                                                 // spin attack

        if (ability.isEmpty()) return;

        if (Utils.getAttackDamage(player, player.getInventory().getItemInMainHand()) > 0)
            event.setDamage(Utils.getAttackDamage(player, player.getInventory().getItemInMainHand())
                    + (ability.get().getKey().getTier(1) > player.getLevel() ? 1 : 4));

        event.getEntity().setVelocity(player.getLocation().getDirection()
                .multiply(ability.get().getKey().getTier(1) > player.getLevel() ? 3 : 5));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        InvisiblePlayer.hidePlayerArmor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        InvisiblePlayer.onJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        InvisiblePlayer.onDamage(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileShoot(EntityShootBowEvent event) {
        if (event.getProjectile().getFireTicks() > 0) EffectRegistry.playEffect("arrow", event.getProjectile());

        if (event.getEntity() instanceof Player player && event.getProjectile() instanceof AbstractArrow arrow
                && event.getBow().hasItemMeta() && event.getBow().getItemMeta().hasEnchant(Enchantment.FLAME))
            FireArrow.shoot(player, arrow);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof AbstractArrow arrow && arrow.getShooter() instanceof Player player)
            FireBow.onArrowHit(player, arrow, event.getHitBlock(), event.getHitEntity());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || !(inventory.getHolder() instanceof TalentTreeInv)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory == null || !(inventory.getHolder() instanceof TalentTreeInv))
            return;
        event.setCancelled(true);
    }
}
