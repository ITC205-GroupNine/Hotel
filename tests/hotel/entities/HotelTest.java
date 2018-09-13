package hotel.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import hotel.credit.CreditCard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


class HotelTest {


    @Mock Room mRoom;
    @Mock Guest mGuest;
    Date arrivalDate;
    int stayLength;
    int occupantNumber;
    @Mock CreditCard mCard;
    @Spy Map<Long, Booking> bookingsByConfirmationNumber;
    SimpleDateFormat format;

    @Mock Booking mBooking;
    long confirmationNumber;

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
    }

    @AfterEach
    void tearDown() {
    }

    @InjectMocks Hotel hotel;

    @Test
    void book() {
        //arrange
        bookingsByConfirmationNumber = new HashMap<Long, Booking>();
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
    void checkin() {
    }

    @Test
    void addServiceCharge() {
    }

    @Test
    void checkout() {
    }
}