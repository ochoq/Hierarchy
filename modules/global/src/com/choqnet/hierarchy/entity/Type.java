package com.choqnet.hierarchy.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum Type implements EnumClass<String> {

    ROOT("ROOT"),
    BUDGET("BUD"),
    ROADMAP("RDP"),
    DEMAND("DMD");

    private String id;

    Type(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static Type fromId(String id) {
        for (Type at : Type.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}