package com.al7afez.al7afez.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RecitationGroup extends MasterFile {
    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne
    @JoinColumn(name = "sheikh_id")
    private Sheikh sheikh;

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Sheikh getSheikh() {
        return sheikh;
    }

    public void setSheikh(Sheikh sheikh) {
        this.sheikh = sheikh;
    }
}
