package me.timur.secondhanduz.listing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Listing domain entity representing an item for sale.
 */
@Entity
@Table(name = "listings")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    private String size;
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingCondition condition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status;

    // Stored as PostgreSQL TEXT[] array
    @Column(name = "image_urls", columnDefinition = "TEXT[]")
    private String[] imageUrls;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Listing() {}

    public Listing(Long sellerId, String title, String description, BigDecimal price,
                   String size, String brand, ListingCondition condition) {
        this.sellerId = sellerId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.size = size;
        this.brand = brand;
        this.condition = condition;
        this.status = ListingStatus.ACTIVE;
        this.imageUrls = new String[0];
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getSellerId() { return sellerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getSize() { return size; }
    public String getBrand() { return brand; }
    public ListingCondition getCondition() { return condition; }
    public ListingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<String> getImageUrls() {
        return imageUrls != null ? List.of(imageUrls) : List.of();
    }

    public boolean isOwnedBy(Long userId) {
        return this.sellerId.equals(userId);
    }

    public boolean isActive() {
        return this.status == ListingStatus.ACTIVE;
    }

    public void update(String title, String description, BigDecimal price,
                       String size, String brand, ListingCondition condition) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.size = size;
        this.brand = brand;
        this.condition = condition;
    }

    public void markSold() {
        this.status = ListingStatus.SOLD;
    }

    public void markDeleted() {
        this.status = ListingStatus.DELETED;
    }

    public void addImageUrl(String url) {
        List<String> list = new ArrayList<>(getImageUrls());
        list.add(url);
        this.imageUrls = list.toArray(new String[0]);
    }
}
