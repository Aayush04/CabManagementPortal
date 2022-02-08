package com.assignment.cabManagementPortal.dao;

import com.assignment.cabManagementPortal.model.Booking;
import com.assignment.cabManagementPortal.model.Cab;
import com.assignment.cabManagementPortal.model.CabHistory;
import com.assignment.cabManagementPortal.model.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class will act as an in memory database
 */
@Service
public class DataService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<Integer, Cab> cabIdVsCab = new HashMap<>();
    private Map<Integer, List<CabHistory>> cabIdVsCabHistory = new HashMap<>();
    private Map<Integer, Booking> bookingIdVsBooking = new HashMap<>();
    private Map<Integer, City> cityIdVsCity = new HashMap<>();

    private AtomicInteger cityIdGenerator = new AtomicInteger(1);
    private AtomicInteger cabHistoryIdGenerator = new AtomicInteger(1);
    private AtomicInteger bookingIdGenerator = new AtomicInteger(1);
    private AtomicInteger cabIdGenerator = new AtomicInteger(1);

    public void addCity(City city) {
        logger.info("adding city to database with city: {}", city);
        city.setId(cityIdGenerator.incrementAndGet());
        cityIdVsCity.put(city.getId(), city);
    }

    public City getCity(String name) {
        logger.info("getting city from database with name: {}", name);
        for(Map.Entry<Integer, City> cityEntry : cityIdVsCity.entrySet()) {
            if(cityEntry.getValue().equals(name))
                return cityEntry.getValue();
        }

        return null;
    }

    public City getCity(Integer cityId) {
        logger.info("getting city from database with id: {}", cityId);
        return cityIdVsCity.get(cityId);
    }



    public void addBooking(Booking booking) {
        logger.info("adding booking to database with booking: {}", booking);
        booking.setId(bookingIdGenerator.incrementAndGet());
        bookingIdVsBooking.put(booking.getId(), booking);
    }

    public Booking getBooking(Integer bookingId) {
        logger.info("getting booking from database with bookingId: {}", bookingId);
        return bookingIdVsBooking.get(bookingId);
    }

    public void updateBooking(Booking booking) {
        logger.info("updating booking to database with booking: {}", booking);
        bookingIdVsBooking.put(booking.getId(), booking);
    }



    public void addCab(Cab cab) {
        logger.info("adding cab to database with cab: {}", cab);
        cab.setId(cabIdGenerator.incrementAndGet());
        cabIdVsCab.put(cab.getId(), cab);
    }

    public Cab getCab(Integer cabId) {
        logger.info("getting cab from database with cabId: {}", cabId);
        return cabIdVsCab.get(cabId);
    }

    public void updateCab(Cab cab) {
        logger.info("updating cab to database with cab: {}", cab);
        cabIdVsCab.put(cab.getId(), cab);
    }



    public void addCabHistory(CabHistory cabHistory) {
        logger.info("adding cab history to database with cabHistory: {}", cabHistory);

        if(!cabIdVsCabHistory.containsKey(cabHistory.getCabId())) {
            cabIdVsCabHistory.put(cabHistory.getCabId(), new ArrayList<>());
        }

        cabHistory.setId(cabHistoryIdGenerator.incrementAndGet());
        cabIdVsCabHistory.get(cabHistory.getCabId()).add(cabHistory);
    }

    public CabHistory getLastCabHistory(Integer cabId) {
        logger.info("getting last cab history from database with cabId: {}", cabId);
        if(!cabIdVsCabHistory.containsKey(cabId)) {
            cabIdVsCabHistory.put(cabId, new ArrayList<>());
        }

        List<CabHistory> cabHistoryList = cabIdVsCabHistory.get(cabId);

        return cabHistoryList.get(cabHistoryList.size() - 1);
    }

    public List<CabHistory> getAllCabHistory(Integer cabId) {
        logger.info("getting all cab history from database with cabId: {}", cabId);
        if(!cabIdVsCabHistory.containsKey(cabId)) {
            cabIdVsCabHistory.put(cabId, new ArrayList<>());
        }

        List<CabHistory> cabHistoryList = cabIdVsCabHistory.get(cabId);

        return cabHistoryList;
    }

    public Map<Integer, List<CabHistory>> getAllCabHistory() {
        return cabIdVsCabHistory;
    }

    public Map<Integer, Booking> getAllBooking() {
        return bookingIdVsBooking;
    }

    public void updateCabHistory(CabHistory cabHistory) {
        logger.info("updating last cab history to database with cabHistory: {}", cabHistory);

        // no need to do anything as data is in memory
    }

}
