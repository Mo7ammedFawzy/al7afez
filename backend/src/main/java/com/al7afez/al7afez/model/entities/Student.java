package com.al7afez.al7afez.model.entities;

import com.al7afez.al7afez.model.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Student extends MasterFile {
    private LocalDate birthDate;
    private String phoneNumber;
    private String parentPhoneNumber;
    private Gender gender;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private RecitationGroup recitationGroup;

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getParentPhoneNumber() {
        return parentPhoneNumber;
    }

    public void setParentPhoneNumber(String parentPhoneNumber) {
        this.parentPhoneNumber = parentPhoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public RecitationGroup getRecitationGroup() {
        return recitationGroup;
    }

    public void setRecitationGroup(RecitationGroup recitationGroup) {
        this.recitationGroup = recitationGroup;
    }
}
