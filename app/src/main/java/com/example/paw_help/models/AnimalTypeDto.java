package com.example.paw_help.models;

public class AnimalTypeDto {
    private int typeId;
    private String typeName;
    private String typeEmoji;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeEmoji() {
        return typeEmoji;
    }

    public void setTypeEmoji(String typeEmoji) {
        this.typeEmoji = typeEmoji;
    }
}

