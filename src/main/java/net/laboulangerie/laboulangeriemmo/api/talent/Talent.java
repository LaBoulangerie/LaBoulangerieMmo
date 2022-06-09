package net.laboulangerie.laboulangeriemmo.api.talent;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class Talent {
    private double xp = 0;
    private String talentId;

    public Talent() {
        talentId = "default";
    }

    public Talent(String talentId) {
        this.talentId = talentId;
    }

    public double getXp() {
        return xp;
    }

    public void setXp(Double amount) {
        xp = amount;
    }

    public void incrementXp(double amount) {
        xp += amount;
    }

    public void decrementXp(double amount) {
        xp -= amount;
    }

    public int getLevel(double multiplier) {
        return (int) (multiplier * Math.sqrt(this.getXp()));
    }

    public int getLevelXp(double multiplier) {
        return (int) Math.pow(getLevel(multiplier) / multiplier, 2);
    }

    public double getXpToNextLevel(double multiplier) {
        return Math.pow((getLevel(multiplier) + 1) / multiplier, 2) - getLevelXp(multiplier);
    }

    public String getTalentId() {
        return talentId;
    }

    public String getDisplayName() {
        return LaBoulangerieMmo.talentsRegistry.getTalent(talentId).displayName;
    }
}
