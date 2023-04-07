package net.laboulangerie.laboulangeriemmo.listener;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.xpboost.XpBoostObj;
import net.laboulangerie.laboulangeriemmo.events.PlayerEarnsXpEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class XpBoostListener implements Listener {

    @EventHandler
    public static void onPlayerChangeXP(PlayerEarnsXpEvent event){
        //Get Boost Current
        XpBoostObj xpBoostObjTarget = LaBoulangerieMmo.PLUGIN.getXpBoostManager().getBoost(event.getTalentName()); //Get boost higher in list (Where Talent)
        if(xpBoostObjTarget != null){
            double amountCalc = event.getAmount() * xpBoostObjTarget.getBoost();
            event.setAmount(amountCalc);
        }
    }

    @EventHandler
    public static void onPlayerJoinEvent(PlayerJoinEvent event){
        for(XpBoostObj xpBoostObj : LaBoulangerieMmo.PLUGIN.getXpBoostManager().getList())
            event.getPlayer().showBossBar(xpBoostObj.getBossBar());
    }

    @EventHandler
    public static void onPlayerQuitEvent(PlayerQuitEvent event){
        for(XpBoostObj xpBoostObj : LaBoulangerieMmo.PLUGIN.getXpBoostManager().getList())
            event.getPlayer().hideBossBar(xpBoostObj.getBossBar());
    }
}
