package com.statista.code.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.statista.code.challenge.entity.Booking;
import com.statista.code.challenge.entity.BookingHumanReadableDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FooBarControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    int randomServerPort;

    @Before
    public void init() throws URISyntaxException {
        setupTestData();
    }

    @Test
    public void testCreateBooking() throws URISyntaxException {
        String baseUrl = "http://localhost:"+randomServerPort+"/bookingservice/bookings";
        URI uri = new URI(baseUrl);
        Booking booking = new Booking("Cool description!", 50.00, Currency.getInstance("USD"),
                683124845000L, "valid@email.ok", "cool department");
        HttpEntity<Booking> request = new HttpEntity<>(booking);
        ResponseEntity<String> response = this.testRestTemplate.postForEntity(uri, request, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateBooking() throws URISyntaxException {
        String baseUrl = "http://localhost:"+randomServerPort+"/bookingservice/bookings";
        URI uri = new URI(baseUrl);
        Booking booking = new Booking("Cool description!", 50.00, Currency.getInstance("USD"),
                683124845000L, "valid@email.ok", "cool department");
        HttpEntity<Booking> request = new HttpEntity<>(booking);
        ResponseEntity<String> response = this.testRestTemplate.postForEntity(uri, request, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        booking.setId(response.getBody());
        booking.setCurrency(Currency.getInstance("EUR"));

        baseUrl = baseUrl + "/"+booking.getId();
        URI putUri = new URI(baseUrl);
        HttpEntity<Booking> putRequest = new HttpEntity<>(booking);
        this.testRestTemplate.put(putUri, putRequest);
        ResponseEntity<Booking> bookingResponse = this.testRestTemplate.getForEntity(putUri, Booking.class);
        assertEquals(HttpStatus.OK, bookingResponse.getStatusCode());
        assertEquals(Currency.getInstance("EUR"), bookingResponse.getBody().getCurrency());
    }

    @Test
    public void testRetrieveBooking() throws URISyntaxException {
        String baseUrl = "http://localhost:"+randomServerPort+"/bookingservice/bookings";
        URI uri = new URI(baseUrl);
        Booking booking = new Booking("Cool description!", 50.00, Currency.getInstance("USD"),
                683124845000L, "valid@email.ok", "cool department");
        HttpEntity<Booking> request = new HttpEntity<>(booking);
        ResponseEntity<String> response = this.testRestTemplate.postForEntity(uri, request, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        baseUrl = baseUrl + "/"+response.getBody();
        URI getUri = new URI(baseUrl);
        ResponseEntity<Booking> bookingResponse = this.testRestTemplate.getForEntity(getUri, Booking.class);
        assertEquals(HttpStatus.OK, bookingResponse.getStatusCode());
        assertEquals(Currency.getInstance("USD"), bookingResponse.getBody().getCurrency());
        assertEquals(booking.getDepartment(), bookingResponse.getBody().getDepartment());
    }

    @Test
    public void testRetrieveByDepartment() throws URISyntaxException {
        String baseUrl = "http://localhost:"+randomServerPort+"/bookingservice/bookings/department/internal";
        URI departmentUri = new URI(baseUrl);
        ResponseEntity<Booking[]> response = this.testRestTemplate.getForEntity(departmentUri, Booking[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        Booking booking = response.getBody()[0];
        assertEquals("internal", booking.getDepartment());
    }

    @Test
    public void testRetrieveCurrenciesUsed() throws URISyntaxException {
        String baseUrl = "http://localhost:"+randomServerPort+"/bookingservice/bookings/currencies";
        URI currencyUri = new URI(baseUrl);
        ResponseEntity<Set> response = this.testRestTemplate.getForEntity(currencyUri, Set.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Set responseSet = response.getBody();
        assertEquals(1, responseSet.size());
        assertEquals("USD", responseSet.stream().findFirst().get());
    }

    @Test
    public void testSumCurrencies() throws URISyntaxException {
        String baseUrl = "http://localhost:"+randomServerPort+"/bookingservice/sum/USD";
        URI sumUri = new URI(baseUrl);
        ResponseEntity<Double> response = this.testRestTemplate.getForEntity(sumUri, Double.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100.00, response.getBody().doubleValue(), 0.001);
    }

    @Test
    public void testDoBusinessSales() throws URISyntaxException {
        setupTestData();
        String baseUrl = "http://localhost:"+randomServerPort+"/bookingservice/bookings/dobusiness/sales";
        URI salesUri = new URI(baseUrl);
        ResponseEntity<Map> response = this.testRestTemplate.getForEntity(salesUri, Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(50.00, response.getBody().get("USD"));
    }

    @Test
    public void testDoBusinessInternal() throws URISyntaxException {
        String baseUrl = "http://localhost:"+randomServerPort+"/bookingservice/bookings/dobusiness/internal";
        URI internalUri = new URI(baseUrl);
        ResponseEntity<List> response = this.testRestTemplate.getForEntity(internalUri, List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        ObjectMapper objectMapper = new ObjectMapper();
        BookingHumanReadableDate bookingHumanReadableDate =
                objectMapper.convertValue(response.getBody().get(0), BookingHumanReadableDate.class);
        assertEquals("25.08.1991, 14:54:05", bookingHumanReadableDate.getHumanReadableSubscriptionStartDate());
    }

    @Test
    public void testDoBusinessDepartmentWithNoVersionOfMethod() throws URISyntaxException {
        String baseUrl = "http://localhost:"+randomServerPort+"/bookingservice/bookings/dobusiness/test";
        URI internalUri = new URI(baseUrl);
        ResponseEntity response = this.testRestTemplate.getForEntity(internalUri, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private void setupTestData() throws URISyntaxException {
        String baseUrl = "http://localhost:"+randomServerPort+"/bookingservice/bookings";
        URI uri = new URI(baseUrl);
        Booking booking1 = new Booking("Cool description!", 50.00, Currency.getInstance("USD"),
                683124845000L, "valid@email.ok", "internal");
        Booking booking2 = new Booking("Cool description!", 50.00, Currency.getInstance("USD"),
                683124845000L, "valid@email.ok", "sales");
        HttpEntity<Booking> request1 = new HttpEntity<>(booking1);
        HttpEntity<Booking> request2 = new HttpEntity<>(booking2);
        this.testRestTemplate.postForEntity(uri, request1, String.class);
        this.testRestTemplate.postForEntity(uri, request2, String.class);
    }
}
