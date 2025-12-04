package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Query;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api.PhysicianDTO;

import java.util.List;


public class SearchPhysiciansQuery implements Query<List<PhysicianDTO>> {

    private final String name;
    private final String specialty;
    private final int page;
    private final int limit;

    public SearchPhysiciansQuery(String name, String specialty, int page, int limit) {
        this.name = name;
        this.specialty = specialty;
        this.page = page;
        this.limit = limit;
    }

    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public int getPage() { return page; }
    public int getLimit() { return limit; }
}
