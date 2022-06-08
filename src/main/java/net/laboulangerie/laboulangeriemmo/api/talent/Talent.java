package net.laboulangerie.laboulangeriemmo.api.talent;

public class Talent {
    private double xp = 0;
    private String talentId;
    private String displayName;

    public Talent() {
        talentId = "default";
        displayName = "default";
    }

    public Talent(String talentId, String displayName) {
        this.talentId = talentId;
        this.displayName = displayName;
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
        return displayName;
    }
    
}
