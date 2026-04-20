package com.al7afez.al7afez.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class MistakeType extends MasterFile {
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private MistakeType parent;

    public MistakeType getParent() {
        return parent;
    }

    public void setParent(MistakeType parent) {
        this.parent = parent;
    }
}
