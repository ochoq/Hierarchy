package com.choqnet.hierarchy;

import com.choqnet.hierarchy.entity.Item;
import com.haulmont.addon.globalevents.GlobalApplicationEvent;
import com.haulmont.addon.globalevents.GlobalUiEvent;

public class SynchroBookEvent extends GlobalApplicationEvent implements GlobalUiEvent {

   private Item item;

    public SynchroBookEvent(Object source, Item item) {
        super(source);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
