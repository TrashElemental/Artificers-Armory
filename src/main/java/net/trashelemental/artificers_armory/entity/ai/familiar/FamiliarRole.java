package net.trashelemental.artificers_armory.entity.ai.familiar;

public enum FamiliarRole {
    NONE(0),
    PROTECTOR(1),
    HEALER(2),
    PRANKSTER(3),
    BRUISER(4);

    private final int id;

    FamiliarRole(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static FamiliarRole fromId(int id) {
        for (FamiliarRole role : values()) {
            if (role.id == id) return role;
        }
        return NONE;
    }
}
