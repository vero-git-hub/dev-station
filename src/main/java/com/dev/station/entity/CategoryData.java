package com.dev.station.entity;

import java.util.List;

public class CategoryData {
    private String name;
    private int id;
    private List<ProgramData> programs;


    public CategoryData(String name, int id, List<ProgramData> programs) {
        this.name = name;
        this.id = id;
        this.programs = programs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ProgramData> getPrograms() {
        return programs;
    }

    public void setPrograms(List<ProgramData> programs) {
        this.programs = programs;
    }
}
