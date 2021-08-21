package fr.laboulangerie.laboulangeriemmo.player.talent;

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

    public void incrementXp(double amount) {
        xp += amount;
    }

    public int getLevel(double multiplier) {
        return (int) Math.round(multiplier * Math.sqrt(this.getXp()));
    }

    public String getTalentId() {
        return talentId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
