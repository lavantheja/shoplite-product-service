package com.shoplite.product_service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductBasicTest {

    @Test
    void shouldCalculateTotalPrice() {

        double price = 89.99;
        int quantity = 2;

        double total = price * quantity;

        assertEquals(179.98, total, 0.001);
    }
}