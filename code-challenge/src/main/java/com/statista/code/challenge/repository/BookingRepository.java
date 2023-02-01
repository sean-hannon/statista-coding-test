package com.statista.code.challenge.repository;

import com.statista.code.challenge.entity.Booking;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BookingRepository {

    private Map<String, Booking> bookings = new ConcurrentHashMap<>();

    public Booking saveOrUpdate(Booking booking){
        if (booking.getId() == null){
            booking.setId(UUID.randomUUID().toString());
        }
        bookings.put(booking.getId(), booking);

        return booking;
    }

    public Booking find(String id){
        if (id == null){
            return null;
        }
        return bookings.get(id);
    }

    public Collection<Booking> findAll(){
        return bookings.values();
    }

    public List findByDepartment(String department) {
        List<Booking> result = new ArrayList<>();
        var bookings = findAll();
        for (Booking booking: bookings) {
            if (Objects.equals(booking.getDepartment(), department)){
                result.add(booking);
            }
        }
        return result;
    }

    public Set findCurrenciesUsed() {
        Set<Currency> result = new HashSet<>();
        var bookings = findAll();
        for (Booking booking : bookings) {
            result.add(booking.getCurrency());
        }
        return result;
    }

    public List findByCurrency(String currency) {
        List<Booking> result = new ArrayList<>();
        var bookings = findAll();
        for (Booking booking : bookings){
            if (Objects.equals(booking.getCurrency().getCurrencyCode(), currency)){
                result.add(booking);
            }
        }
        return result;
    }
}
