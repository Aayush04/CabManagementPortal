package com.assignment.cabManagementPortal.service;

import com.assignment.cabManagementPortal.dao.DataService;
import com.assignment.cabManagementPortal.model.Booking;
import com.assignment.cabManagementPortal.model.BookingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CustomerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private QueueManager queueManager;

    @Resource
    private DataService dataService;


    public Booking book(Integer userId, String source, String destination) {
        logger.info("received new booking for userId: {}, source: {}, destination: {}", userId, source, destination);
        Booking booking = Booking.builder()
                .userId(userId)
                .source(source)
                .destination(destination)
                .startTime(System.currentTimeMillis())
                .state(BookingState.PENDING)
                .build();

        dataService.addBooking(booking);
        queueManager.addBooking(source, booking);
        return booking;
    }

}
