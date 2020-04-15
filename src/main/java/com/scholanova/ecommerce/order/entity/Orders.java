package com.scholanova.ecommerce.order.entity;
import com.scholanova.ecommerce.order.exception.IllegalArgException;
import com.scholanova.ecommerce.order.exception.NotAllowedException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scholanova.ecommerce.cart.entity.Cart;
import com.sun.xml.bind.v2.TODO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;

@Entity(name="orders")
public class Orders {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column
    private String number;

    @Column
    private Date issueDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="cart_id", referencedColumnName = "id")
    private Cart cart;

    public Orders() {
    }

    public void createOrder(Cart cart) throws NotAllowedException {
        //TODO
        setCart(cart);
        setStatus(OrderStatus.CREATED);
    }

    public void checkout() throws NotAllowedException, IllegalArgException {
        //TODO
        if (this.status == OrderStatus.CLOSED){
            throw new NotAllowedException("Order is CLOSED");
        }
        if ( this.cart != null && cart.getTotalQuantity()==0) {
            throw new IllegalArgException("Order contain 0 quantity of item");
        }
        this.issueDate = new Date(System.currentTimeMillis());
        this.status = OrderStatus.PENDING;
    }

    public double getDiscount(){
        //TODO

        if ( this.cart != null && cart.getTotalPrice().compareTo(new BigDecimal("100")) < 0) {
            return 0;
        }else if ( this.cart != null && cart.getTotalPrice().compareTo(new BigDecimal("100")) >= 0){

            return cart.getTotalPrice().divide(new BigDecimal("100")).multiply(new BigDecimal("5")).doubleValue();
        }
        return 10;

    }

    public double getOrderPrice(){
        //TODO
        return cart.getTotalPrice().subtract(new BigDecimal(getDiscount())).doubleValue();
    }

    public void close(){
        //TODO
        setStatus(OrderStatus.CLOSED);
    }


    public Long getId() {return id;}

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {return number;}

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getIssueDate() {return issueDate;}

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public OrderStatus getStatus() {return status;}

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Cart getCart() {return cart;}

    public void setCart(Cart cart) throws NotAllowedException {
        if (this.status == OrderStatus.CLOSED){
            throw new NotAllowedException("Order is CLOSED");
        }
        this.cart = cart;
    }
}
