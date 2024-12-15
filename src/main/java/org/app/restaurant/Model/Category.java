package org.app.restaurant.Model;

import java.math.BigDecimal;

public class Category {
    private String productID;
    private String name;
    private String type;
    private BigDecimal price;
    private String status;

    public Category(String productID, String name, String type, BigDecimal price, String status){
        this.productID = productID;
        this.name = name;
        this.type = type;
        this.price = price;
        this.status = status;
    }

    public String getProductID(){
        return productID;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }
    public BigDecimal getPrice(){
        return price;
    }

    public String getStatus(){
        return status;
    }
}
