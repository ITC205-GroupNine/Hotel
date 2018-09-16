package hotel.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hotel.credit.CreditCard;
import org.mockito.runners.MockitoJUnitRunner;

@ExtendWith(MockitoExtension.class)
public class BookingTest {
    
    @Mock Hotel mockHotel;
    @Mock Guest mockGuest;
    @Mock CreditCard mockCreditCard;
    @Mock Booking mockBooking;
    @Spy Map<Long, Booking> bookingsConfirmationNumber = new HashMap<>();
    @Spy Map<Integer, Booking> activeBookingsByRoomId = new HashMap<>();
    
    Date arrivalDate;
    int stayLength;
    int numOccupants;
    SimpleDateFormat format;
    int roomId;
    long confirmationNumber;
    @Mock
    private Map<Integer, Guest> integerGuestMap;
    @Mock
    private Map<RoomType, Map<Integer, Room>> roomTypeMapMap;
    
    @BeforeEach
    void setUp() {
        format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            arrivalDate = format.parse("01-01-2018");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        stayLength = 1;
        numOccupants = 1;
        confirmationNumber = 11111111111L;
        roomId = 100;
    }
    @AfterEach
    void tearDown() {
    }
    
    
    @InjectMocks Hotel hotel;
    
    
    @Test
    void bookCheckOutput(){
    
    
    }
    
    
    
    
}
