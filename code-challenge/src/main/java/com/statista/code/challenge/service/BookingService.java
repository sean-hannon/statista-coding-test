package com.statista.code.challenge.service;

import com.statista.code.challenge.entity.Booking;
import com.statista.code.challenge.entity.BookingHumanReadableDate;
import com.statista.code.challenge.repository.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class BookingService {

    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity createBooking(Booking booking){
        if (repository.find(booking.getId()) != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Booking bookingAfterSave = repository.saveOrUpdate(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingAfterSave.getId());
    }

    public ResponseEntity updateBooking(String bookingId, Booking booking) {
        if (repository.find(bookingId) == null){
            return ResponseEntity.badRequest().build();
        }
        repository.saveOrUpdate(booking);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity find(String bookingId) {
        Booking booking = repository.find(bookingId);
        if (booking == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(booking);
    }

    public ResponseEntity findByDepartment(String department) {
        List bookingsByDepartment = repository.findByDepartment(department);
        if (bookingsByDepartment.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(bookingsByDepartment);
    }

    public ResponseEntity retrieveCurrencyUsed() {
        Set currenciesUsed = repository.findCurrenciesUsed();
        if (currenciesUsed.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(currenciesUsed);
    }

    public ResponseEntity retrieveSumByCurrency(String currency) {
        List<Booking> bookingsByCurrency = repository.findByCurrency(currency);
        double result = 0.0;
        for (Booking booking : bookingsByCurrency) {
            result += booking.getPrice();
        }
        return ResponseEntity.ok(result);
    }

    public ResponseEntity doBusiness(String department) {
        switch (department.toLowerCase()){
            case "internal":
                return convertSubscriptionStartDateToHumanReadable(department);
            case "sales":
                return averageAllSalesByCurrency(department);
            default:
                return ResponseEntity.badRequest().build();
        }
    }

     ResponseEntity averageAllSalesByCurrency(String department) {
        List<Booking> bookingByDepartment = repository.findByDepartment(department);
        Map<Currency, Double> currencyTotals = new HashMap<>();
        Map<Currency, Integer> currencyCount = new HashMap<>();
        for (Booking booking : bookingByDepartment) {
            if (currencyTotals.containsKey(booking.getCurrency())) {
                currencyTotals.put(booking.getCurrency(), currencyTotals.get(booking.getCurrency()) + booking.getPrice());
                currencyCount.put(booking.getCurrency(), currencyCount.get(booking.getCurrency()) + 1);
            } else {
                currencyTotals.put(booking.getCurrency(), booking.getPrice());
                currencyCount.put(booking.getCurrency(), 1);
            }
        }
        Map<Currency, Double> averagePerCurrency = new HashMap<>();
        for (Map.Entry<Currency, Double> entry : currencyTotals.entrySet()){
            averagePerCurrency.put(entry.getKey(), entry.getValue()/currencyCount.get(entry.getKey()));
        }
        return ResponseEntity.ok(averagePerCurrency);
    }

     ResponseEntity convertSubscriptionStartDateToHumanReadable(String department) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.GERMANY)
                .withZone(ZoneId.systemDefault());
        List<Booking> bookingByDepartment = repository.findByDepartment(department);
        List<BookingHumanReadableDate> result = new ArrayList<>();
        for (Booking booking : bookingByDepartment) {
            BookingHumanReadableDate bookingHumanReadableDate = new BookingHumanReadableDate(booking);
            String date = formatter.format(Instant.ofEpochMilli(booking.getSubscriptionStartDate()));
            bookingHumanReadableDate.setHumanReadableSubscriptionStartDate(date);
            result.add(bookingHumanReadableDate);
        }
        return ResponseEntity.ok(result);
    }
}
