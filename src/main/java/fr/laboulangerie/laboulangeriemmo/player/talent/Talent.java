package fr.laboulangerie.laboulangeriemmo.player.talent;

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

    public void incrementXp(double amount) {
        xp += amount;
    }

    public int getLevel(double multiplier) {
        return (int) Math.round(multiplier * Math.sqrt(this.getXp()));
    }

    public String getTalentId() {
        return talentId;
    }
}
