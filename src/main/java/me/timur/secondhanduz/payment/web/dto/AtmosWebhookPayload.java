package me.timur.secondhanduz.payment.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** ATMOS payment webhook notification payload. */
public record AtmosWebhookPayload(
        @JsonProperty("transaction_id")    String transactionId,
        @JsonProperty("store_transaction") String storeTransaction,
        @JsonProperty("amount")            Long amount,
        @JsonProperty("status")            String status
) {}
