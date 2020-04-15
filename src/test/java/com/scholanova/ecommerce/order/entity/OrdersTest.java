package com.scholanova.ecommerce.order.entity;

import com.scholanova.ecommerce.cart.entity.Cart;
import com.scholanova.ecommerce.order.exception.IllegalArgException;
import com.scholanova.ecommerce.order.exception.NotAllowedException;
import com.scholanova.ecommerce.product.entity.Product;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.BIG_DECIMAL;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)

class OrdersTest {

    @Test
    public void checkout_ShouldSetTheDateAndTimeOfTodayInTheOrder() throws NotAllowedException, IllegalArgException {
        Orders order = new Orders();
        order.checkout();
        Date now = new Date(System.currentTimeMillis());
        assertThat(order.getIssueDate().equals(now) );
    }

    @Test
    public void checkout_ShouldSetOrderStatusToPending() throws NotAllowedException, IllegalArgException {
        Orders order = new Orders();
        order.checkout();
        assertThat(order.getStatus() == OrderStatus.PENDING);
    }

    @Test
    public void checkout_ShouldThrowNotAllowedExceptionIfStatusIsClosed() throws NotAllowedException {
        Orders order = new Orders();
        order.setStatus(OrderStatus.CLOSED);
        assertThrows(NotAllowedException.class, () -> order.checkout());
    }

    @Test
    public void checkout_ShouldThrowIllegalArgExceptionIfCartTotalItemsQuantityIsZERO() throws NotAllowedException {
        Orders order = new Orders();
        Cart cart = new Cart();
        order.setCart(cart);
        assertThrows(IllegalArgException.class, () -> order.checkout());
    }

    @Test
    public void setCart_ShouldThrowNotAllowedExceptionIfStatusIsClosed(){
        Orders order = new Orders();
        Cart cart = new Cart();
        order.setStatus(OrderStatus.CLOSED);
        assertThrows(NotAllowedException.class, () -> order.setCart(cart));
    }

    @Test
    public void createOrder_ShouldSetTheCartInTheOrder() throws NotAllowedException {
        Orders order = new Orders();
        Cart cart = new Cart();
        order.createOrder(cart);
        assertThat(order.getCart().equals(cart));
    }

    @Test
    public void createOrder_ShouldSetStatusToCreated() throws NotAllowedException {
        Orders order = new Orders();
        Cart cart = new Cart();
        order.createOrder(cart);
        assertThat(order.getStatus() == OrderStatus.CREATED);
    }

    @Test
    public void getDiscount_shouldReturnZEROIFCartTotalPriceIsLessThan100() throws NotAllowedException {
        Orders order = new Orders();
        Cart cart = new Cart();
        order.setCart(cart);
        assertThat(order.getDiscount()).isEqualTo(0);
    }

    @Test
    public void getDiscount_shouldReturn5percentIfCartTotalPriceIsMoreOrEqual100() throws NotAllowedException {
        Orders order = new Orders();

        Cart cart = new Cart();
        int quantity = 20;
        double price = 11.55;
        Product product = Product.create("tested", "tested", 10.5f, 0.1f, "EUR");
        product.setId((long) 12);
        cart.addProduct(product, quantity);

        order.setCart(cart);
        assertThat(order.getDiscount()).isEqualTo(((price * quantity) / 100) * 5);
    }

    @Test
    public void getOrderPrice_shouldReturnTotalPriceWithDiscount() throws NotAllowedException {
        Orders order = new Orders();

        Cart cart = new Cart();
        int quantity = 20;
        double price = 11.55;
        double prixTotal = price * quantity;
        Product product = Product.create("tested", "tested", 10.5f, 0.1f, "EUR");
        product.setId((long) 12);
        cart.addProduct(product, quantity);



        order.setCart(cart);
        assertThat(order.getOrderPrice()).isEqualTo(prixTotal - (((prixTotal) / 100) * 5));
    }

    @Test
    public void close_ShouldSetStatusToClose(){
        Orders order = new Orders();
        order.close();
        assertThat(order.getStatus() == OrderStatus.CLOSED);
    }

}