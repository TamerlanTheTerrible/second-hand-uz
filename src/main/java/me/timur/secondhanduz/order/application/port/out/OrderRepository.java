package me.timur.secondhanduz.order.application.port.out;

import me.timur.secondhanduz.order.domain.Order;
import me.timur.secondhanduz.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Output port: persistence contract for {@link Order} entities.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);

    /** Returns true if a non-canceled (active) order exists for the given listing. */
    boolean existsByListingIdAndStatusNot(Long listingId, OrderStatus status);
}
