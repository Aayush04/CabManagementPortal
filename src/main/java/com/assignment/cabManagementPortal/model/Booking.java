package com.assignment.cabManagementPortal.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Booking {
    private Integer id;
    private Integer userId;
    private String source;
    private String destination;
    private long startTime;
    private long endTime;
    private BookingState state;
    private Integer cabId;
}
