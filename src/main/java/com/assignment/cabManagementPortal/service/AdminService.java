package com.assignment.cabManagementPortal.service;

import com.assignment.cabManagementPortal.dao.DataService;
import com.assignment.cabManagementPortal.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private DataService dataService;

    public CabInsight getCabInsights(Integer cabId, long startTime, long endTime) {
        List<CabHistory> cabHistoryList = dataService.getAllCabHistory(cabId);
        long idleTime = 0;
        long onTripTime = 0;
        long noServiceTime = 0;
        for (CabHistory history : cabHistoryList ) {

            long diff = Math.min( history.getEndTime(), endTime) - Math.max( history.getStartTime(), startTime);
            diff = diff > 0 ? diff : 0;

            switch (history.getState()) {
                case NOT_IN_SERVICE:
                    noServiceTime += diff;
                    break;
                case IDLE:
                    noServiceTime += diff;
                    break;
                case ON_TRIP:
                    onTripTime += diff;
                    break;
            }
        }

        CabInsight cabInsight = new CabInsight();
        cabInsight.setCabId(cabId);
        cabInsight.setIdleTime(idleTime);
        cabInsight.setOnTripTime(onTripTime);

        System.out.println("============ CabInsight =============");
        System.out.println("CabId: " + cabId);
        System.out.println("OnTripTime: " + onTripTime/1000);
        System.out.println("IdleTime: " + idleTime/1000);
        return cabInsight;
    }

    public List<CabHistory> getCabHistory(Integer cabId) {

        List<CabHistory> historyList = dataService.getAllCabHistory(cabId);
        Iterator<CabHistory> it = historyList.iterator();

        System.out.println("============ Cab: " + cabId + " CabHistory =============");
        while(it.hasNext()) {
            System.out.println(it.next());
        }
        return historyList;
    }

    public Demand getCabDemandData() {
        Map<Integer, Booking> bookingMap = dataService.getAllBooking();
        Iterator<Booking> it = bookingMap.values().iterator();

        Map<String, Integer> cityVsCount = new HashMap<>();
        Map<Long, Integer> timeVsCount = new HashMap<>();

        int maxCityCount = 0;
        int maxTimeCount = 0;
        while(it.hasNext()) {
            Booking booking = it.next();

            String cityName = booking.getSource();
            if(!cityVsCount.containsKey(cityName)) {
                cityVsCount.put(cityName, 0);
            }

            cityVsCount.put(cityName, cityVsCount.get(cityName)+1);

            maxCityCount = Math.max(maxCityCount, cityVsCount.get(cityName));

            long startTime = booking.getStartTime();
            if(!timeVsCount.containsKey(startTime)) {
                timeVsCount.put(startTime, 0);
            }
            timeVsCount.put(startTime, timeVsCount.get(startTime)+1);

            maxCityCount = Math.max(maxTimeCount, timeVsCount.get(startTime));
        }

        System.out.println("============ Demand Insight =============");

        for (Map.Entry<String, Integer> entry : cityVsCount.entrySet()) {
            if(entry.getValue().equals(maxCityCount)) {
                System.out.println("City: " + entry.getKey() + " Count: " + maxCityCount);
            }
        }

        for (Map.Entry<Long, Integer> entry : timeVsCount.entrySet()) {
            if(entry.getValue().equals(maxTimeCount)) {
                System.out.println("StartTime: " + entry.getKey()/1000 + " Count: " + maxTimeCount);
            }
        }

        Demand demand = new Demand();
        demand.setCityVsCount(cityVsCount);
        demand.setTimeVsCount(timeVsCount);
        return demand;
    }

    public void addCity(String name) {
        logger.info("adding new city with name: {} ", name);
        City city = dataService.getCity(name);
        if(city != null) {
            throw new RuntimeException("city already exist");
        }
        City newCity = City.builder()
                .name(name)
                .build();
        dataService.addCity(newCity);
    }
}
