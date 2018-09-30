package hotel.booking;

import hotel.HotelHelper;
import hotel.credit.CreditAuthorizer;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import hotel.entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BookingCTLScenarioTest {

    @Mock BookingUI mBookingUI;
    Hotel hotel;
    BookingCTL bookingCTL;
    Room room;
    Booking booking;
    Guest guest;
    CreditCard card;
    CreditAuthorizer creditAuthorizer;
    RoomType roomType;
    Date arrivalDate;
    int stayLength;
    int occupantNumber;
    SimpleDateFormat sdf;
    int roomId;
    long confirmationNumber;
    String roomDescription;
    CreditCardType creditCardType;
    int vendor;
    int cardNumber;
    int ccv;
    String guestName;
    int cost;
    String vendorName;
    String address;
    int phoneNum;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        Calendar myCalendar = new GregorianCalendar(2018, 11, 11);
        arrivalDate = myCalendar.getTime();
        stayLength = 1;
        occupantNumber = 1;
        confirmationNumber = 11112018101L;
        roomId = 101;
        roomDescription = "Single room";
        creditCardType = CreditCardType.VISA;
        vendor = 1;
        cardNumber = 5;
        ccv = 1;
        cost = 0;
        guestName = "John Lennon";
        vendorName = "Visa";
        address = "fake st";
        phoneNum = 2;
        try {
            hotel = HotelHelper.loadHotel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bookingCTL = new BookingCTL(hotel);
        bookingCTL.bookingUI = mBookingUI;
    }


    @AfterEach
    void tearDown() {
    }


    @Test
    void testNewBookInNotRegistered() {
        //arrange
        phoneNum = 1;
        bookingCTL.bookingUI = mBookingUI;
        roomType = RoomType.SINGLE;

        //act
        bookingCTL.phoneNumberEntered(phoneNum);
        bookingCTL.guestDetailsEntered(guestName, address);
        bookingCTL.roomTypeAndOccupantsEntered(roomType, occupantNumber);
        bookingCTL.bookingTimesEntered(arrivalDate, stayLength);
        bookingCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);

        //assert
        verify(mBookingUI, times(5)).setState(any());
        verify(mBookingUI).displayGuestDetails(anyString(), anyString(), anyInt());
        verify(mBookingUI).displayBookingDetails(anyString(), any(Date.class), anyInt(), anyDouble());
        verify(mBookingUI).displayConfirmedBooking(anyString(), anyInt(),any(Date.class),
                anyInt(), anyString(), anyString(), anyInt(), anyDouble(), anyLong());
    }

    @Test
    void testNewBookInIsRegistered() {
        //arrange
        phoneNum = 2;
        bookingCTL.bookingUI = mBookingUI;
        roomType = RoomType.SINGLE;

        //act
        bookingCTL.phoneNumberEntered(phoneNum);
        bookingCTL.roomTypeAndOccupantsEntered(roomType, occupantNumber);
        bookingCTL.bookingTimesEntered(arrivalDate, stayLength);
        bookingCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);

        //assert
        verify(mBookingUI, times(4)).setState(any());
        verify(mBookingUI).displayGuestDetails(anyString(), anyString(), anyInt());
        verify(mBookingUI).displayBookingDetails(anyString(), any(Date.class), anyInt(), anyDouble());
        verify(mBookingUI).displayConfirmedBooking(anyString(), anyInt(),any(Date.class),
                anyInt(), anyString(), anyString(), anyInt(), anyDouble(), anyLong());
    }

    @Test
    void testNewBookInIsRegisteredUnsuitableNumOfOccupants() {
        //arrange
        phoneNum = 2;
        occupantNumber = 10;
        bookingCTL.bookingUI = mBookingUI;
        roomType = RoomType.SINGLE;

        //act
        bookingCTL.phoneNumberEntered(phoneNum);
        bookingCTL.roomTypeAndOccupantsEntered(roomType, occupantNumber);

        //assert
        verify(mBookingUI, times(1)).setState(any());
        verify(mBookingUI).displayGuestDetails(anyString(), anyString(), anyInt());
        verify(mBookingUI).displayMessage(anyString());
    }
}
