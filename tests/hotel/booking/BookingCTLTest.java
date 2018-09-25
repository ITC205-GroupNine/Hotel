package hotel.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hotel.credit.CreditAuthorizer;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import hotel.entities.Booking;
import hotel.entities.Guest;
import hotel.entities.Hotel;
import hotel.entities.Room;


@ExtendWith(MockitoExtension.class)
class BookingCTLTest {



    @Mock Hotel mHotel;
    @Mock Room mRoom;
    @Mock Guest mGuest;
    Date arrivalDate;
    int stayLength;
    int occupantNumber;
    @Mock CreditCard mCard;
    SimpleDateFormat format;
    int roomId;
    long confirmationNumber;
    @Mock Booking mBooking;
    @Mock CreditAuthorizer mCreditAuthorizer;
    @Mock BookingUI mBookingUI;
    String roomDescription;
    CreditCardType creditCardType;
    int vendor;
    int cardNumber;
    int ccv;
    String guestName;
    int cost;
    String vendorName;


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
        roomDescription = "room description";
        CreditCardType creditCardType = CreditCardType.VISA;
        vendor = 1;
        cardNumber = 6;
        ccv = 1;
        cost = 0;
        guestName = "John Lennon";
        vendorName = "Visa";
    }

    @AfterEach
    void tearDown() {
    }

    @InjectMocks BookingCTL bookingCTL = spy(new BookingCTL(mHotel));

    @Test
    void creditDetailsEntered() {
        //arrange
        //changed state and enum State to public for testing purposes
        //bookingCTL.state = BookingCTL.State.CREDIT;
        when(bookingCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(mCard);
        when(bookingCTL.getCreditAuthorizer()).thenReturn(mCreditAuthorizer);
        when(mCreditAuthorizer.authorize(mCard, cost)).thenReturn(true);

        //act
        bookingCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        //assert
        //assertEquals(BookingCTL.State.COMPLETED, bookingCTL.state);
        verify(mBookingUI).setState(BookingUI.State.COMPLETED);
    }

    @Test
    void creditDetailsEnteredNotApproved() {
        //arrange
        //changed state and enum State to public for testing purposes
        //bookingCTL.state = BookingCTL.State.CREDIT;
        //act
        bookingCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        //assert
        verify(mBookingUI).displayMessage("Credit Not Authorized");
    }

    @Test
    void creditDetailsEnteredThrowException() {
        //arrange
        //changed state and enum State to public for testing purposes
        //bookingCTL.state = BookingCTL.State.COMPLETED;
        //act
        Executable e = () -> bookingCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        Throwable t = assertThrows(RuntimeException.class, e);
        //assert
        assertEquals("BookingCTL.creditDetailsEntered(): state not set to State.CREDIT", t.getMessage());
    }
}