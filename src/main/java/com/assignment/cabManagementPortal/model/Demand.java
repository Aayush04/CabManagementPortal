package com.assignment.cabManagementPortal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Demand {
    private Map<String, Integer> cityVsCount;
    private Map<Long, Integer> timeVsCount;
}
