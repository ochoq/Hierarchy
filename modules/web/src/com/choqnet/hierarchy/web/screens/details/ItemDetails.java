package com.choqnet.hierarchy.web.screens.details;

import com.choqnet.hierarchy.SynchroBookEvent;
import com.choqnet.hierarchy.entity.Item;
import com.choqnet.hierarchy.service.UtilityService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

@UiController("hierarchy_ItemDetails")
@UiDescriptor("item-details.xml")
@DialogMode(height = "500px", width = "700px")
public class ItemDetails extends Screen {
    private static final Logger log = LoggerFactory.getLogger(ItemDetails.class);

    @Inject
    private DataManager dm;
    @Inject
    private UtilityService us;
    private Item parent;
    @Inject
    private CollectionLoader<Item> itemsDl;
    @Inject
    private Events events;
    @Inject
    private Table<Item> itemsTable;

    @Subscribe
    public void onInit(InitEvent event) {
    }

    public void getParameter(Item parent) {
        this.parent = parent;
        itemsDl.setQuery("select e from hierarchy_Item e WHERE e.parent = :parent ORDER BY e.smartID");
        itemsDl.setParameter("parent", parent);
        itemsDl.setView("items");
        itemsDl.load();
    }


    @Subscribe(target = Target.DATA_CONTEXT)
    public void onChange(DataContext.ChangeEvent event) {
        // the event only gives access to the table data, not the Item itself
        // the first thing to do is to retrieve the Item
        List<Item> items = dm.load(Item.class)
                .query("select e from hierarchy_Item e WHERE e.smartID = :smartID")
                .parameter("smartID", ((Item) event.getEntity()).getSmartID())
                .view("items")
                .list();

        if (items.size()!=0) {
            // update of the Item with the data coming from the Table (via an event)
            Item item = items.get(0);
            item.setName(((Item) event.getEntity()).getName());
            item.setTag(((Item) event.getEntity()).getTag());
            item.setWorkload(((Item) event.getEntity()).getWorkload());
            item.setQ1(((Item) event.getEntity()).getQ1());
            item.setQ2(((Item) event.getEntity()).getQ2());
            item.setQ3(((Item) event.getEntity()).getQ3());
            item.setQ4(((Item) event.getEntity()).getQ4());
            dm.commit(item);
            // propagate the update of the parents
            while (item.getParent()!=null) {
                item = us.getItemBySmartID(item.getParent().getSmartID());
                updateItem(item);
            }
            itemsDl.load();
            // fires the event, for the connected clients to be refreshed
            SynchroBookEvent sbe = new SynchroBookEvent(this, (Item) event.getEntity());
            events.publish(sbe);
        }
        itemsDl.load();
        itemsTable.repaint();
    }

    private void updateItem(Item item) {
        List<Item> children = dm.load(Item.class)
                .query("select e from hierarchy_Item e WHERE e.parent = :parent")
                .parameter("parent", item)
                .view("items")
                .list();

        item.setWorkload(children.stream().map(Item::getWorkload).reduce(0, Integer::sum));
        dm.commit(item);

    }

    @Subscribe(id = "itemsDc", target = Target.DATA_CONTAINER)
    public void onItemsDcItemChange(InstanceContainer.ItemChangeEvent<Item> event) {
        log.info("really changed for " + event.getItem().getTag());

    }


    



}