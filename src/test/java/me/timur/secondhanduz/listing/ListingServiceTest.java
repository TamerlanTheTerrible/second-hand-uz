package me.timur.secondhanduz.listing;

import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.logging.AuditLogger;
import me.timur.secondhanduz.common.util.InputSanitizer;
import me.timur.secondhanduz.listing.application.port.out.ListingRepository;
import me.timur.secondhanduz.listing.application.service.ListingServiceImpl;
import me.timur.secondhanduz.listing.domain.Listing;
import me.timur.secondhanduz.listing.domain.ListingCondition;
import me.timur.secondhanduz.listing.domain.ListingStatus;
import me.timur.secondhanduz.listing.web.dto.CreateListingRequest;
import me.timur.secondhanduz.listing.web.dto.ListingResponse;
import me.timur.secondhanduz.listing.web.dto.UpdateListingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock private ListingRepository listingRepository;
    @Mock private AuditLogger auditLogService;
    @Mock private InputSanitizer contentSanitizer;

    @InjectMocks
    private ListingServiceImpl listingService;

    private Listing activeListing;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listingService, "uploadPath", "/tmp/uploads");
        activeListing = new Listing(1L, "Nike Shoes", "Good pair", BigDecimal.valueOf(50),
                "42", "Nike", ListingCondition.GOOD);
    }

    @Test
    void should_createListing_when_requestIsValid() {
        when(contentSanitizer.sanitize(anyString())).thenAnswer(i -> i.getArgument(0));
        when(listingRepository.save(any())).thenReturn(activeListing);

        ListingResponse response = listingService.createListing(
                new CreateListingRequest("Nike Shoes", "Good pair",
                        BigDecimal.valueOf(50), "42", "Nike", ListingCondition.GOOD), 1L);

        assertThat(response.title()).isEqualTo("Nike Shoes");
        assertThat(response.price()).isEqualByComparingTo(BigDecimal.valueOf(50));
        verify(listingRepository).save(any(Listing.class));
    }

    @Test
    void should_throwForbidden_when_nonOwnerTriesToUpdate() {
        when(listingRepository.findById(1L)).thenReturn(Optional.of(activeListing));

        assertThatThrownBy(() -> listingService.updateListing(1L,
                new UpdateListingRequest("New", "Desc", BigDecimal.ONE, "M", "Brand", ListingCondition.NEW),
                99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("do not own");
    }

    @Test
    void should_throwConflict_when_updatingSoldListing() {
        activeListing.markSold();
        when(listingRepository.findById(1L)).thenReturn(Optional.of(activeListing));

        assertThatThrownBy(() -> listingService.updateListing(1L,
                new UpdateListingRequest("T", "D", BigDecimal.ONE, "S", "B", ListingCondition.FAIR),
                1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("sold");
    }

    @Test
    void should_markDeleted_when_ownerDeletesListing() {
        when(listingRepository.findById(1L)).thenReturn(Optional.of(activeListing));
        when(listingRepository.save(any())).thenReturn(activeListing);

        listingService.deleteListing(1L, 1L);

        assertThat(activeListing.getStatus()).isEqualTo(ListingStatus.DELETED);
    }

    @Test
    void should_throwNotFound_when_listingDoesNotExist() {
        when(listingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listingService.getListing(999L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not found");
    }
}
