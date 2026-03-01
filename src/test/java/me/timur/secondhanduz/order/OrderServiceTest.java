package me.timur.secondhanduz.order;

import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.logging.AuditLogger;
import me.timur.secondhanduz.listing.application.port.out.ListingRepository;
import me.timur.secondhanduz.listing.domain.Listing;
import me.timur.secondhanduz.listing.domain.ListingCondition;
import me.timur.secondhanduz.order.application.port.out.OrderRepository;
import me.timur.secondhanduz.order.application.service.OrderServiceImpl;
import me.timur.secondhanduz.order.domain.Order;
import me.timur.secondhanduz.order.domain.OrderStatus;
import me.timur.secondhanduz.order.web.dto.CreateOrderRequest;
import me.timur.secondhanduz.order.web.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ListingRepository listingRepository;
    @Mock private AuditLogger auditLogService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Listing activeListing;
    private Order createdOrder;

    @BeforeEach
    void setUp() {
        activeListing = new Listing(2L, "Shoes", "desc", BigDecimal.valueOf(100),
                "42", "Nike", ListingCondition.GOOD);
        createdOrder = new Order(1L, 1L, BigDecimal.valueOf(100));
    }

    @Test
    void should_createOrder_when_listingIsAvailable() {
        when(listingRepository.findById(1L)).thenReturn(Optional.of(activeListing));
        when(orderRepository.existsByListingId(1L)).thenReturn(false);
        when(orderRepository.save(any())).thenReturn(createdOrder);

        OrderResponse response = orderService.createOrder(new CreateOrderRequest(1L), 1L);

        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.buyerId()).isEqualTo(1L);
    }

    @Test
    void should_throwConflict_when_listingAlreadyOrdered() {
        when(listingRepository.findById(1L)).thenReturn(Optional.of(activeListing));
        when(orderRepository.existsByListingId(1L)).thenReturn(true);

        assertThatThrownBy(() -> orderService.createOrder(new CreateOrderRequest(1L), 1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("already been ordered");
    }

    @Test
    void should_throwConflict_when_listingNotActive() {
        activeListing.markSold();
        when(listingRepository.findById(1L)).thenReturn(Optional.of(activeListing));

        assertThatThrownBy(() -> orderService.createOrder(new CreateOrderRequest(1L), 1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void should_cancelOrder_when_orderIsInCreatedState() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(createdOrder));
        when(orderRepository.save(any())).thenReturn(createdOrder);

        OrderResponse response = orderService.cancelOrder(1L, 1L);

        assertThat(response.status()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void should_throwConflict_when_cancelingPaidOrder() {
        createdOrder.markPaid();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(createdOrder));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("cannot be canceled");
    }

    @Test
    void should_throwForbidden_when_differentUserAccessesOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(createdOrder));

        assertThatThrownBy(() -> orderService.getOrder(1L, 999L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Access denied");
    }
}
