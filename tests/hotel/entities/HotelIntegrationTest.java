package hotel.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import hotel.credit.CreditCardType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hotel.credit.CreditCard;

//Hotel-Booking-Room
@ExtendWith(MockitoExtension.class)
public class HotelIntegrationTest {


    Hotel hotel;
    Room room;
    Booking booking;
    Guest guest;
    CreditCard card = new CreditCard(CreditCardType.VISA, 7, 7);
    @Mock
    CreditCard mCard;

    Date arrivalDate;
    int stayLength;
    int occupantNumber;
    SimpleDateFormat format;
    int roomId;
    long confirmationNumber;

    @Spy
    Map<Long, Booking> bookingsByConfirmationNumber = new HashMap<Long, Booking>();
    @Spy
    Map<Integer, Booking> activeBookingsByRoomId = new HashMap<Integer, Booking>();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            arrivalDate = format.parse("11-22-2018");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        stayLength = 1;
        occupantNumber = 1;
        confirmationNumber = 1192019101L;
        roomId = 101;
        hotel = new Hotel();
        room = new Room(roomId, RoomType.SINGLE);
        guest = new Guest("bob", "1 bob st", 11);
        //booking = new Booking(guest, room, arrivalDate, stayLength, occupantNumber, card);
    }


    @AfterEach
    void tearDown() {
    }


    @Test
    void bookCheckOutputRealObjects() {
        //arrange
        //act
        long expected = hotel.book(room, guest, arrivalDate, stayLength, occupantNumber, mCard);
        booking = room.book(guest, arrivalDate, stayLength, occupantNumber, card);

        //assert
        assertEquals(confirmationNumber, expected);
        assertEquals(1, hotel.bookingsByConfirmationNumber.size());
        assertEquals(expected, booking.getConfirmationNumber());
        assertTrue(booking != null);
    }

    @Test
    void checkinIdealUseCaseRealObjects() {
        //arrange
        booking = room.book(guest, arrivalDate, stayLength, occupantNumber, card);
        hotel.bookingsByConfirmationNumber.put(confirmationNumber, booking);
        //act
        hotel.checkin(confirmationNumber);
        //assert
        assertEquals(1, hotel.activeBookingsByRoomId.size());
    }
}

