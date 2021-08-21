package fr.laboulangerie.laboulangeriemmo.player;

public enum GrindingCategory {
    BREAK,
    CRAFT,
    KILL;

    public String toString() {
        return super.toString().toLowerCase();
    }
}
