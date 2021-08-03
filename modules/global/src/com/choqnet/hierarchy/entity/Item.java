package com.choqnet.hierarchy.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "HIERARCHY_ITEM")
@Entity(name = "hierarchy_Item")
@NamePattern("%s %s|name,tag")
public class Item extends BaseUuidEntity {
    private static final long serialVersionUID = 7723342981545250547L;

    @Column(name = "SMART_ID")
    private String smartID;

    @Column(name = "YEAR_")
    private String year;

    @Column(name = "TAG", nullable = false)
    @NotNull
    private String tag;

    @Column(name = "TYPE_")
    private String type;

    @Column(name = "NAME")
    private String name;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @Column(name = "TSHIRT")
    private Integer tshirt = 0;

    @Column(name = "WORKLOAD")
    private Integer workload = 0;

    @Transient
    @MetaProperty(related = {"q1", "q2", "q3", "q4", "workload"})
    private Boolean dispatched;

    @Column(name = "Q1")
    private Integer q1 = 0;

    @Column(name = "Q2")
    private Integer q2 = 0;

    @Column(name = "Q3")
    private Integer q3 = 0;

    @Column(name = "Q4")
    private Integer q4 = 0;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "open", "clear"})
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Item parent;

    @Column(name = "EMPTY", length = 1)
    private String empty;

    public String getEmpty() {
        return "";
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Boolean getDispatched() {
        return (workload.equals(q1 + q2 + q3 + q4));
    }

    public TShirt getTshirt() {
        return tshirt == null ? null : TShirt.fromId(tshirt);
    }

    public void setTshirt(TShirt tshirt) {
        this.tshirt = tshirt == null ? null : tshirt.getId();
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSmartID() {
        return smartID;
    }

    public void setSmartID(String smartID) {
        this.smartID = smartID;
    }

    public void setQ3(Integer q3) {
        this.q3 = q3;
    }

    public Integer getQ3() {
        return q3;
    }

    public Integer getQ4() {
        return q4;
    }

    public void setQ4(Integer q4) {
        this.q4 = q4;
    }

    public Integer getQ2() {
        return q2;
    }

    public void setQ2(Integer q2) {
        this.q2 = q2;
    }

    public Integer getQ1() {
        return q1;
    }

    public void setQ1(Integer q1) {
        this.q1 = q1;
    }

    public Type getType() {
        return type == null ? null : Type.fromId(type);
    }

    public void setType(Type type) {
        this.type = type == null ? null : type.getId();
    }

    public Item getParent() {
        return parent;
    }

    public void setParent(Item parent) {
        this.parent = parent;
    }

    public Integer getWorkload() {
        return workload;
    }

    public void setWorkload(Integer workload) {
        this.workload = workload;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int giveQ(int i) {
        switch (i) {
            case 1:
                return q1;
            case 2:
                return q2;
            case 3:
                return q3;
            case 4:
                return q4;
            default:
                return 0;
        }
    }
}