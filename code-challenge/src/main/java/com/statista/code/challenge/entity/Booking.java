package com.statista.code.challenge.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    private String id;
    @NotNull
    private String description;
    @NotNull
    private double price;
    @NotNull
    private Currency currency;
    @NotNull
    @JsonProperty("subscription_start_date")
    private Long subscriptionStartDate;
    @NotNull
    private String email;
    @NotNull
    private String department;

    public Booking(String description, double price, Currency currency, Long subscriptionStartDate, String email,
                   String department) {
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.subscriptionStartDate = subscriptionStartDate;
        this.email = email;
        this.department = department;
    }
}
