package hotel.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;

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


@ExtendWith(MockitoExtension.class)
class HotelTest {


    @Mock Room mRoom;
    @Mock Guest mGuest;
    Date arrivalDate;
    int stayLength;
    int occupantNumber;
    @Mock CreditCard mCard;
    @Spy Map<Long, Booking> bookingsByConfirmationNumber = new HashMap<Long, Booking>();
    SimpleDateFormat format;
    int roomId;
    long confirmationNumber;
    @Mock Booking mBooking;
    @Spy Map<Integer, Booking> activeBookingsByRoomId = new HashMap<Integer, Booking>();



    @BeforeEach
    void setUp() {
        format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            arrivalDate = format.parse("11-02-2018");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        stayLength = 1;
        occupantNumber = 1;
        confirmationNumber = 11022018101L;
        roomId = 101;
    }


    @AfterEach
    void tearDown() {
    }


    @InjectMocks Hotel hotel;


    @Test
    void bookCheckOutput() {
        //arrange
        when(mRoom.book(mGuest, arrivalDate, stayLength, occupantNumber, mCard)).thenReturn(mBooking);
        when(mBooking.getConfirmationNumber()).thenReturn(confirmationNumber);
        assertEquals(0, bookingsByConfirmationNumber.size());

        //act
        long expected = hotel.book(mRoom, mGuest, arrivalDate, stayLength, occupantNumber, mCard);

        //assert
        verify(mRoom).book(mGuest, arrivalDate, stayLength, occupantNumber, mCard);
        assertEquals(confirmationNumber, expected);
        assertEquals(1, bookingsByConfirmationNumber.size());
        assertEquals(mBooking, hotel.findBookingByConfirmationNumber(expected));
    }


    @Test
    void checkinIdealUseCase() {
        //arrange
        when(hotel.bookingsByConfirmationNumber.get(confirmationNumber)).thenReturn(mBooking);
        when(mBooking.getRoomId()).thenReturn(roomId);
        assertEquals(0, activeBookingsByRoomId.size());
        //act
        hotel.checkin(confirmationNumber);
        //assert
        assertEquals(1, activeBookingsByRoomId.size());
    }

    @Test
    void checkinNullBookingException() {
        //arrange
        when(hotel.bookingsByConfirmationNumber.get(confirmationNumber)).thenReturn(null);
        assertEquals(0, activeBookingsByRoomId.size());
        //act
        Executable e = () -> hotel.checkin(confirmationNumber);
        Throwable t = assertThrows(RuntimeException.class, e);
        //assert
        assertEquals(0, activeBookingsByRoomId.size());
        assertEquals("Hotel.checkin(): Booking does not exist for confirmation number " + confirmationNumber, t.getMessage());
    }


    @Test
    void addServiceChargeIdealUseCase() {
        //arrange
        ServiceType serviceType = ServiceType.ROOM_SERVICE;
        double cost = 1D;
        when(hotel.activeBookingsByRoomId.get(roomId)).thenReturn(mBooking);
        //act
        hotel.addServiceCharge(roomId, serviceType, cost);
        //assert
        verify(mBooking).addServiceCharge(serviceType, cost);
    }

    @Test
    void addServiceNullBooking() {
        //arrange
        ServiceType serviceType = ServiceType.ROOM_SERVICE;
        double cost = 1D;
        when(activeBookingsByRoomId.get(roomId)).thenReturn(null);
        //act
        Executable e = () -> hotel.addServiceCharge(roomId, serviceType, cost);
        Throwable t = assertThrows(RuntimeException.class, e);
        //assert
        assertEquals("Hotel.addServiceCharge(): Booking does not exist for room id " + roomId, t.getMessage());
    }


    @Test
    void checkoutIdealUseCase() {
        //arrange
        when(activeBookingsByRoomId.get(roomId)).thenReturn(mBooking);
        //act
        hotel.checkout(roomId);
        //assert
        verify(mBooking).checkOut();
    }


    @Test
    void checkoutNullBooking() {
        //arrange
        when(activeBookingsByRoomId.get(roomId)).thenReturn(null);
        //act
        Executable e = () -> hotel.checkout(roomId);
        Throwable t = assertThrows(RuntimeException.class, e);
        //assert
        assertEquals("Hotel.checkout(): Booking does not exist for room id " + roomId, t.getMessage());
    }
}