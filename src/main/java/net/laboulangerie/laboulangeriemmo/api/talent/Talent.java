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

    public int getLevel() {
        return (int) (LaBoulangerieMmo.XP_MULTIPLIER * Math.sqrt(getXp()));
    }

    public int getLevelXp() {
        return (int) Math.pow(getLevel() / LaBoulangerieMmo.XP_MULTIPLIER, 2);
    }

    public double getXpToNextLevel() {
        return Math.pow((getLevel() + 1) / LaBoulangerieMmo.XP_MULTIPLIER, 2) - getLevelXp();
    }

    public String getTalentId() {
        return talentId;
    }

    /**
     * 
     * @return The display name or the identifier if this talent isn't found in the {@link net.laboulangerie.laboulangeriemmo.api.talent.TalentsRegistry TalentsRegistry}
     */
    public String getDisplayName() {
        return LaBoulangerieMmo.talentsRegistry.getTalent(talentId) == null ? talentId : LaBoulangerieMmo.talentsRegistry.getTalent(talentId).displayName;
    }
}
