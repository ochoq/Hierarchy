package com.choqnet.hierarchy.service;

import com.choqnet.hierarchy.entity.Counter;
import com.choqnet.hierarchy.entity.Item;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(UtilityService.NAME)
public class UtilityServiceBean implements UtilityService {
    @Inject
    private DataManager dm;

    @Override
    public String getNewCounter() {
        List<Counter> counters = dm.load(Counter.class)
                .query("select e from hierarchy_Counter e")
                .list();
        int counter = counters.get(0).getItemCounter() + 1;
        counters.get(0).setItemCounter(counter);
        dm.commit(counters.get(0));
        return "ITEM" + counter;
    }

    @Override
    public Item getItemBySmartID(String smartID) {
        List<Item> items = dm.load(Item.class)
                .query("select e from hierarchy_Item e WHERE e.smartID= '" + smartID +"'")
                .view("items")
                .list();
        if (items.size()==0) {
            return null;
        } else {
            return items.get(0);
        }
    }
}