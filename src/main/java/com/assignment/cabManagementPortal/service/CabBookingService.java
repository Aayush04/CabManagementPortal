package com.assignment.cabManagementPortal.service;

import com.assignment.cabManagementPortal.dao.DataService;
import com.assignment.cabManagementPortal.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class CabBookingService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private DataService dataService;

    private Map<String, Queue<Cab>> cityVsCabQueue = new HashMap<>();

    public Cab addNewCab(String city) {

        Cab cab = Cab.builder()
//                .id(cabId)
                .state(CabState.NOT_IN_SERVICE)
                .location(city)
                .build();
        dataService.addCab(cab);

        CabHistory newCabHistory = CabHistory.builder()
                .cabId(cab.getId())
                .startTime(System.currentTimeMillis())
                .state(CabState.NOT_IN_SERVICE)
                .build();
        dataService.addCabHistory(newCabHistory);

        return cab;
    }

    public void changeCabState(Integer cabId, CabState cabState, Integer cityId, Integer bookingId) {

        Cab cab = dataService.getCab(cabId);
        if(cab == null) {
            throw new RuntimeException("cab not found");
        }

        switch (cabState) {
            case NOT_IN_SERVICE:
                handleCabNotIsService(cab);
                break;
            case IDLE:
                City city = dataService.getCity(cityId);
                if(city == null) {
                    throw new RuntimeException("city not found");
                }

                handleCabIdle(cab, city.getName());
                break;
            case ON_TRIP:
                handleCabOnTrip(cab, bookingId);
                break;
        }
    }

    private void addCabToQueue(Cab cab) {

        if(!cityVsCabQueue.containsKey(cab.getLocation())) {
            cityVsCabQueue.put(cab.getLocation(), new LinkedList<>());
        }

        cityVsCabQueue.get(cab.getLocation()).add(cab);
    }

    private void removeCabFromQueue(Cab cabToRemove) {
        if(cityVsCabQueue.containsKey(cabToRemove.getLocation()) ) {
            Iterator<Cab> it = cityVsCabQueue.get(cabToRemove.getLocation()).iterator();
            while(it.hasNext()) {
                Cab cab = it.next();
                if(cab.getId().equals(cabToRemove.getId())) {
                    it.remove();
                    break;
                }
            }
        }
    }

    private void handleCabOnTrip(Cab cab, Integer bookingId ) {

        CabHistory cabHistory = dataService.getLastCabHistory(cab.getId());

        if(cabHistory == null) {
            throw new RuntimeException("no previous history found for cab " + cab.getId());
        }

        if(cabHistory.getState() != CabState.IDLE ) {
            throw new RuntimeException("invalid new state " + CabState.ON_TRIP + " for cab " + cab.getId());
        }

        long currTime = System.currentTimeMillis();
        cabHistory.setEndTime(currTime);
        dataService.updateCabHistory(cabHistory);

        dataService.updateCab(cab);
        CabHistory newCabHistory = CabHistory.builder()
                .cabId(cab.getId())
                .startTime(currTime)
                .state(CabState.ON_TRIP)
                .bookingId(bookingId)
                .build();
        dataService.addCabHistory(newCabHistory);
    }

    private void handleCabIdle(Cab cab, String city) {

        CabHistory cabHistory = dataService.getLastCabHistory(cab.getId());

        if(cabHistory == null) {
            throw new RuntimeException("no previous history found for cab " + cab.getId());
        }

        if(cabHistory.getState() != CabState.NOT_IN_SERVICE || cabHistory.getState() != CabState.ON_TRIP ) {
            throw new RuntimeException("invalid new state " + CabState.IDLE + " for cab " + cab.getId());
        }

        long currTime = System.currentTimeMillis();
        cabHistory.setEndTime(currTime);
        dataService.updateCabHistory(cabHistory);

        cab.setState(CabState.IDLE);
        cab.setLocation(city);
        dataService.updateCab(cab);

        addCabToQueue(cab);

        CabHistory newCabHistory = CabHistory.builder()
                .cabId(cab.getId())
                .startTime(currTime)
                .state(CabState.IDLE)
                .build();
        dataService.addCabHistory(newCabHistory);
    }

    private void handleCabNotIsService(Cab cab) {

        CabHistory cabHistory = dataService.getLastCabHistory(cab.getId());

        if(cabHistory == null) {
            throw new RuntimeException("no previous history found for cab " + cab.getId());
        }

        if(cabHistory.getState() != CabState.IDLE ) {
            throw new RuntimeException("invalid new state " + CabState.NOT_IN_SERVICE + " for cab " + cab.getId());
        }

        long currTime = System.currentTimeMillis();
        cabHistory.setEndTime(currTime);
        dataService.updateCabHistory(cabHistory);

        cab.setState(CabState.NOT_IN_SERVICE);
        dataService.updateCab(cab);

        removeCabFromQueue(cab);

        CabHistory newCabHistory = CabHistory.builder()
                .cabId(cab.getId())
                .startTime(currTime)
                .state(CabState.NOT_IN_SERVICE)
                .build();
        dataService.addCabHistory(newCabHistory);
    }


    public Cab getNextAvailableCab(String city) {

        if(!cityVsCabQueue.containsKey(city)) {
            throw new RuntimeException("sorry no cabs are available");
        }

        Cab cab = cityVsCabQueue.get(city).poll();
        if(cab == null) {
            throw new RuntimeException("sorry no cabs are available");
        }

        return cab;
    }

    public void handleBooking(Booking booking) {
        logger.info("handling booking: {}", booking);

        Cab cab = getNextAvailableCab(booking.getSource());
        cab.setState(CabState.ON_TRIP);
        handleCabOnTrip(cab, booking.getId());
    }

    public void handleBookingCompleted(Integer bookingId) {
        logger.info("handling booking completed for bookingId: {}", bookingId);
        Booking booking = dataService.getBooking(bookingId);

        if(booking == null) {
            throw new RuntimeException("booking not found");
        }

        Integer cabId = booking.getCabId();
        Cab cab = dataService.getCab(cabId);

        if(cab == null) {
            throw new RuntimeException("cab not found");
        }

        handleCabIdle(cab, booking.getDestination());
        booking.setEndTime(System.currentTimeMillis());

        dataService.updateBooking(booking);

    }

}
