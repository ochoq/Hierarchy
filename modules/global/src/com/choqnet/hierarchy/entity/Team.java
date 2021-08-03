package com.choqnet.hierarchy.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "HIERARCHY_TEAM")
@Entity(name = "hierarchy_Team")
@NamePattern("%s|fullName")
public class Team extends BaseUuidEntity {
    private static final long serialVersionUID = 1735757111164934281L;

    @Column(name = "PLATFORM")
    private String platform;

    @Column(name = "TRIBE")
    private String tribe;

    @Transient
    @MetaProperty(related = {"name", "platform", "tribe"})
    private String fullName;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullName() {
        return platform + "-" + tribe + "-" + name;
    }

    public String getTribe() {
        return tribe;
    }

    public void setTribe(String tribe) {
        this.tribe = tribe;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}