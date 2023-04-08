package net.laboulangerie.laboulangeriemmo.api.player;

public enum GrindingCategory {
    BREAK, CRAFT, KILL, FIRST_CRAFT;

    public String toString() {
        return super.toString().toLowerCase();
    }
}
