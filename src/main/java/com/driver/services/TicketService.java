package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.Exceptions.PassengerNotFoundException;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    TrainService trainService;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db

        List<Passenger> passengers=new ArrayList<>();
        for(int passengerId:bookTicketEntryDto.getPassengerIds()){
            Optional<Passenger> optionalPassenger=passengerRepository.findById(passengerId);
            if(!optionalPassenger.isPresent()){
                throw new PassengerNotFoundException("Passenger with given Id Not found");
            }
            Passenger passenger=optionalPassenger.get();
            passengers.add(passenger);
        }
        Passenger bookingPerson=passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();
        int bookingSeat=passengers.size();
        Train train=trainRepository.findById(bookTicketEntryDto.getTrainId()).get();

        SeatAvailabilityEntryDto seatAvailabilityEntryDto=new SeatAvailabilityEntryDto();
        seatAvailabilityEntryDto.setTrainId(train.getTrainId());
        seatAvailabilityEntryDto.setFromStation(bookTicketEntryDto.getFromStation());
        seatAvailabilityEntryDto.setToStation(bookTicketEntryDto.getToStation());
        int availableSeats=0;
        try{

         availableSeats=trainService.calculateAvailableSeats(seatAvailabilityEntryDto);
        }catch (Exception e){
            throw e;
        }

        if(bookingSeat>availableSeats){
            throw new Exception("Less tickets are available");
        }
        int totalFare=0;
        String fromStation=bookTicketEntryDto.getFromStation().toString();
        String toStation=bookTicketEntryDto.getToStation().toString();
        int indexOfFromStation=-1,indexOfToStation=-1;

        String[] route=train.getRoute().split(" ");

        for(int i=0;i<route.length;i++){
            if(fromStation.equals(route[i])){
                indexOfFromStation=i;
            }
            if(toStation.equals(route[i])){
                indexOfToStation=i;
            }
        }
        if(indexOfToStation==-1||indexOfFromStation==-1||indexOfToStation<=indexOfFromStation){
            throw new Exception("Invalid stations");
//            return 0;
        }

        totalFare=(indexOfToStation-indexOfFromStation)*300;
        // creating ticket object
        Ticket ticket=new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTotalFare(totalFare);

        // now establish relation between the tables

        // making relation between ticket in passenger

                //bookingPerson.getBookedTickets().add(ticket);
        for(Passenger passenger:passengers) {
            ticket.getPassengersList().add(passenger);
            passenger.getBookedTickets().add(ticket);
        }




        // making relation between ticket and train
        train.getBookedTickets().add(ticket);
        ticket.setTrain(train);


        Ticket savedTicket=ticketRepository.save(ticket);




       return savedTicket.getTicketId();

    }
}
