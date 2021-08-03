package com.choqnet.hierarchy.web.screens.details;

import com.choqnet.hierarchy.SynchroBookEvent;
import com.choqnet.hierarchy.entity.Item;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.List;

@UiController("hierarchy_Reconnect")
@UiDescriptor("reconnect.xml")
@LoadDataBeforeShow
public class Reconnect extends Screen {
    @Inject
    private Label<String> lblHeader;
    @Inject
    private Label<String> lblSubHeader;
    @Inject
    private Label<String> lblCurrentParent;
    @Inject
    private LookupField<Item> lkpItem;
    @Inject
    private DataManager dm;

    private Item item;
    @Inject
    private Events events;

    @Subscribe
    public void onInit(InitEvent event) {


    }



    public void setItem(Item item, String year) {
        this.item = item;
        lblHeader.setValue("Choose a new parent for " + item.getTag());
        lblSubHeader.setValue(item.getName());
        // set up the lookupList
        String query="";
        switch (item.getType()) {
            case DEMAND:
                query = "e.type = 'RDP'";
                break;
            case ROADMAP:
                query = "e.type = 'BUD'";
                break;
        }
        List<Item> parents = dm.load(Item.class)
                .query("select e from hierarchy_Item e where " + query)
                .list();
        lkpItem.setOptionsList(parents);
        List<Item> mes = dm.load(Item.class)
                .query("select e from hierarchy_Item e where e.smartID='" + item.getSmartID() + "' and e.year ='" + year +"'")
                .view("items")
                .list();
        if (mes.size()!=0) {
            lkpItem.setValue(mes.get(0).getParent());
            Item parent = mes.get(0).getParent();
            String parName = mes.get(0).getParent().getName();
            lblCurrentParent.setValue("Current parent is " + mes.get(0).getParent().getTag() + " - " + mes.get(0).getParent().getName());
        }
    }

    @Subscribe("btnConnect")
    public void onBtnConnectClick(Button.ClickEvent event) {
        if (lkpItem.getValue()!=null && !lkpItem.getValue().equals(item.getParent())) {
            item.setParent(lkpItem.getValue());
            dm.commit(item);
            SynchroBookEvent sbe = new SynchroBookEvent(this, item);
            events.publish(sbe);
        }
        this.closeWithDefaultAction();
    }

    @Subscribe("btnEscape")
    public void onBtnEscapeClick(Button.ClickEvent event) {
        this.closeWithDefaultAction();
    }




}