package com.assignment.cabManagementPortal;

import com.assignment.cabManagementPortal.model.Booking;
import com.assignment.cabManagementPortal.model.BookingState;
import com.assignment.cabManagementPortal.model.Cab;
import com.assignment.cabManagementPortal.model.City;
import com.assignment.cabManagementPortal.service.AdminService;
import com.assignment.cabManagementPortal.service.CustomerService;
import com.assignment.cabManagementPortal.service.DriverService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ApplicationRunner {

    @Resource
    private AdminService adminService;

    @Resource
    private CustomerService customerService;

    @Resource
    private DriverService driverService;

    public void start() {
        long currTime = System.currentTimeMillis();
        City city1 = adminService.addCity("city-1");
        City city2 = adminService.addCity("city-2");
        City city3 = adminService.addCity("city-3");
        City city4 = adminService.addCity("city-4");
        City city5 = adminService.addCity("city-5");

        Cab cab1 =  driverService.register();
        Cab cab2 =  driverService.register();
        Cab cab3 =  driverService.register();
        Cab cab4 =  driverService.register();
        Cab cab5 =  driverService.register();

        driverService.startService(cab1.getId(), city1.getId());
        driverService.startService(cab2.getId(), city2.getId());
        driverService.startService(cab3.getId(), city3.getId());
        driverService.startService(cab4.getId(), city4.getId());
        driverService.startService(cab5.getId(), city5.getId());

        Thread t1 = new Thread(() -> {
            simulateBooking();
        });

        Thread t2 = new Thread(() -> {
            simulateBooking();
        });

        t1.start();
        t2.start();


        try {
            t1.join();
            t2.join();
        } catch (Exception e){
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        adminService.getCabDemandData();
        adminService.getCabHistory(cab1.getId());
        adminService.getCabHistory(cab2.getId());
        adminService.getCabInsights(cab1.getId(), currTime, endTime);
        adminService.getCabInsights(cab2.getId(), currTime, endTime);

    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void simulateBooking() {
        sleep(1000);
        Booking b1 = customerService.book(1, "city-1", "city-2");
        sleep(1000);
        Booking b2 = customerService.book(2, "city-2", "city-3");
        sleep(2000);
        completeBooking(b1);
        Booking b3 = customerService.book(3, "city-3", "city-4");
        sleep(1500);
        completeBooking(b2);
        completeBooking(b3);
        Booking b4 = customerService.book(4, "city-4", "city-1");
        sleep(3000);
        Booking b5 = customerService.book(5, "city-5", "city-2");
        sleep(500);
        completeBooking(b4);
        Booking b6 = customerService.book(6, "city-4", "city-1");
        sleep(1000);
        completeBooking(b5);
        Booking b7 = customerService.book(7, "city-3", "city-2");
        sleep(2000);
        completeBooking(b6);
        Booking b8 = customerService.book(8, "city-5", "city-2");
        completeBooking(b7);
        sleep(1000);
        completeBooking(b8);
    }

    private void completeBooking(Booking booking) {
        if(booking.getState() == BookingState.ON_GOING)
            driverService.completeBooking(booking.getId());
    }

}
