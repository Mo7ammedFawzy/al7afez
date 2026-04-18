package com.al7afez.al7afez.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

@Entity
public class RecitationDocument extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private LocalDate recitationDate;
    private Integer fromSurah;
    private Integer toSurah;
    private Integer fromAya;
    private Integer toAya;
    private Integer numberOfAyat;
    private Integer grade;
    private String notes;

    @OneToMany(mappedBy = "recitationDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<RecitationMistake> mistakes = new ArrayList<>();

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDate getRecitationDate() {
        return recitationDate;
    }

    public void setRecitationDate(LocalDate recitationDate) {
        this.recitationDate = recitationDate;
    }

    public Integer getFromSurah() {
        return fromSurah;
    }

    public void setFromSurah(Integer fromSurah) {
        this.fromSurah = fromSurah;
    }

    public Integer getToSurah() {
        return toSurah;
    }

    public void setToSurah(Integer toSurah) {
        this.toSurah = toSurah;
    }

    public Integer getFromAya() {
        return fromAya;
    }

    public void setFromAya(Integer fromAya) {
        this.fromAya = fromAya;
    }

    public Integer getToAya() {
        return toAya;
    }

    public void setToAya(Integer toAya) {
        this.toAya = toAya;
    }

    public Integer getNumberOfAyat() {
        return numberOfAyat;
    }

    public void setNumberOfAyat(Integer numberOfAyat) {
        this.numberOfAyat = numberOfAyat;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<RecitationMistake> getMistakes() {
        return mistakes;
    }

    public void setMistakes(List<RecitationMistake> mistakes) {
        this.mistakes = mistakes;
    }
}
