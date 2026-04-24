package com.al7afez.al7afez.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
@Entity
public class AppUser extends MasterFile {
    @ManyToOne
    @JoinColumn(name = "sheikh_id")
    private Sheikh sheikh;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    public Sheikh getSheikh() {
        return sheikh;
    }

    public void setSheikh(Sheikh sheikh) {
        this.sheikh = sheikh;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
