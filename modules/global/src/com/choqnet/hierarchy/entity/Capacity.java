package com.choqnet.hierarchy.entity;

import com.haulmont.cuba.core.entity.BaseUuidEntity;

import javax.persistence.*;

@Table(name = "HIERARCHY_CAPACITY")
@Entity(name = "hierarchy_Capacity")
public class Capacity extends BaseUuidEntity {
    private static final long serialVersionUID = -6989385330826857017L;

    @Column(name = "YEAR_")
    private String year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @Column(name = "Q1")
    private Integer q1 = 0;

    @Column(name = "Q2")
    private Integer q2 = 0;

    @Column(name = "Q3")
    private Integer q3 = 0;

    @Column(name = "Q4")
    private Integer q4 = 0;

    public void setQ2(Integer q2) {
        this.q2 = q2;
    }

    public Integer getQ2() {
        return q2;
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

    public Integer getQ1() {
        return q1;
    }

    public void setQ1(Integer q1) {
        this.q1 = q1;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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