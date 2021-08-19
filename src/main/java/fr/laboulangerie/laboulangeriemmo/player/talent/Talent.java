package fr.laboulangerie.laboulangeriemmo.player.talent;

public class Talent {

    private int xp = 0;
    private String talentId;

    public Talent() {
        talentId = "default";
    }

    public Talent(String talentId) {
        this.talentId = talentId;
    }

    public int getXp() {
        return xp;
    }

    public void incrementXp(int amount) {
        xp += amount;
    }

    public int getLevel(double multiplier) {
        return (int) Math.round(multiplier * Math.sqrt(this.getXp()));
    }

    public String getTalentId() {
        return talentId;
    }
}
