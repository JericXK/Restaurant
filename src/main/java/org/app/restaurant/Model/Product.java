package org.app.restaurant.Model;

import java.math.BigDecimal;

public class Product {
    private Integer id;
    private String productID;
    private String name;
    private String type;
    private BigDecimal price;
    private Integer quantity;

    public Product(int id, String productID, String name, String type, BigDecimal price, Integer quantity) {
        this.id = this.id;
        this.productID = productID;
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getId(){
        return id;
    }

    public String getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}