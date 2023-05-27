package com.driver.EntryDto;

import com.driver.model.Station;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalTime;

public class TimeRangeDto {

    Station station;
    LocalTime startTime;
    LocalTime endTime;

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
