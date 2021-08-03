package com.choqnet.hierarchy.service;

import com.choqnet.hierarchy.entity.Item;

public interface UtilityService {
    String NAME = "hierarchy_UtilityService";
    public String getNewCounter();
    public Item getItemBySmartID(String smartID);
}