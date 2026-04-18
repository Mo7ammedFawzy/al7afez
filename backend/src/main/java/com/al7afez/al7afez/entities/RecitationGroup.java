package com.al7afez.al7afez.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RecitationGroup extends MasterFile {
    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne
    @JoinColumn(name = "sheikh_id")
    private Sheikh sheikh;

    @OneToMany(mappedBy = "recitationGroup")
    private List<Student> students = new ArrayList<>();

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

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
