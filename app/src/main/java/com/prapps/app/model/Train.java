package com.prapps.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pratik on 19/2/17.
 */

public class Train {
    private Long id;
    private String trainType;
    private String name;
    @JsonIgnore
    private List<RunDayType> runDayTypes;
    private List<Integer> runDayType;
    private List<Route> routes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public List<RunDayType> getRunDayTypes() {
        return runDayTypes;
    }

    public void setRunDayTypes(List<RunDayType> runDayTypes) {
        this.runDayTypes = runDayTypes;
    }

    public List<Integer> getRunDayType() {
        return runDayType;
    }

    public void setRunDayType(List<Integer> runDayType) {
        this.runDayType = runDayType;
        runDayTypes = new ArrayList<>(runDayType.size());
        for (Integer temp : runDayType) {
            runDayTypes.add(RunDayType.getByDayOfWeek(temp));
        }
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
