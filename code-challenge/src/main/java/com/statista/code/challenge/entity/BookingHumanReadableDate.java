package com.statista.code.challenge.entity;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class BookingHumanReadableDate extends Booking{
    private String humanReadableSubscriptionStartDate;

    public BookingHumanReadableDate(Booking booking) {
        this.setId(booking.getId());
        this.setCurrency(booking.getCurrency());
        this.setDepartment(booking.getDepartment());
        this.setDescription(booking.getDescription());
        this.setEmail(booking.getEmail());
        this.setPrice(booking.getPrice());
        this.setSubscriptionStartDate(booking.getSubscriptionStartDate());
    }
}
