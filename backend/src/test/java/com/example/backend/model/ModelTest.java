package com.example.backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void role_shouldHaveThreeValues() {
        Role[] roles = Role.values();
        assertEquals(3, roles.length);
    }

    @Test
    void role_shouldContainAllExpectedRoles() {
        assertTrue(containsRole(Role.values(), "CUSTOMER"));
        assertTrue(containsRole(Role.values(), "SELLER"));
        assertTrue(containsRole(Role.values(), "ADMIN"));
    }

    @Test
    void role_valueOf_shouldReturnCorrectRole() {
        assertEquals(Role.CUSTOMER, Role.valueOf("CUSTOMER"));
        assertEquals(Role.SELLER, Role.valueOf("SELLER"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }

    @Test
    void role_name_shouldReturnCorrectString() {
        assertEquals("CUSTOMER", Role.CUSTOMER.name());
        assertEquals("SELLER", Role.SELLER.name());
        assertEquals("ADMIN", Role.ADMIN.name());
    }

    @Test
    void orderStatus_shouldHaveFiveValues() {
        OrderStatus[] statuses = OrderStatus.values();
        assertEquals(5, statuses.length);
    }

    @Test
    void orderStatus_shouldContainAllExpectedStatuses() {
        OrderStatus[] statuses = OrderStatus.values();
        assertTrue(containsOrderStatus(statuses, "PENDING"));
        assertTrue(containsOrderStatus(statuses, "PROCESSING"));
        assertTrue(containsOrderStatus(statuses, "SHIPPED"));
        assertTrue(containsOrderStatus(statuses, "DELIVERED"));
        assertTrue(containsOrderStatus(statuses, "CANCELED"));
    }

    @Test
    void orderStatus_valueOf_shouldReturnCorrectStatus() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.PROCESSING, OrderStatus.valueOf("PROCESSING"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"));
        assertEquals(OrderStatus.CANCELED, OrderStatus.valueOf("CANCELED"));
    }

    @Test
    void orderStatus_name_shouldReturnCorrectString() {
        assertEquals("PENDING", OrderStatus.PENDING.name());
        assertEquals("PROCESSING", OrderStatus.PROCESSING.name());
        assertEquals("SHIPPED", OrderStatus.SHIPPED.name());
        assertEquals("DELIVERED", OrderStatus.DELIVERED.name());
        assertEquals("CANCELED", OrderStatus.CANCELED.name());
    }

    @Test
    void orderStatus_ordinal_shouldReturnCorrectIndex() {
        assertEquals(0, OrderStatus.PENDING.ordinal());
        assertEquals(1, OrderStatus.PROCESSING.ordinal());
        assertEquals(2, OrderStatus.SHIPPED.ordinal());
        assertEquals(3, OrderStatus.DELIVERED.ordinal());
        assertEquals(4, OrderStatus.CANCELED.ordinal());
    }

    @Test
    void role_ordinal_shouldReturnCorrectIndex() {
        assertEquals(0, Role.CUSTOMER.ordinal());
        assertEquals(1, Role.SELLER.ordinal());
        assertEquals(2, Role.ADMIN.ordinal());
    }

    @Test
    void role_compareTo_shouldWorkCorrectly() {
        assertTrue(Role.CUSTOMER.compareTo(Role.SELLER) < 0);
        assertTrue(Role.SELLER.compareTo(Role.ADMIN) < 0);
        assertEquals(0, Role.ADMIN.compareTo(Role.ADMIN));
    }

    @Test
    void orderStatus_compareTo_shouldWorkCorrectly() {
        assertTrue(OrderStatus.PENDING.compareTo(OrderStatus.PROCESSING) < 0);
        assertTrue(OrderStatus.PROCESSING.compareTo(OrderStatus.SHIPPED) < 0);
        assertEquals(0, OrderStatus.DELIVERED.compareTo(OrderStatus.DELIVERED));
    }

    private boolean containsRole(Role[] roles, String roleName) {
        for (Role role : roles) {
            if (role.name().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsOrderStatus(OrderStatus[] statuses, String statusName) {
        for (OrderStatus status : statuses) {
            if (status.name().equals(statusName)) {
                return true;
            }
        }
        return false;
    }
}
