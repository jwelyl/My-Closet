package com.example.mycloset.entity;

public class Clothes {
    private Long id;    //  Primary Key
    private String name;    //  옷 이름
    private String category;    //  옷 종류
    private String brand;       //  옷 브랜드
    private String purchaseDate;
    private Integer price;  //  옷 가격
    private String originPicName;
    private String picPath;
    private String createdDate;
    private String modifiedDate;

    public Clothes(String name, String category, String brand, String purchaseDate, Integer price, String originPicName, String picPath) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.originPicName = originPicName;
        this.picPath = picPath;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public Integer getPrice() {
        return price;
    }

    public String getOriginPicName() {
        return originPicName;
    }

    public String getPicPath() {
        return picPath;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setOriginPicName(String originPicName) {
        this.originPicName = originPicName;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getSelectionNum(String category) {
        if (category.equals("상의")) return 0;
        else if (category.equals("하의")) return 1;
        else if (category.equals("외투")) return 2;
        else if (category.equals("신발")) return 3;
        else if (category.equals("가방")) return 4;
        else if (category.equals("모자")) return 5;
        else return 6;
    }

    @Override
    public String toString() {
        return "Clothes{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", price=" + price +
                '}';
    }
}
