package com.assignment.cabManagementPortal.service;

import com.assignment.cabManagementPortal.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * This class will act as a message broker
 */
@Service
public class QueueManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private CabBookingService bookingConsumer;

    private Map<String, Queue<Booking>> cityVsQueue = new HashMap<>();
    private Map<String, Thread> cityVsQueueReader = new HashMap<>();


    public void addBooking(String city, Booking booking) {
        logger.info("adding new booking: {} for city: {}", booking, city);
        if(!cityVsQueue.containsKey(city)) {
            cityVsQueue.put(city, new LinkedList<>());
            Thread worker = getNewWorker(city);
            cityVsQueueReader.put(city, worker);
        }

        cityVsQueue.get(city).add(booking);
    }

    private void processBooking(Booking booking ) {
        bookingConsumer.handleBooking(booking);
    }

    private Thread getNewWorker(String city) {
        String name = "worker-" + city;
        Thread worker = new Thread(() -> {
            logger.info("started worker: {}", name);
            Queue<Booking> bookingQueue = cityVsQueue.get(city);
            while(true) {
                Booking nextBooking = bookingQueue.poll();
                if(nextBooking != null) {
                    processBooking(nextBooking);
                }
            }
        }, name);

        worker.start();
        return worker;
    }

}
