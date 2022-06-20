package com.example.mycloset.entity;

public class Member {
    private Long id;
    private String name;
    private Integer age;
    private String address;
    private String createdDate;
    private String modifiedDate;

    public Member(Long id, String name, Integer age, String address, String createdDate, String modifiedDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }


}
