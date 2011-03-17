package com.trailmagic.image;

public enum ImageGroupType {
    ROLL("roll", "Roll", "Rolls"), ALBUM("album", "Album", "Albums");
    private String typeName;
    private String displayName;
    private String pluralDisplayName;

    ImageGroupType(String typeName, String displayName, String pluralDisplayName) {
        this.typeName = typeName;
        this.displayName = displayName;
        this.pluralDisplayName = pluralDisplayName;
    }

    public String toString() {
        return this.typeName;
    }

    public String getDisplayString() {
        return displayName;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public String getPluralDisplayString() {
        return pluralDisplayName;
    }

    public static ImageGroupType fromString(String typeString) {
        for (ImageGroupType type : ImageGroupType.values()) {
            if (type.typeName.equalsIgnoreCase(typeString)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid type string: " + typeString);
    }
}
