package com.statista.code.challenge;
import com.statista.code.challenge.entity.Booking;
import com.statista.code.challenge.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@Validated
@RequestMapping("/bookingservice")
public class FooBarController {

    private final BookingService bookingService;

    public FooBarController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseEntity createBooking(@Valid @RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }
    @PutMapping("/bookings/{bookingId}")
    public ResponseEntity updateBooking(@PathVariable String bookingId, @RequestBody @Valid Booking booking) {
        return bookingService.updateBooking(bookingId, booking);
    }
    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity getBookingById(@PathVariable String bookingId) {
        return bookingService.find(bookingId);
    }
    @GetMapping("/bookings/department/{department}")
    public ResponseEntity getBookingByDepartment(@PathVariable String department) {
        return bookingService.findByDepartment(department);
    }
    @GetMapping("/bookings/currencies")
    public ResponseEntity getCurrencyUsed(){
        return bookingService.retrieveCurrencyUsed();
    }
    @GetMapping("/sum/{currency}")
    public ResponseEntity getSumByCurrency(@PathVariable String currency){
        return bookingService.retrieveSumByCurrency(currency);
    }

    @GetMapping("/bookings/dobusiness/{department}")
    public ResponseEntity doBusiness(@PathVariable String department){
        return bookingService.doBusiness(department);
    }
}