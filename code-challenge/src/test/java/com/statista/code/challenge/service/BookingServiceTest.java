package com.statista.code.challenge.service;

import com.statista.code.challenge.entity.Booking;
import com.statista.code.challenge.entity.BookingHumanReadableDate;
import com.statista.code.challenge.repository.BookingRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    BookingService bookingService;

    Booking booking = new Booking("123abc","description", 50.00, Currency.getInstance("USD"),
            1675008444000L, "valid@email.ok", "test");

    @Test
    public void testCreateValidBooking(){
        Booking bookingAfterSaving = new Booking("123abc","description", 50.00,
                Currency.getInstance("USD"), 1675008444000L, "valid@email.ok",
                "test");
        when(bookingRepository.find(anyString())).thenReturn(null);
        when(bookingRepository.saveOrUpdate(booking)).thenReturn(bookingAfterSaving);
        ResponseEntity response = bookingService.createBooking(booking);
        assertEquals("123abc", response.getBody());
    }

    @Test
    public void testCreateBookingAlreadyExists(){
        when(bookingRepository.find(anyString())).thenReturn(booking);
        ResponseEntity response = bookingService.createBooking(booking);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testUpdateBooking(){
        when(bookingRepository.find(anyString())).thenReturn(booking);
        ResponseEntity response = bookingService.updateBooking("123abc", booking);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testUpdateBookingNoBookingPreviouslyCreated(){
        when(bookingRepository.find(anyString())).thenReturn(null);
        ResponseEntity response = bookingService.updateBooking("123abc", booking);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindBooking(){
        when(bookingRepository.find(anyString())).thenReturn(booking);
        ResponseEntity response = bookingService.find("123abc");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindBookingNullBookingId(){
        ResponseEntity response  = bookingService.find(null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testFindBookingNotFound(){
        when(bookingRepository.find("123abd")).thenReturn(null);
        ResponseEntity response = bookingService.find("123abd");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testFindBookingsByDepartment(){
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        when(bookingRepository.findByDepartment(anyString())).thenReturn(bookingList);
        ResponseEntity response = bookingService.findByDepartment("The Dept");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Booking> resultList = (List<Booking>) response.getBody();
        assertEquals(1, resultList.size());
    }

    @Test
    public void testFindBookingsByDepartmentForNoDepartment(){
        when(bookingRepository.findByDepartment(anyString())).thenReturn(new ArrayList());
        ResponseEntity response = bookingService.findByDepartment("The Dept");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRetrieveCurrenciesUsed(){
        Set<Currency> currencies = new HashSet<>();
        currencies.add(Currency.getInstance("USD"));
        currencies.add(Currency.getInstance("EUR"));
        when(bookingRepository.findCurrenciesUsed()).thenReturn(currencies);
        ResponseEntity response = bookingService.retrieveCurrencyUsed();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Set<Currency> result = (Set<Currency>) response.getBody();
        assertEquals(2, result.size());
        assertEquals(currencies, result);
    }

    @Test
    public void testRetrieveCurrenciesUsedNonePresent(){
        when(bookingRepository.findCurrenciesUsed()).thenReturn(new HashSet());
        ResponseEntity response = bookingService.retrieveCurrencyUsed();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAverageAllSalesByCurrency(){
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        bookings.add(booking);
        when(bookingRepository.findByDepartment(anyString())).thenReturn(bookings);
        ResponseEntity response = bookingService.averageAllSalesByCurrency("test");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HashMap<Currency, Double> result = (HashMap<Currency, Double>) response.getBody();
        assertTrue(result.containsKey(Currency.getInstance("USD")));
        assertEquals(Double.valueOf(50.00), result.get(Currency.getInstance("USD")));
    }

    @Test
    public void testConvertSubscriptionDateToHumanReadable(){
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(bookingRepository.findByDepartment(anyString())).thenReturn(bookings);
        ResponseEntity response = bookingService.convertSubscriptionStartDateToHumanReadable("internal");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<BookingHumanReadableDate> bookingHumanReadableDates = (List<BookingHumanReadableDate>) response.getBody();
        assertEquals(1, bookingHumanReadableDates.size());
        BookingHumanReadableDate bookingHumanReadableDate = bookingHumanReadableDates.get(0);
        assertEquals("29.01.2023, 17:07:24", bookingHumanReadableDate.getHumanReadableSubscriptionStartDate());
    }
}
