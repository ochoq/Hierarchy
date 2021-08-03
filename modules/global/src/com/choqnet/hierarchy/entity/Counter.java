package com.choqnet.hierarchy.entity;

import com.haulmont.cuba.core.entity.BaseUuidEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "HIERARCHY_COUNTER")
@Entity(name = "hierarchy_Counter")
public class Counter extends BaseUuidEntity {
    private static final long serialVersionUID = -913022753491358726L;

    @Column(name = "ITEM_COUNTER")
    private Integer itemCounter = 0;

    public Integer getItemCounter() {
        return itemCounter;
    }

    public void setItemCounter(Integer itemCounter) {
        this.itemCounter = itemCounter;
    }
}