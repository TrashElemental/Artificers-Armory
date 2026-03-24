package net.trashelemental.artificers_armory.entity.ai.familiar;

public enum FamiliarSkin {
    DEFAULT(0, "Buddy"),
    ALLAY(1, "Allay"),
    VEX(2, "Vex"),
    PROTECTOR(3, "Garde"),
    BRUISER(4, "Buster"),
    PRANKSTER(5, "Imp"),
    HEALER(6, "Angel"),
    GABBY(7, "Gabby"),
    ZAGGY(8, "Zaggy"),
    CAMMY(9, "Cammy"),
    SPAMTON(10, "Spamton"),
    MAGE(11, "Mage"),
    SCARECROW(12, "Scarecrow");

    private final int id;
    private final String triggerName;

    FamiliarSkin(int id, String triggerName) {
        this.id = id;
        this.triggerName = triggerName;
    }

    public int getId() { return id; }

    public static FamiliarSkin fromName(String name) {
        for (FamiliarSkin skin : values()) {
            if (skin.triggerName.equals(name)) {
                return skin;
            }
        }
        return DEFAULT;
    }

    public static FamiliarSkin fromId(int id) {
        for (FamiliarSkin skin : values()) {
            if (skin.id == id) return skin;
        }
        return DEFAULT;
    }
}