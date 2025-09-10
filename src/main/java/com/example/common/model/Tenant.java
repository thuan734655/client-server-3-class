package com.example.common.model;

public class Tenant {
    private Integer user_id; // nullable for create
    private String contact_number;
    private String gender;
    private Integer roomNumber;
    private String name;

    public Tenant() {}

    public Tenant(Integer user_id, String contact_number, String gender, Integer roomNumber, String name) {
        this.user_id = user_id;
        this.contact_number = contact_number;
        this.gender = gender;
        this.roomNumber = roomNumber;
        this.name = name;
    }

    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public String getContact_number() { return contact_number; }
    public void setContact_number(String contact_number) { this.contact_number = contact_number; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getRoomNumber() { return roomNumber; }
    public void setRoomNumber(Integer roomNumber) { this.roomNumber = roomNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
