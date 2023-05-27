package com.driver.Transformer;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.model.Station;
import com.driver.model.Train;

public class TrainTransfomer {
    public static Train addTrainEntryDtoToTrain(AddTrainEntryDto addTrainEntryDto){
        Train train=new Train();

        train.setNoOfSeats(addTrainEntryDto.getNoOfSeats());
        train.setDepartureTime(addTrainEntryDto.getDepartureTime());
        StringBuilder sb=new StringBuilder();
        for(Station station:addTrainEntryDto.getStationRoute()){
            sb.append(station.toString());
            sb.append(" ");
        }
        String route=sb.toString().trim();
        train.setRoute(route);
        return train;
    }
}
