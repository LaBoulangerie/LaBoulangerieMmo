package net.laboulangerie.laboulangeriemmo.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.laboulangerie.laboulangeriemmo.abilities.farmer.AnimalTwins;
import net.laboulangerie.laboulangeriemmo.abilities.farmer.BetterBonemeal;
import net.laboulangerie.laboulangeriemmo.abilities.farmer.NatureTouch;
import net.laboulangerie.laboulangeriemmo.abilities.farmer.TastyBread;
import net.laboulangerie.laboulangeriemmo.abilities.mining.FastMine;
import net.laboulangerie.laboulangeriemmo.abilities.mining.FastSmelt;
import net.laboulangerie.laboulangeriemmo.abilities.mining.MagneticField;
import net.laboulangerie.laboulangeriemmo.abilities.mining.MinecraftExpMultiplier;
import net.laboulangerie.laboulangeriemmo.abilities.thehunter.ExpInBottle;
import net.laboulangerie.laboulangeriemmo.abilities.thehunter.FireBow;
import net.laboulangerie.laboulangeriemmo.abilities.thehunter.Hiding;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.BetterAppleDrop;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.DoubleDropLog;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.ThickTree;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.Timber;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityTrigger;
import net.laboulangerie.laboulangeriemmo.api.ability.RegisterAbilitiesEvent;

public class AbilitiesListener implements Listener {
    @EventHandler
    public void onRegisterTime(RegisterAbilitiesEvent event) {
        event.getRegistry().registerAbility("fast-mine", FastMine.class, AbilityTrigger.COMBO);
        event.getRegistry().registerAbility("fast-smelt", FastSmelt.class, AbilityTrigger.LEFT_CLICK_BLOCK);
        event.getRegistry().registerAbility("minecraft-exp-multiplier", MinecraftExpMultiplier.class, AbilityTrigger.BREAK);
        event.getRegistry().registerAbility("magnetic-field", MagneticField.class, AbilityTrigger.RIGHT_CLICK_AIR);

        event.getRegistry().registerAbility("animal-twins", AnimalTwins.class, AbilityTrigger.BREED);
        event.getRegistry().registerAbility("better-bonemeal", BetterBonemeal.class, AbilityTrigger.RIGHT_CLICK_BLOCK);
        event.getRegistry().registerAbility("nature-touch", NatureTouch.class, AbilityTrigger.BREAK);
        event.getRegistry().registerAbility("tasty-bread", TastyBread.class, AbilityTrigger.HOLD_ITEM);

        event.getRegistry().registerAbility("dodging", FastMine.class, AbilityTrigger.COMBO);
        event.getRegistry().registerAbility("xp-in-bottle", ExpInBottle.class, AbilityTrigger.RIGHT_CLICK_AIR);
        event.getRegistry().registerAbility("fire-bow", FireBow.class, AbilityTrigger.COMBO);
        event.getRegistry().registerAbility("hiding", Hiding.class, AbilityTrigger.COMBO);

        event.getRegistry().registerAbility("better-apple-drop", BetterAppleDrop.class, AbilityTrigger.BREAK);
        event.getRegistry().registerAbility("double-drop-log", DoubleDropLog.class, AbilityTrigger.BREAK);
        event.getRegistry().registerAbility("strip", MinecraftExpMultiplier.class, AbilityTrigger.COMBO);
        event.getRegistry().registerAbility("timber", Timber.class, AbilityTrigger.BREAK);
        event.getRegistry().registerAbility("thick-tree", ThickTree.class, AbilityTrigger.COMBO);
    }
}
