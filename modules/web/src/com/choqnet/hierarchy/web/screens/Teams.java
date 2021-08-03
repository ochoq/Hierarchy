package com.choqnet.hierarchy.web.screens;

import com.choqnet.hierarchy.entity.Capacity;
import com.choqnet.hierarchy.entity.Item;
import com.choqnet.hierarchy.entity.Team;
import com.haulmont.charts.gui.components.charts.SerialChart;
import com.haulmont.charts.gui.data.ListDataProvider;
import com.haulmont.charts.gui.data.MapDataItem;
import com.haulmont.chile.core.model.Instance;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@UiController("hierarchy_Teams")
@UiDescriptor("teams.xml")
@LoadDataBeforeShow
public class Teams extends Screen {
    @Inject
    private DataGrid<Team> teamsTable;
    @Inject
    private DataManager dm;
    @Inject
    private CollectionLoader<Team> teamsDl;
    @Inject
    private CollectionLoader<Capacity> capacitiesDl;
    @Inject
    private LookupField<String> lkpYear;
    @Inject
    private DataGrid<Capacity> capacitiesTable;
    @Inject
    private Label<String> lblTitle;
    @Inject
    private CollectionContainer<Capacity> capacitiesDc;
    @Inject
    private VBoxLayout capacityBox;

    private ListDataProvider dp;
    @Inject
    private SerialChart chart;

    @Subscribe
    public void onInit(InitEvent event) {
        // add the years to the lookupField
        List<String> list = Arrays.asList("2021","2022");
        lkpYear.setOptionsList(list);
        // set the current year
        lkpYear.setValue("2022");

    }

    @Subscribe("teamsTable")
    public void onTeamsTableSelection(DataGrid.SelectionEvent<Team> event) {
        if (teamsTable.getSingleSelected()!=null) {
            refreshCapacity();
            lblTitle.setValue("Capacity for the team " + teamsTable.getSingleSelected().getFullName() + " for the year ");
        }

    }

    @Subscribe("lkpYear")
    public void onLkpYearValueChange(HasValue.ValueChangeEvent event) {
        refreshCapacity();
    }

    public void onTeamsTableAddBtnClick() {
        Team newTeam = dm.create(Team.class);
        newTeam.setPlatform(teamsTable.getSingleSelected()==null ? "" : teamsTable.getSingleSelected().getPlatform());
        newTeam.setTribe(teamsTable.getSingleSelected()==null ? "" : teamsTable.getSingleSelected().getTribe());
        dm.commit(newTeam);
        teamsDl.load();
    }

    @Subscribe("teamsTable")
    public void onTeamsTableEditorPostCommit(DataGrid.EditorPostCommitEvent<Team> event) {
        dm.commit(event.getItem());
        teamsDl.load();

    }

    private void refreshCapacity() {
        if (lkpYear.getValue()==null || teamsTable.getSingleSelected()==null) {
            capacityBox.setVisible(false);
        } else {
            capacityBox.setVisible(true);
            capacitiesDl.setQuery("select e from hierarchy_Capacity e where e.year ='" + lkpYear.getValue() + "' and e.team = :team");
            capacitiesDl.setParameter("team", teamsTable.getSingleSelected());
            capacitiesDl.load();
            if (capacitiesDc.getItems().size()==0) {
                Capacity capacity = dm.create(Capacity.class);
                capacity.setYear(lkpYear.getValue());
                capacity.setTeam(teamsTable.getSingleSelected());
                dm.commit(capacity);
                capacitiesDl.load();
            }
            drawCpacityPlan();
        }
    }

    private void drawCpacityPlan() {
        Team team = teamsTable.getSingleSelected();
        String year = lkpYear.getValue();
        chart.setDataProvider(null);
        dp = new ListDataProvider();
        for (int i=1 ; i<5; i++) {
            String quarter = "Q" + i;
            MapDataItem mdi = new MapDataItem();
            mdi.add("quarter", quarter);
            List<Capacity> capacities = dm.load(Capacity.class)
                    .query("select e from hierarchy_Capacity e where e.year = '" + year + "' and e.team = :team")
                    .parameter("team", team)
                    .list();

            int finalI1 = i;
            int capacity = capacities.stream().map(o -> o.giveQ(finalI1)).reduce(0, Integer::sum);
            mdi.add("capacity", capacity);
            List<Item> demands = dm.load(Item.class)
                    .query("select e from hierarchy_Item e where e.year ='" + year +"' and e.team = :team")
                    .parameter("team", team)
                    .list();
            int finalI = i;
            int demand = demands.stream().map(o -> o.giveQ(finalI)).reduce(0, Integer::sum);
            mdi.add("demand", demand);
            dp.addItem(mdi);
        }
        chart.setDataProvider(dp);
        chart.repaint();
    }

    @Subscribe("capacitiesTable")
    public void onCapacitiesTableEditorPostCommit(DataGrid.EditorPostCommitEvent event) {
        dm.commit(event.getItem());
        drawCpacityPlan();
    }


}