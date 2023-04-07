package net.laboulangerie.laboulangeriemmo.core;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.xpboost.XpBoostObj;
import org.bukkit.Bukkit;
import org.bukkit.boss.KeyedBossBar;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class XpBoostManager {

    public List<XpBoostObj> list = new ArrayList<XpBoostObj>();

    public void init() {
        for (@NotNull Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
            KeyedBossBar bossBar = it.next();
            if(bossBar.getKey().getNamespace().equalsIgnoreCase("laboulangerie_xpboost")) {
                bossBar.removeAll();
                Bukkit.removeBossBar(bossBar.getKey());
            }
        }
    }

    public void add(XpBoostObj xpBoostObj) {
        list.add(xpBoostObj);
    }

    public List<XpBoostObj> getList() {
        return list;
    }

    public XpBoostObj getBoost(String identifier) {
        XpBoostObj target = null;
        for (XpBoostObj xpBoostObj : LaBoulangerieMmo.PLUGIN.getXpBoostManager().getList())
            if (xpBoostObj.getTalent().identifier.equalsIgnoreCase(identifier))
                if(target == null)
                    target = xpBoostObj;
                else if(xpBoostObj.getBoost() > target.getBoost())
                    target = xpBoostObj;

        return target;
    }

    public void expire(UUID uid) {
        Iterator<XpBoostObj> itr = getList().iterator();
        while (itr.hasNext()) {
            XpBoostObj xpBoostObj = (XpBoostObj) itr.next();
            if (xpBoostObj.getUid().equals(uid))
                itr.remove();
        }
    }
}