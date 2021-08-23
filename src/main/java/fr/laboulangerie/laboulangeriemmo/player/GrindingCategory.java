package fr.laboulangerie.laboulangeriemmo.player;

public enum GrindingCategory {
    BREAK,
    CRAFT,
    KILL,
    DISCOVER_RECIPE;

    public String toString() {
        return super.toString().toLowerCase();
    }
}
