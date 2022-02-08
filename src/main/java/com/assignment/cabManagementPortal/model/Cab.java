package com.assignment.cabManagementPortal.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Cab {
    private Integer id;
    private String location;
    private CabState state;
}
