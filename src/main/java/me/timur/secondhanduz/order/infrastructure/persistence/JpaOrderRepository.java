package me.timur.secondhanduz.order.infrastructure.persistence;

import me.timur.secondhanduz.order.application.port.out.OrderRepository;
import me.timur.secondhanduz.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA adapter fulfilling the {@link OrderRepository} output port.
 */
@Repository
public interface JpaOrderRepository extends JpaRepository<Order, Long>, OrderRepository {

    @Override
    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);

    @Override
    boolean existsByListingId(Long listingId);
}
