package hotel.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import hotel.credit.CreditCardType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import hotel.credit.CreditCard;

//Hotel-Booking-Room
@ExtendWith(MockitoExtension.class)
public class HotelIntegrationTest {


    Hotel hotel;
    Room room;
    Booking booking;
    Guest guest;
    CreditCard card = new CreditCard(CreditCardType.VISA, 7, 7);
    Date arrivalDate;
    int stayLength;
    int occupantNumber;
    SimpleDateFormat sdf;
    int roomId;
    long confirmationNumber;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        Calendar myCalendar = new GregorianCalendar(2018, 11, 11);
        arrivalDate = myCalendar.getTime();
        stayLength = 1;
        occupantNumber = 1;
        confirmationNumber = 11122018101L;
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
        booking = room.book(guest, arrivalDate, stayLength, occupantNumber, card);

        //assert
        assertTrue(booking != null);
    }

    @Test
    void checkinIdealUseCaseRealObjects() {
        //arrange
        booking = room.book(guest, arrivalDate, stayLength, occupantNumber, card);
        hotel.bookingsByConfirmationNumber.put(booking.getConfirmationNumber(), booking);
        //act
        hotel.checkin(booking.getConfirmationNumber());
        //assert
        assertEquals(1, hotel.activeBookingsByRoomId.size());
        assertEquals(Booking.State.CHECKED_IN ,booking.getState());//added for getState() testing and change Booking.State
        assertEquals(Room.State.OCCUPIED, room.getState());
    }

    @Test
    void addServiceChargeIdealUseCaseRealObjects() {
        //arrange
        ServiceType serviceType = ServiceType.ROOM_SERVICE;
        double cost = 1D;
        booking = room.book(guest, arrivalDate, stayLength, occupantNumber, card);
        hotel.activeBookingsByRoomId.put(roomId, booking);
        hotel.bookingsByConfirmationNumber.put(booking.getConfirmationNumber(), booking);
        hotel.checkin(booking.getConfirmationNumber());
        //act
        hotel.addServiceCharge(roomId, serviceType, cost);
        //assert
        assertEquals(1, booking.getCharges().size());
        assertEquals(cost, booking.getCharges().get(0).getCost());
    }

    @Test
    void checkoutIdealUseCaseRealObjects() {
        //arrange
        booking = room.book(guest, arrivalDate, stayLength, occupantNumber, card);
        hotel.bookingsByConfirmationNumber.put(confirmationNumber, booking);
        hotel.checkin(confirmationNumber);
        assertEquals(1, hotel.activeBookingsByRoomId.size());
        assertEquals(Room.State.OCCUPIED, room.getState());// added getState and changed Room.State enum to public for testing
        assertEquals(Booking.State.CHECKED_IN ,booking.getState());//added for getState() testing and change Booking.State
        //act
        hotel.checkout(roomId);
        //assert
        assertEquals(Booking.State.CHECKED_OUT ,booking.getState());//added for getState() testing and change Booking.State
        assertEquals(Room.State.READY, room.getState()); // added getState and changed Room.State enum to public for testing
    }
}

