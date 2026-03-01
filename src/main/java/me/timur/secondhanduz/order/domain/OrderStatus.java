package me.timur.secondhanduz.order.domain;

/** Order lifecycle status. */
public enum OrderStatus {
    CREATED,
    PAID,
    SHIPPED,
    COMPLETED,
    CANCELED
}
