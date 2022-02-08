package com.assignment.cabManagementPortal.service;

import com.assignment.cabManagementPortal.model.Cab;
import com.assignment.cabManagementPortal.model.CabState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DriverService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private CabBookingService cabManager;

    public Cab register(String location) {
        logger.info("registering new cab for city: {}", location);
        return cabManager.addNewCab(location);
    }

    public void changeLocation(Integer cabId, Integer cityId) {
        logger.info("changing location for cabId: {}, cityId: {}", cabId, cityId);
        cabManager.changeCabState(cabId, CabState.IDLE, cityId, null);
    }

    public void startService(Integer cabId, Integer cityId) {
        logger.info("starting service for cabId: {}, cityId: {}", cabId, cityId);
        cabManager.changeCabState(cabId, CabState.IDLE, cityId, null);
    }

    public void stopService(Integer cabId) {
        logger.info("stopping service for cabId: {}", cabId);
        cabManager.changeCabState(cabId, CabState.NOT_IN_SERVICE, null, null);
    }

    public void completeBooking(Integer bookingId) {
        logger.info("completing booking for bookingId: {}", bookingId);
        cabManager.handleBookingCompleted(bookingId);
    }
}
