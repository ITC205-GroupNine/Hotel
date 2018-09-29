package hotel.entities;

import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.function.Executable;

import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class RoomIntegrationTest {
    
    private Room room;
    private Booking booking;
    private CreditCard card = new CreditCard(CreditCardType.VISA, 7, 7);
    
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        
        Calendar myCalendar = new GregorianCalendar(2018, 11, 21);
        Date arrivalDate = myCalendar.getTime();
        int roomId = 101;
        room = new Room(roomId, RoomType.SINGLE);
        Guest guest = new Guest("bob", "1 bob st", 11);
        booking = new Booking(guest, room, arrivalDate,
                0, 0, card);
    }
    
    @Test
    void testCheckInRoomAvailable() {
        //arrange
        assertTrue(booking.isPending());
        assertTrue(room.isReady());
        
        //act
        booking.checkIn();
        
        //assert
        assertTrue(booking.isCheckedIn());
        assertFalse(room.isReady());
    }
    
    
    @Test
    void testCheckInRoomOccupied() {
        
        //arrange
        room.checkIn();
        assertTrue(booking.isPending());
        assertFalse(room.isReady());
        //act
        Executable e = () -> booking.checkIn();
        
        //assert
        Throwable t = assertThrows(RuntimeException.class, e);
        assertEquals("The Room is not ready " +
                "to be checked in must " +
                "be in a READY state", t.getMessage());
    }
    
    
    @Test
    void testCheckInBookingNotPending() {
        //arrange
        booking.checkIn();
        assertFalse(booking.isPending());
        assertFalse(room.isReady());
        //act
        Executable e = () -> booking.checkIn();
        //assert
        Throwable t = assertThrows(RuntimeException.class, e);
        assertEquals("Booking Entity cannot" +
                " call checkIn except in " +
                "PENDING State", t.getMessage());
    }
    
    
}
