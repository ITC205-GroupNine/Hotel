package hotel.entities;

//import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.function.Executable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class RoomTest {
    
    @Mock Booking booking;
    @Spy ArrayList<Booking> bookings;
    
    int roomId = 1;
    RoomType roomType = RoomType.SINGLE;
    
    @InjectMocks Room room = new Room(roomId, roomType);
    
    @Test
    void testCheckInWhenReady(){
        //arrange
        //act
        room.checkIn();
        //assert
        assertTrue(room.isOccupied());
    }


    @Test
    void testCheckInWhenOccupied(){

        //arrange
        room.checkIn();
        assertTrue(room.isOccupied());
        //act
        Executable e = () -> room.checkIn();
        Throwable t = assertThrows(RuntimeException.class, e);
        //assert
        assertEquals("The Room is not ready to be checked in must be in a READY state", t.getMessage());
    }

    @Test
    void testCheckOutWhenOccupied(){
        //arrange
        bookings.add(booking);
        room.checkIn();
        assertEquals(1, bookings.size());
        assertTrue(room.isOccupied());
        //act
        room.checkOut(booking);
        //assert
        verify(bookings).remove(booking);
        assertTrue(room.isReady());
        assertEquals(0, bookings.size());
   
    }
    
}
