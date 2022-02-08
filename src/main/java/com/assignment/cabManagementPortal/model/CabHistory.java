package com.assignment.cabManagementPortal.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CabHistory {
    private Integer id;
    private Integer cabId;
    private long startTime;
    private long endTime;
    private CabState state;
    private Integer bookingId;
}
