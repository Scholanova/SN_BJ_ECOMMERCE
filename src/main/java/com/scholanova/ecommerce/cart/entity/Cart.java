package com.scholanova.ecommerce.cart.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scholanova.ecommerce.order.entity.Orders;
import com.scholanova.ecommerce.product.entity.Product;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Entity(name = "cart")
public class Cart {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToOne(mappedBy = "cart", optional = true)
    private Orders orders;


    public Cart() {
    }

    public CartItem getCartItemByProductName(String productName) {
        return cartItems.stream().filter(item -> item.getProduct().getName().equals(productName)).findFirst().orElseThrow(() -> new NoSuchElementException(productName.toString()));
    }

    public Cart addProduct(Product product, int quantity) throws IllegalArgumentException{
        if (quantity < 1) throw new IllegalArgumentException("quantity must be a positive integer");
        CartItem item;
        try {
            item = getCartItemByProductName(product.getName());
            int newQuantity = item.getQuantity() + quantity;
            changeProductQuantity(product, newQuantity);
        } catch (NoSuchElementException exc) {
            item = CartItem.create(product, quantity);
        }
        this.cartItems.add(item);
        return this;
    }

    public Cart removeProduct(Product product) {
        CartItem itemToBeRemoved = getCartItemByProductName(product.getName());
        this.cartItems.remove(itemToBeRemoved);
        return this;
    }

    public Cart changeProductQuantity(Product product, int quantity) throws IllegalArgumentException{
        if (quantity < 1) throw new IllegalArgumentException("quantity must be a positive integer");
        CartItem itemToChangeQuantity = getCartItemByProductName(product.getName());
        itemToChangeQuantity.setQuantity(quantity);
        return this;
    }

    @JsonProperty("totalPrice")
    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (CartItem item: cartItems) {
            totalPrice = totalPrice.add(BigDecimal.valueOf(
                    item.getProduct().getPriceVatExcluded() * (1+item.getProduct().getVat()) * item.getQuantity()
            ));
        }
        return totalPrice.setScale(2, RoundingMode.HALF_EVEN);
    }

    @JsonProperty("totalQuantity")
    public int getTotalQuantity() {
        int totalQuantity = 0;
        for (CartItem item: cartItems) {
            totalQuantity = totalQuantity + item.getQuantity();
        }
        return totalQuantity;
    }

    public Long getId() {return id;}

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartItem> getCartItems() {return cartItems;}

    public void setCartItems(List<CartItem> products) {
        this.cartItems = products;
    }

    public Orders getOrders() {return orders;}

    public void setOrders(Orders orders) {
        this.orders = orders;
    }
}
