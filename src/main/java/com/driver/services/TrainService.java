package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.EntryDto.TimeRangeDto;
import com.driver.Transformer.TrainTransfomer;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto){

        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library
        Train train= TrainTransfomer.addTrainEntryDtoToTrain(trainEntryDto);
        train=trainRepository.save(train);
        return train.getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto) throws Exception {

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats avaialble in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.

        Train train=trainRepository.findById(seatAvailabilityEntryDto.getTrainId()).get();
        int indexOfFromStation=-1,indexOfToStation=-1;

        Station fromStation=seatAvailabilityEntryDto.getFromStation();
        Station toStation=seatAvailabilityEntryDto.getToStation();
        String[] route=train.getRoute().split(" ");
        for(int i=0;i<route.length;i++){
            if(route[i].equals(fromStation.toString())){
                indexOfFromStation=i;
            }
            if(route[i].equals(toStation.toString())){
                indexOfToStation=i;
            }
        }
        if(indexOfToStation==-1||indexOfFromStation==-1||indexOfToStation<=indexOfFromStation){
            throw new Exception("Invalid stations");
        }
        //
        int count=train.getNoOfSeats();
        for(Ticket ticket: train.getBookedTickets()){
            int boardingIndex=getIndexOfStation(route,ticket.getFromStation());
            int destIndex=getIndexOfStation(route,ticket.getToStation());
            if(boardingIndex>=indexOfFromStation && boardingIndex<indexOfToStation
                    || destIndex>indexOfFromStation && destIndex<=indexOfToStation){
                count--;
            }
        }
        return count;

    }
    private int getIndexOfStation(String[] route,Station station){
        for(int i=0;i<route.length;i++){
            if(route[i].equals(station.toString())){
                return i;
            }
        }
        return 0;
    }
    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.

        Train train=trainRepository.findById(trainId).get();
        String[] route=train.getRoute().split(" ");
        boolean stationFound=false;
        for(String routeStation:route){
            if(routeStation.equals(station.toString())){
                stationFound=true;
            }
        }
        if(!stationFound){
            throw new Exception("Train is not passing from this station");
        }
        int count=0;
        for(Ticket ticket:train.getBookedTickets()){
            if(ticket.getFromStation().equals(station)){
                count+=ticket.getPassengersList().size();
            }
        }

        return count;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId){

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0
        int age=0;
        Train train=trainRepository.findById(trainId).get();
        if(train.getBookedTickets().size()==0){
            return 0;
        }
        // may have to change accordingly because of ManyToMany relation
        for(Ticket ticket:train.getBookedTickets()){
            for(Passenger passenger: ticket.getPassengersList()){
                if(passenger.getAge()>age){
                    age=passenger.getAge();
                }
            }
        }
        return age;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){


//        TimeRangeDto timeRangeDto
//        Station station=timeRangeDto.getStation();
//        LocalTime startTime=timeRangeDto.getStartTime();
//        LocalTime endTime=timeRangeDto.getEndTime();
        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.
        List<Train> trains=trainRepository.findAll();
        class Pair{
            public Train train;
            public LocalTime arrivalTime;
            public Pair(Train train,LocalTime arrivalTime){
                this.train=train;
                this.arrivalTime=arrivalTime;
            }
        }
        List<Pair> passingTrains=new ArrayList<>();
        for(Train train: trains){
            String[] route=train.getRoute().split(" ");
            for(int i=0;i<route.length;i++){
                if(route[i].equals(station.toString())){
                    LocalTime arrivalTime=train.getDepartureTime().plusHours(i);
                    passingTrains.add(new Pair(train,arrivalTime));
                }
            }
        }
        List<Integer> ans=new ArrayList<>();
        for(Pair train:passingTrains){
            if(train.arrivalTime.equals(startTime)||train.arrivalTime.equals(endTime)|| train.arrivalTime.isAfter(startTime) && train.arrivalTime.isBefore(endTime)){
                ans.add(train.train.getTrainId());
            }
        }
        return ans;
    }

}
