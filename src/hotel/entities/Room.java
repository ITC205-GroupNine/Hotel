package hotel.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hotel.credit.CreditCard;
import hotel.utils.IOUtils;

public class Room {
    
    private enum State {READY, OCCUPIED, PENDING}
    
    private int id;
    private RoomType roomType;
    private List<Booking> bookings;
    private State state;
    
    
    Room(int id, RoomType roomType) {
        this.id = id;
        this.roomType = roomType;
        bookings = new ArrayList<>();
        state = State.READY;
    }
    
    
    public String toString() {
        return String.format("Room : %d, %s", id, roomType);
    }
    
    
    public int getId() {
        return id;
    }
    
    
    public String getDescription() {
        return roomType.getDescription();
    }
    
    
    public RoomType getType() {
        return roomType;
    }
    
    
    boolean isAvailable(Date arrivalDate, int stayLength) {
        IOUtils.trace("Room: isAvailable");
        for (Booking b : bookings) {
            if (b.doTimesConflict(arrivalDate, stayLength)) {
                return false;
            }
        }
        return true;
    }
    
    
    public boolean isReady() {
        return state == State.READY;
    }
    
    
    public Booking book(Guest guest, Date arrivalDate, int stayLength, int numberOfOccupants, CreditCard creditCard) {
        
        Boolean availability = isAvailable(arrivalDate, stayLength);
        
        if (availability) {
            Booking booking = new Booking(guest, this, arrivalDate, stayLength, numberOfOccupants, creditCard);
            state = State.PENDING;
            return booking;
        } else {
            
            return null;
        }
    }
    
    
    public void checkIn() {
        
        if (state != State.READY) {
            throw new RuntimeException("The Room is not ready to be checked in must be in a READY state");
        } else {
            state = State.OCCUPIED;
            //booking.checkIn();
        }
    }
    
    
    public void checkout(Booking booking) {
        
        if (state != State.OCCUPIED) {
            throw new RuntimeException("Checkout cannot be processed unless in the OCCUPIED State");
        } else {
            state = State.READY;
            booking.checkOut();
            
        }
    }
}
