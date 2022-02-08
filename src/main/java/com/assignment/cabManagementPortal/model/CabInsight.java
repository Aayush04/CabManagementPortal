package com.assignment.cabManagementPortal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CabInsight {
    private Integer cabId;
    private long idleTime;
    private long onTripTime;
}
