package com.al7afez.al7afez.entities;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class MasterFile extends BaseEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}