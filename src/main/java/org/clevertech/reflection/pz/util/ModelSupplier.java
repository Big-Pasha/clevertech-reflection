package org.clevertech.reflection.pz.util;

import org.clevertech.reflection.pz.model.Customer;
import org.clevertech.reflection.pz.model.Order;
import org.clevertech.reflection.pz.model.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ModelSupplier {

    public static Customer getCustomer() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setOrders(getOrders());
        customer.setBirthDate(LocalDate.now());
        customer.setFirstName("Tolik");
        customer.setLastName("Anabolik");

        return customer;
    }

    public static List<Order> getOrders() {
        List<Order> orderList = new ArrayList<>();
        orderList.add(getOrder());
        orderList.add(getOrder());

        return orderList;
    }

    public static Order getOrder() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setProducts(getProducts());
        order.setCreateDate(OffsetDateTime.now());

        return order;
    }

    public static List<Product> getProducts() {
        List<Product> productList = new ArrayList<>();
        productList.add(getProduct());
        productList.add(getProduct());

        return productList;
    }

    public static Product getProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Gear");
        product.setPrice(78.521);
        product.setPriceMap(
                new HashMap<>() {{
                    put(UUID.randomUUID(), new BigDecimal("75.4"));
                    put(UUID.randomUUID(), new BigDecimal("78.323"));
                }}
        );

        return product;
    }
}
