package net.laboulangerie.laboulangeriemmo.api.xpboost;

import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class XpBoostManager {

    public List<XpBoostObj> list = new ArrayList<XpBoostObj>();

    public void add(XpBoostObj xpBoostObj) {
        list.forEach(item -> {
            if(item.getTalent().equals(xpBoostObj.getTalent())) {
                if (item.getBoost() < xpBoostObj.getBoost() && item.getTime() > xpBoostObj.getTime() || item.getTime() > xpBoostObj.getTime()) {
                    item.hideBossBar();
                } else {
                    xpBoostObj.setInitShownBossBar(false);
                }
            }
        });
        list.add(xpBoostObj);
    }

    public List<XpBoostObj> getList() {
        return list;
    }

    public XpBoostObj getBoost(String talentIdentifier){
        return getBoost(talentIdentifier, "boost");
    }

    public XpBoostObj getBoost(String talentIdentifier, String key) {
        XpBoostObj target = null;

        for (XpBoostObj xpBoostObj : getList()) {
            if (xpBoostObj.getTalent().identifier.equalsIgnoreCase(talentIdentifier)) {
                if (target == null) target = xpBoostObj;
                else {
                    if(key.equalsIgnoreCase("boost")){
                        if (xpBoostObj.getBoost() > target.getBoost() && xpBoostObj.getBoost() != target.getBoost()) target = xpBoostObj;
                    }
                    if(key.equalsIgnoreCase("timeLeft")){
                        if (xpBoostObj.getTime() < target.getTime()) target = xpBoostObj;
                    }
                }
            }
        }

        return target;
    }

    public void expire(UUID uid) {
        Iterator<XpBoostObj> itr = getList().iterator();
        TalentArchetype talentXpBoostRemove = null;
        while (itr.hasNext()) {
            XpBoostObj xpBoostObj = (XpBoostObj) itr.next();
            if (xpBoostObj.getUid().equals(uid)) {
                talentXpBoostRemove = xpBoostObj.getTalent();
                itr.remove();
            }
        }

        if(talentXpBoostRemove != null){
            XpBoostObj higher = getBoost(talentXpBoostRemove.identifier);
            XpBoostObj timeLeft = getBoost(talentXpBoostRemove.identifier, "timeLeft");
            TalentArchetype finalTalentXpBoostRemove = talentXpBoostRemove;
            list.forEach(item -> {
                if(item.getTalent().equals(finalTalentXpBoostRemove)) {
                    if (higher == null && item.getTime() > timeLeft.getTime()) {
                        item.hideBossBar();
                    } else {
                        assert higher != null;
                        if (item.getBoost() < higher.getBoost()) {
                            item.hideBossBar();
                        } else {
                            item.showBossBar();
                        }
                    }
                }
            });
        }

    }
}