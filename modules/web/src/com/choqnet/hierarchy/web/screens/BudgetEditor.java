package com.choqnet.hierarchy.web.screens;

import com.choqnet.hierarchy.SynchroBookEvent;
import com.choqnet.hierarchy.entity.Item;
import com.choqnet.hierarchy.entity.TShirt;
import com.choqnet.hierarchy.entity.Team;
import com.choqnet.hierarchy.entity.Type;
import com.choqnet.hierarchy.service.UtilityService;
import com.choqnet.hierarchy.web.screens.details.ItemDetails;
import com.choqnet.hierarchy.web.screens.details.Reconnect;
import com.haulmont.charts.gui.data.ListDataProvider;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.web.gui.components.renderers.WebComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@UiController("hierarchy_BudgetEditor")
@UiDescriptor("budget_editor.xml")
@LoadDataBeforeShow
public class BudgetEditor extends Screen {
    private static final Logger log = LoggerFactory.getLogger(BudgetEditor.class);
    @Inject
    private TreeDataGrid<Item> itemsTable;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private DataManager dm;
    @Inject
    private CollectionLoader<Item> itemsDl;
    @Inject
    private Events events;
    @Inject
    private Dialogs dialogs;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private UtilityService us;
    @Inject
    private LookupField<String> lkpYear;

    private String year;
    private TShirt currentTShirt;
    @Inject
    private VBoxLayout table;
    @Inject
    private Notifications notifications;
    @Inject
    private CollectionContainer<Item> itemsDc;


    @Subscribe("lkpYear")
    public void onLkpYearValueChange(HasValue.ValueChangeEvent<String> event) {
        year = event.getValue();
        itemsDl.setQuery("select e from hierarchy_Item e WHERE e.year ='" + lkpYear.getValue() + "' order by e.name ASC");
        itemsDl.load();
    }

    @Subscribe
    public void onInit(InitEvent event) {
        // setup the years list
        List<String> list = Arrays.asList("2021","2022");
        lkpYear.setOptionsList(list);
        lkpYear.setValue("2022");
        //itemsTable.setStyleName("compact");

    }

    @Subscribe
    public void onAfterShow1(AfterShowEvent event) {
        // build the UI
        addTagColumn(0);
        addNameColumn(1);
        addTeamColumn(2);
        addTshirtColumn(3);
        addWorkLoadColumn(4);
        addWorkLoadColumn(4);
        addDispatcherColumnb(5);
        addQ1Column(6);
        addQ2Column(7);
        addQ3Column(8);
        addQ4Column(9);
        addCommandsColumn(10);
        //addEndColumn(11);
        itemsTable.setWidth("100%");
        itemsTable.repaint();

    }
    private void addTagColumn(int position) {
        DataGrid.Column<Item> columnIcon = itemsTable.addGeneratedColumn("Hierarchy",
                new DataGrid.ColumnGenerator<Item, Component>() {
                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        String iconName;
                        switch (event.getItem().getType()) {
                            case ROADMAP:
                                iconName = "ROAD";
                                break;
                            case DEMAND:
                                iconName = "CALENDAR";
                                break;
                            case BUDGET:
                                iconName = "BAR_CHART";
                                break;
                            case ROOT:
                                iconName = "BOOK";
                                break;
                            default:
                                iconName = "REVELRT";
                        }
                        if (itemsTable.getSelected().contains(event.getItem())) {
                            // edit mode
                            TextField<String> label = uiComponents.create(TextField.NAME);
                            label.setWidth("100px");
                            label.setStyleName("roundFrame");
                            label.setValue(event.getItem().getTag());
                            label.setIconFromSet(CubaIcon.valueOf(iconName));
                            label.setEditable(true);
                            label.addValueChangeListener(e -> {
                                event.getItem().setTag(label.getValue());
                                dm.commit(event.getItem());
                                SynchroBookEvent sbe = new SynchroBookEvent(this, event.getItem());
                                events.publish(sbe);
                            });

                            return label;

                        } else  {
                            // read mode
                            Label<String> label =uiComponents.create(Label.NAME);
                            label.setValue(event.getItem().getTag());

                            label.setIconFromSet(CubaIcon.valueOf(iconName));
                            return label;
                        }
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                },position);
        columnIcon.setRenderer(new WebComponentRenderer<Item>());
        columnIcon.setWidth(180);
    }
    private void addNameColumn(int position) {
        DataGrid.Column<Item> columnIcon = itemsTable.addGeneratedColumn("Name",
                new DataGrid.ColumnGenerator<Item, Component>() {
                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        if (itemsTable.getSelected().contains(event.getItem())) {
                            // edit mode
                            TextField<String> label = uiComponents.create(TextField.NAME);
                            label.setStyleName("roundFrame");
                            label.setValue(event.getItem().getName());
                            label.setEditable(true);
                            label.setWidth("100%");
                            label.addValueChangeListener(e -> {
                                event.getItem().setName(label.getValue());
                                dm.commit(event.getItem());
                                SynchroBookEvent sbe = new SynchroBookEvent(this, event.getItem());
                                events.publish(sbe);
                            });
                            return label;
                        } else  {
                            // read mode
                            Label<String> label =uiComponents.create(Label.NAME);
                            label.setValue(event.getItem().getName());
                            return label;
                        }
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                },position);
        columnIcon.setRenderer(new WebComponentRenderer<Item>());
        columnIcon.setWidth(700);
    }
    private void addTeamColumn(int position) {
        // sets the renderer for the column Team
        List<Team> teams = dm.load(Team.class)
                .query("select e from hierarchy_Team e order by e.platform asc, e.tribe asc, e.name asc")
                .list();
        DataGrid.Column columnTeam = itemsTable.addGeneratedColumn("Team",
                new DataGrid.ColumnGenerator<Item, Component>() {

                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        // enable team choice only for demands
                        if (Type.DEMAND.equals(event.getItem().getType())) {
                            if (itemsTable.getSelected().contains(event.getItem())) {
                                LookupField<Team> lstTeam = uiComponents.create(LookupField.NAME);
                                lstTeam.setWidth("100%");
                                lstTeam.setStyleName("roundFrame");
                                lstTeam.setOptionsList(teams);
                                lstTeam.setValue(event.getItem().getTeam());
                                lstTeam.addValueChangeListener(e -> {
                                    event.getItem().setTeam(lstTeam.getValue());
                                    dm.commit(event.getItem());
                                    SynchroBookEvent sbe = new SynchroBookEvent(this, event.getItem());
                                    events.publish(sbe);
                                });
                                lstTeam.setFilterMode(LookupField.FilterMode.CONTAINS);
                                return lstTeam;
                            } else  {
                                Label txtTeam = uiComponents.create(Label.NAME);
                                txtTeam.setValue(event.getItem().getTeam()==null ? "" : event.getItem().getTeam().getFullName());
                                return txtTeam;
                            }
                        } else {
                            return uiComponents.create(Label.NAME);
                        }
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                }, position);
        columnTeam.setRenderer(new WebComponentRenderer<Item>());
        columnTeam.setWidth(450);
    }
    private void addTshirtColumn(int position) {
        // sets the renderer for the column Team
        List<TShirt> tShirts = Arrays.asList(TShirt.values());
        DataGrid.Column columnTShirt = itemsTable.addGeneratedColumn("T-Shirt",
                new DataGrid.ColumnGenerator<Item, Component>() {

                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        // enable team choice only for demands
                        if (Type.DEMAND.equals(event.getItem().getType())) {
                            if (itemsTable.getSelected().contains(event.getItem())) {
                                LookupField<TShirt> lstTshirt = uiComponents.create(LookupField.NAME);
                                lstTshirt.setWidth("100%");
                                lstTshirt.setStyleName("roundFrame");
                                lstTshirt.setOptionsList(tShirts);
                                lstTshirt.setNullOptionVisible(false);
                                lstTshirt.setValue(event.getItem().getTshirt());
                                lstTshirt.addValueChangeListener(e -> {
                                    event.getItem().setTshirt(lstTshirt.getValue());
                                    if (currentTShirt==null || (!currentTShirt.equals(event.getItem().getTshirt().name()) && !TShirt.FREE.equals(event.getItem().getTshirt())))
                                    {
                                        event.getItem().setWorkload(event.getItem().getTshirt().getId());
                                        Item item = dm.commit(event.getItem());
                                        // update parent
                                        Item parent = item.getParent();
                                        while (parent!=null) {
                                            parent = updateParent(parent);
                                        }
                                    }
                                    dm.commit(event.getItem());
                                    SynchroBookEvent sbe = new SynchroBookEvent(this, event.getItem());
                                    events.publish(sbe);
                                });

                                return lstTshirt;
                            } else  {
                                Label txtTshirt = uiComponents.create(Label.NAME);
                                txtTshirt.setValue(event.getItem().getTshirt()==null ? "" : event.getItem().getTshirt().name());
                                return txtTshirt;
                            }
                        } else {
                            return uiComponents.create(Label.NAME);
                        }
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                },position);
        columnTShirt.setRenderer(new WebComponentRenderer<Item>());
        columnTShirt.setWidth(75);

    }
    private void addWorkLoadColumn(int position) {
        // sets the renderer for the column Team
        List<TShirt> tShirts = Arrays.asList(TShirt.values());
        DataGrid.Column columnWorkload = itemsTable.addGeneratedColumn("Workload",
                new DataGrid.ColumnGenerator<Item, Component>() {

                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        // enable team choice only for demands
                        if (Type.DEMAND.equals(event.getItem().getType()) &&
                                itemsTable.getSelected().contains(event.getItem()) &&
                                (event.getItem().getTshirt()==null || TShirt.FREE.equals(event.getItem().getTshirt()))
                        ) {
                            // selected demand, with no T-Shirt constraint mode edit
                            TextField<Integer> workload = uiComponents.create(TextField.NAME);
                            workload.setStyleName("roundFrame");
                            workload.setWidth("100%");
                            workload.setDatatype(Datatypes.get(Integer.class));
                            workload.setValue(event.getItem().getWorkload());
                            // workload.setDatatype(Datatype<Integer>);
                            workload.addValueChangeListener(e -> {
                                event.getItem().setWorkload(workload.getValue());
                                Item item = dm.commit(event.getItem());
                                // update parent
                                Item parent = item.getParent();
                                while (parent!=null) {
                                    parent = updateParent(parent);
                                }
                                SynchroBookEvent sbe = new SynchroBookEvent(this, event.getItem());
                                events.publish(sbe);
                            });
                            return workload;
                        } else {
                            // mode read
                            Label<Integer> workload = uiComponents.create(Label.NAME);
                            workload.setValue(event.getItem().getWorkload());
                            return workload;
                        }
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                },position);
        columnWorkload.setRenderer(new WebComponentRenderer<Item>());
        columnWorkload.setWidth(100);

    }
    private void addDispatcherColumnb(int position) {
        DataGrid.Column<Item> columnDispatch = itemsTable.addGeneratedColumn("Check",
                new DataGrid.ColumnGenerator<Item, Label>() {
                    @Override
                    public Label getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        Label<String> status = uiComponents.create(Label.NAME);
                        status.setWidth("100%");
                        if (Type.DEMAND.equals(event.getItem().getType())) {
                            if (event.getItem().getDispatched()) {
                                // status.setValue("correct");
                                status.setDescription("Workload well dispatched on quarters.");
                                status.setStyleName("good");
                                // status.setIconFromSet(CubaIcon.valueOf("CHECK_CIRCLE"));
                            } else {
                                // status.setValue("error");
                                status.setStyleName("bad");
                                status.setIconFromSet(CubaIcon.valueOf("EXCLAMATION_CIRCLE"));
                                status.setDescription("<!> The sum of the quarters doesn't match with the total workload");
                            }
                        }


                        return status;
                    }

                    @Override
                    public Class<Label> getType() {
                        return Label.class;
                    }
                },position);
        columnDispatch.setRenderer(new WebComponentRenderer<Item>());
        columnDispatch.setWidth(40);
    }
    private void addQ1Column(int position) {
        // sets the renderer for the column Q1
        DataGrid.Column columnWorkload = itemsTable.addGeneratedColumn("Q1",
                new DataGrid.ColumnGenerator<Item, Component>() {
                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        // enable team choice only for demands
                        if (Type.DEMAND.equals(event.getItem().getType()) &&
                                itemsTable.getSelected().contains(event.getItem())) {
                            // selected demand, with no T-Shirt constraint mode edit
                            TextField<Integer> workload = uiComponents.create(TextField.NAME);
                            workload.setStyleName("roundFrame");
                            workload.setWidth("100%");
                            workload.setDatatype(Datatypes.get(Integer.class));
                            workload.setValue(event.getItem().getQ1());
                            // workload.setDatatype(Datatype<Integer>);
                            workload.addValueChangeListener(e -> {
                                event.getItem().setQ1(workload.getValue());
                                Item item = dm.commit(event.getItem());
                                // update parent
                                Item parent = item.getParent();
                                while (parent!=null) {
                                    parent = updateParent(parent);
                                }
                                SynchroBookEvent sbe = new SynchroBookEvent(this, event.getItem());
                                events.publish(sbe);
                            });
                            return workload;
                        } else {
                            // mode read
                            Label<Integer> workload = uiComponents.create(Label.NAME);
                            workload.setValue(event.getItem().getQ1());
                            return workload;
                        }
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                },position);
        columnWorkload.setRenderer(new WebComponentRenderer<Item>());
        columnWorkload.setWidth(75);

    }
    private void addQ2Column(int position) {
        // sets the renderer for the column Q2
        DataGrid.Column columnWorkload = itemsTable.addGeneratedColumn("Q2",
                new DataGrid.ColumnGenerator<Item, Component>() {
                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        // enable team choice only for demands
                        if (Type.DEMAND.equals(event.getItem().getType()) &&
                                itemsTable.getSelected().contains(event.getItem())) {
                            // selected demand, with no T-Shirt constraint mode edit
                            TextField<Integer> workload = uiComponents.create(TextField.NAME);
                            workload.setStyleName("roundFrame");
                            workload.setWidth("100%");
                            workload.setDatatype(Datatypes.get(Integer.class));
                            workload.setValue(event.getItem().getQ2());
                            // workload.setDatatype(Datatype<Integer>);
                            workload.addValueChangeListener(e -> {
                                event.getItem().setQ2(workload.getValue());
                                Item item = dm.commit(event.getItem());
                                // update parent
                                Item parent = item.getParent();
                                while (parent!=null) {
                                    parent = updateParent(parent);
                                }
                                SynchroBookEvent sbe = new SynchroBookEvent(this, event.getItem());
                                events.publish(sbe);
                            });
                            return workload;
                        } else {
                            // mode read
                            Label<Integer> workload = uiComponents.create(Label.NAME);
                            workload.setValue(event.getItem().getQ2());
                            return workload;
                        }
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                },position);
        columnWorkload.setRenderer(new WebComponentRenderer<Item>());
        columnWorkload.setWidth(75);

    }
    private void addQ3Column(int position) {
        // sets the renderer for the column Q3
        DataGrid.Column columnWorkload = itemsTable.addGeneratedColumn("Q3",
                new DataGrid.ColumnGenerator<Item, Component>() {
                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        // enable team choice only for demands
                        if (Type.DEMAND.equals(event.getItem().getType()) &&
                                itemsTable.getSelected().contains(event.getItem())) {
                            // selected demand, with no T-Shirt constraint mode edit
                            TextField<Integer> workload = uiComponents.create(TextField.NAME);
                            workload.setStyleName("roundFrame");
                            workload.setWidth("100%");
                            workload.setDatatype(Datatypes.get(Integer.class));
                            workload.setValue(event.getItem().getQ3());
                            // workload.setDatatype(Datatype<Integer>);
                            workload.addValueChangeListener(e -> {
                                event.getItem().setQ3(workload.getValue());
                                Item item = dm.commit(event.getItem());
                                // update parent
                                Item parent = item.getParent();
                                while (parent!=null) {
                                    parent = updateParent(parent);
                                }
                                SynchroBookEvent sbe = new SynchroBookEvent(this, event.getItem());
                                events.publish(sbe);
                            });
                            return workload;
                        } else {
                            // mode read
                            Label<Integer> workload = uiComponents.create(Label.NAME);
                            workload.setValue(event.getItem().getQ3());
                            return workload;
                        }
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                },position);
        columnWorkload.setRenderer(new WebComponentRenderer<Item>());
        columnWorkload.setWidth(75);

    }
    private void addQ4Column(int position) {
        // sets the renderer for the column Q4
        DataGrid.Column columnWorkload = itemsTable.addGeneratedColumn("Q4",
                new DataGrid.ColumnGenerator<Item, Component>() {
                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        // enable team choice only for demands
                        if (Type.DEMAND.equals(event.getItem().getType()) &&
                                itemsTable.getSelected().contains(event.getItem())) {
                            // selected demand, with no T-Shirt constraint mode edit
                            TextField<Integer> workload = uiComponents.create(TextField.NAME);
                            workload.setStyleName("roundFrame");
                            workload.setWidth("100%");
                            workload.setDatatype(Datatypes.get(Integer.class));
                            workload.setValue(event.getItem().getQ4());
                            // workload.setDatatype(Datatype<Integer>);
                            workload.addValueChangeListener(e -> {
                                event.getItem().setQ4(workload.getValue());
                                Item item = dm.commit(event.getItem());
                                // update parent
                                Item parent = item.getParent();
                                while (parent!=null) {
                                    parent = updateParent(parent);
                                }
                                SynchroBookEvent sbe = new SynchroBookEvent(this, event.getItem());
                                events.publish(sbe);
                            });
                            return workload;
                        } else {
                            // mode read
                            Label<Integer> workload = uiComponents.create(Label.NAME);
                            workload.setValue(event.getItem().getQ4());
                            return workload;
                        }
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                },position);
        columnWorkload.setRenderer(new WebComponentRenderer<Item>());
        columnWorkload.setWidth(75);

    }
    private void addCommandsColumn(int position) {
        DataGrid.Column<Item> columnCommands = itemsTable.addGeneratedColumn("Commands",
                new DataGrid.ColumnGenerator<Item, HBoxLayout>() {
                    @Override
                    public HBoxLayout getValue(DataGrid.ColumnGeneratorEvent<Item> event) {
                        HBoxLayout hBoxLayout = uiComponents.create(HBoxLayout.NAME);
                        hBoxLayout.setSpacing(true);
                        hBoxLayout.setWidth("100%");

                        // fill the box
                        String size = "30px";
                        HBoxLayout empty = uiComponents.create(HBoxLayout.NAME);
                        empty.setWidth(size);

                        // ADD button
                        VBoxLayout addBox = uiComponents.create(VBoxLayout.NAME);
                        addBox.setWidth(size);
                        LinkButton addBtn = uiComponents.create(LinkButton.NAME);
                        addBtn.setIconFromSet(CubaIcon.valueOf("PLUS"));
                        addBtn.addClickListener(click -> doAddClick(click, event.getItem()));
                        if (Type.ROOT.equals(event.getItem().getType()) || Type.BUDGET.equals(event.getItem().getType()) || Type.ROADMAP.equals(event.getItem().getType())) {
                            addBox.add(addBtn);
                        }
                        hBoxLayout.add(addBox);

                        // REMOVE button
                        VBoxLayout remBox = uiComponents.create(VBoxLayout.NAME);
                        remBox.setWidth(size);
                        LinkButton remBtn = uiComponents.create(LinkButton.NAME);
                        remBtn.setIconFromSet(CubaIcon.valueOf("MINUS"));
                        remBtn.addClickListener(click -> doRemoveClick(click, event.getItem()));
                        if (Type.BUDGET.equals(event.getItem().getType()) || Type.ROADMAP.equals(event.getItem().getType()) || Type.DEMAND.equals(event.getItem().getType())) {
                            remBox.add(remBtn);
                        }
                        hBoxLayout.add(remBox);

                        // MOVE button
                        VBoxLayout movBox = uiComponents.create(VBoxLayout.NAME);
                        movBox.setWidth(size);
                        LinkButton movBtn = uiComponents.create(LinkButton.NAME);
                        movBtn.setIconFromSet(CubaIcon.valueOf("CODE_FORK"));
                        movBtn.addClickListener(click -> doMoveClick(click, event.getItem(), lkpYear.getValue()));
                        if (Type.ROADMAP.equals(event.getItem().getType()) || Type.DEMAND.equals(event.getItem().getType())) {
                            movBox.add(movBtn);
                        }
                        hBoxLayout.add(movBox);

                        // EDIT button
                        VBoxLayout edtBox = uiComponents.create(VBoxLayout.NAME);
                        edtBox.setWidth(size);
                        LinkButton edtBtn = uiComponents.create(LinkButton.NAME);
                        edtBtn.setIconFromSet(CubaIcon.valueOf("TABLE"));
                        movBtn.setDescription("Move the selected item to another parent");
                        edtBtn.addClickListener(click -> doEditClick(click, event.getItem()));
                        if (Type.ROOT.equals(event.getItem().getType()) || Type.ROADMAP.equals(event.getItem().getType()) ) {
                            edtBox.add(edtBtn);
                        }
                        hBoxLayout.add(edtBox);

                        // DISPATCH button
                        VBoxLayout dspBox = uiComponents.create(VBoxLayout.NAME);
                        dspBox.setWidth(size);
                        LinkButton dspBtn = uiComponents.create(LinkButton.NAME);
                        dspBtn.setIconFromSet(CubaIcon.valueOf("ARROWS_H"));
                        dspBtn.setDescription("Spread the workload over the 4 quarters");
                        dspBtn.addClickListener(click -> doDispatchClick(click, event.getItem()));
                        if (Type.DEMAND.equals(event.getItem().getType())) {
                            dspBox.add(dspBtn);
                        }
                        hBoxLayout.add(dspBox);

                        Label<String> spacer = uiComponents.create(Label.NAME);
                        hBoxLayout.add(spacer);

                        hBoxLayout.expand(spacer);
                        return hBoxLayout;
                    }

                    @Override
                    public Class<HBoxLayout> getType() {
                        return HBoxLayout.class;
                    }
                },position);
        columnCommands.setRenderer(new WebComponentRenderer<Item>());
        columnCommands.setWidth(230);
    }

    @Install(to = "itemsTable", subject = "rowStyleProvider")
    protected String itemsTableRowStyleProvider(Item item) {
        switch (item.getType()) {
            case ROADMAP:
                return "roadmap";
            case BUDGET:
                return "iprb";
            case ROOT:
                return "root";
            default:
                return "";
        }
    }


    // functions for editing the items list
    private void doAddClick(Button.ClickEvent clickEvent, Item item) {
        Item newItem = dm.create(Item.class);

        newItem.setSmartID(us.getNewCounter());
        switch (item.getType()) {
            case ROOT:
                newItem.setType(Type.BUDGET);
                newItem.setTag("IPRB");
                break;
            case BUDGET:
                newItem.setType(Type.ROADMAP);
                newItem.setTag("roadmap");
                break;
            case ROADMAP:
                newItem.setType(Type.DEMAND);
                newItem.setTag("demand");
                newItem.setTshirt(TShirt.FREE);
                break;
        }
        newItem.setParent(item);
        newItem.setYear(lkpYear.getValue());
        dm.commit(newItem);
        // fires the synchronization event
        SynchroBookEvent sbe = new SynchroBookEvent(this, item);
        events.publish(sbe);
        log.info("doAddClick event sent");
    }
    private void doRemoveClick(Button.ClickEvent click, Item item) {
        // build confirmation message
        List<Item> items = dm.load(Item.class)
                .query("select e from hierarchy_Item e WHERE e.parent = :parent")
                .parameter("parent", item)
                .list();
        if (items.size()!= 0) {
            // get confirmation message
            dialogs.createOptionDialog()
                    .withCaption("Confirm operation")
                    .withMessage("You're about to delete a budget item with " + items.size() + " direct children, that will be deleted as well. Are you sure to proceed?")
                    .withType(Dialogs.MessageType.CONFIRMATION)
                    .withActions(
                            new BaseAction("custom")
                            .withCaption("I understand and agree")
                            .withHandler(e -> doDelete(item)),
                            new DialogAction((DialogAction.Type.CANCEL))

                    ).show();

        } else {
            doDelete(item);
        }
    }
    private void doDelete(Item item ) {
        dm.remove(item);
        SynchroBookEvent sbe = new SynchroBookEvent(this, item.getParent());
        events.publish(sbe);
    }
    private void doEditClick(Button.ClickEvent click, Item item) {
        ItemDetails details = screenBuilders.screen(this)
                .withScreenClass(ItemDetails.class)
                .withOpenMode(OpenMode.DIALOG)
                .build();
        details.getParameter(item);
        details.show();

    }
    private void doDispatchClick(Button.ClickEvent click, Item item) {
        int quarter = (int) (item.getWorkload() / 4);
        item.setQ1(quarter);
        item.setQ2(quarter);
        item.setQ3(quarter);
        item.setQ4(item.getWorkload() - 3*quarter);
        dm.commit(item);
        SynchroBookEvent sbe = new SynchroBookEvent(this, item);
        events.publish(sbe);
    }
    private void doMoveClick(Button.ClickEvent click, Item item, String year) {
        Reconnect reconnect = screenBuilders.screen(this)
                .withScreenClass(Reconnect.class)
                .withOpenMode(OpenMode.DIALOG)
                .build();
        reconnect.setItem(item, year);
        reconnect.show();
    }

    @EventListener
    private void getUIMessages(SynchroBookEvent event) {
        itemsDl.load();
        itemsTable.expand(event.getItem());
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        onBtnIPRBClick();
        //itemsTable.expandAll();
    }


    @Subscribe("itemsTable")
    public void onItemsTableItemClick(DataGrid.ItemClickEvent<Item> event) {
        currentTShirt = event.getItem().getTshirt();

    }

    @Subscribe("itemsTable")
    public void onItemsTableEditorOpen(DataGrid.EditorOpenEvent<Item> event) {
        //currentTShirt = event.getItem().getTshirt();
    }

 // if (currentTShirt!=null && !currentTShirt.equals(item.getTshirt().name()) && !TShirt.FREE.equals(item.getTshirt()))

    private Item updateParent(Item parent) {
        List<Item> children = dm.load(Item.class)
                .query("select e from hierarchy_Item e WHERE e.parent = :parent")
                .parameter("parent", parent)
                .view("items")
                .list();

        parent.setWorkload(children.stream().map(Item::getWorkload).reduce(0, Integer::sum));
        parent.setQ1(children.stream().map(Item::getQ1).reduce(0, Integer::sum));
        parent.setQ2(children.stream().map(Item::getQ2).reduce(0, Integer::sum));
        parent.setQ3(children.stream().map(Item::getQ3).reduce(0, Integer::sum));
        parent.setQ4(children.stream().map(Item::getQ4).reduce(0, Integer::sum));
        dm.commit(parent);
        Optional<Item> copyParent = dm.load(Item.class)
                .query("select e from hierarchy_Item e where e = :parent")
                .parameter("parent", parent)
                .view("items")
                .list()
                .stream()
                .findFirst();
        return copyParent.get()==null ? null : copyParent.get().getParent();

    }

    public void onTempBtnClick() {
        Item root;
        List<Team> teams = dm.load(Team.class)
                .query("select e from hierarchy_Team e")
                .list();
        List<Item> roots = dm.load(Item.class)
                .query("select e from hierarchy_Item e where e.name = 'Budget 2022'")
                .list();
        if (roots.size()!=0) {
            root = roots.get(0);
            // creations
            for (int i = 0; i<10;i++) {
                Item iprb = dm.create(Item.class);
                iprb.setType(Type.BUDGET);
                iprb.setParent(root);
                iprb.setTag("IPRB " + i);
                iprb.setYear("2022");
                iprb.setName("Auto generated IPRB #" + i);
                iprb.setSmartID(us.getNewCounter());
                dm.commit(iprb);
                // creates the children
                for (int j = 0; j<10; j++) {
                    Item roadmap = dm.create(Item.class);
                    roadmap.setParent(iprb);
                    roadmap.setType(Type.ROADMAP);
                    roadmap.setTag("RDMP " + i + "-" + j);
                    roadmap.setName("-> Auto generated IPRB #"  + i + "-" + j);
                    roadmap.setYear("2022");
                    roadmap.setSmartID(us.getNewCounter());
                    dm.commit(roadmap);
                    // creates the children
                    for (int k=0; k<10; k++) {

                        Item demand  = dm.create(Item.class);
                        demand.setType(Type.DEMAND);
                        demand.setParent(roadmap);
                        demand.setTag("DMD " + i + "-" + j + ":" + k);
                        demand.setName("  +--> Auto generated demand #" + i + "-" + j + ":" + k);
                        demand.setSmartID(us.getNewCounter());
                        demand.setYear("2022");
                        try {
                            int ts = (int) (Math.random() * 7);
                            demand.setTshirt(TShirt.values()[ts]);
                            int workload =  TShirt.values()[ts].getId();

                            int quarter = (int) (workload / 4);
                            demand.setQ1(quarter);
                            demand.setQ2(quarter);
                            demand.setQ3(quarter);
                            demand.setQ4(workload - 3*quarter);
                            demand.setWorkload(workload);
                            // doDispatchClick(null, demand);
                            int team =(int) (Math.random() * teams.size());
                            demand.setTeam(teams.get(team));
                        } catch (Exception e) {
                            demand.setTshirt(TShirt.FREE);
                            demand.setTeam(teams.get(0));
                            log.info("Une erreur s'est produite " + e.getMessage());
                        }
                        dm.commit(demand);
                    }

                }

                List<Item> iList = dm.load(Item.class)
                        .query("select e from hierarchy_Item e where e.type <> :type" )
                        .parameter("type", Type.DEMAND)
                        .list();
                for (Item item:iList) {
                    dm.commit(updateTotals(item));
                }
            }


        } else {
            notifications.create()
                    .withCaption("No root found")
                    .withType(Notifications.NotificationType.ERROR)
                    .show();
        }

    }

    private Item updateTotals(Item item) {
        List<Item> items = dm.load(Item.class)
                .query("select e from hierarchy_Item e where e.parent = :parent")
                .parameter("parent", item)
                .list();
        item.setWorkload(items.stream().map(o -> o.getWorkload()).reduce(0, Integer::sum));
        item.setQ1(items.stream().map(o -> o.getQ1()).reduce(0, Integer::sum));
        item.setQ2(items.stream().map(o -> o.getQ2()).reduce(0, Integer::sum));
        item.setQ3(items.stream().map(o -> o.getQ3()).reduce(0, Integer::sum));
        item.setQ4(items.stream().map(o -> o.getQ4()).reduce(0, Integer::sum));
        return item;
    }

    public void onBtnCloseClick() {
        itemsTable.collapseAll();
    }

    public void onBtnIPRBClick() {
        List<Item> items = dm.load(Item.class)
                .query("select e from hierarchy_Item e WHERE e.type = :type and e.year = :year")
                .parameter("type", Type.ROOT)
                .parameter("year", lkpYear.getValue())
                .list();

        itemsTable.collapseAll();
        itemsTable.expandRecursively(items.stream(),0);
    }

    public void onBtnRoadmapClick() {
        List<Item> items = dm.load(Item.class)
                .query("select e from hierarchy_Item e WHERE e.type = :type and e.year = :year")
                .parameter("type", Type.ROOT)
                .parameter("year", lkpYear.getValue())
                .list();

        itemsTable.collapseAll();
        itemsTable.expandRecursively(items.stream(),1);
    }

    public void onBtnOPenClick() {
        itemsTable.expandAll();

    }
}