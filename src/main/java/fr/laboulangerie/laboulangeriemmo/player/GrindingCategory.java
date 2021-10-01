package fr.laboulangerie.laboulangeriemmo.player;

public enum GrindingCategory {
    BREAK,
    CRAFT,
    KILL,
    FIRST_CRAFT;

    public String toString() {
        return super.toString().toLowerCase();
    }
}
