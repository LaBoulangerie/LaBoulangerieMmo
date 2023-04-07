package net.laboulangerie.laboulangeriemmo.api.xpboost;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class XpBoostManager {

    public List<XpBoostObj> list = new ArrayList<XpBoostObj>();

    public void add(XpBoostObj xpBoostObj) {
        list.add(xpBoostObj);
    }

    public List<XpBoostObj> getList() {
        return list;
    }

    public XpBoostObj getBoost(String talentIdentifier) {
        XpBoostObj target = null;

        for (XpBoostObj xpBoostObj : getList()) {
            if (xpBoostObj.getTalent().identifier.equalsIgnoreCase(talentIdentifier)) {
                if (target == null) target = xpBoostObj;
                else if (xpBoostObj.getBoost() > target.getBoost()) target = xpBoostObj;
            }
        }

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