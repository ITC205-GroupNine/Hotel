package hotel.booking;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import hotel.entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;


import hotel.credit.CreditAuthorizer;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


@ExtendWith(MockitoExtension.class)
public class BookingCTLIntegrationTest {

    @Mock Hotel mHotel;
    @Mock Room mRoom;
    @Mock Guest mGuest;
    @Mock CreditCard mCard;
    @Mock Booking mBooking;
    @Mock CreditAuthorizer mCreditAuthorizer;
    @Mock BookingUI mBookingUI;
    @Spy @InjectMocks BookingCTL bookingCTL = new BookingCTL(mHotel);

    Hotel hotel;
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
        //MockitoAnnotations.initMocks(this);

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
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void creditDetailsEnteredRealCard(){
        //arrange
        card = new CreditCard(creditCardType, cardNumber, ccv);
        bookingCTL.bookingUI = mBookingUI;
        bookingCTL.hotel = mHotel;
        bookingCTL.guest = mGuest;
        bookingCTL.room = mRoom;
        bookingCTL.cost = cost;
        bookingCTL.occupantNumber = occupantNumber;
        bookingCTL.arrivalDate = arrivalDate;
        bookingCTL.stayLength = stayLength;
        ArgumentCaptor<String> roomDescriptionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> roomIDCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Date> arrivalDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Integer> stayLengthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> vendorCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> cardNumCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Double> costCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Long> confirmationNumCaptor = ArgumentCaptor.forClass(Long.class);
        bookingCTL.setState(BookingCTL.State.CREDIT);
        when(bookingCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(card);
        when(mRoom.getDescription()).thenReturn(roomDescription);
        when(mRoom.getId()).thenReturn(roomId);
        when(mGuest.getName()).thenReturn(guestName);
        when(mHotel.book(mRoom, mGuest, arrivalDate, stayLength, occupantNumber, card)).thenReturn(confirmationNumber);

        //act
        bookingCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        //assert
        verify(mBookingUI).displayConfirmedBooking(roomDescriptionCaptor.capture(), roomIDCaptor.capture(),
                arrivalDateCaptor.capture(), stayLengthCaptor.capture(), nameCaptor.capture(),
                vendorCaptor.capture(), cardNumCapture.capture(), costCaptor.capture(),
                confirmationNumCaptor.capture());
        assertEquals(BookingCTL.State.COMPLETED, bookingCTL.getState());
        verify(mBookingUI).setState(BookingUI.State.COMPLETED);
        assertEquals(roomDescription, roomDescriptionCaptor.getValue());
        assertTrue(roomId == roomIDCaptor.getValue());
        assertEquals(arrivalDate, arrivalDateCaptor.getValue());
        assertTrue(stayLength == stayLengthCaptor.getValue());
        assertEquals(guestName, nameCaptor.getValue());
        assertEquals("Visa", vendorCaptor.getValue());
        assertTrue(card.getNumber() == cardNumCapture.getValue());
        assertTrue(cost == costCaptor.getValue());
        assertTrue(confirmationNumber == confirmationNumCaptor.getValue());
    }

    @Test
    void creditDetailsEnteredNotApprovedRealCard() {
        //arrange
        card = new CreditCard(creditCardType, cardNumber, ccv);
        bookingCTL.bookingUI = mBookingUI;
        bookingCTL.setState(BookingCTL.State.CREDIT);
        when(bookingCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(card);
        when(bookingCTL.getCreditAuthorizer()).thenReturn(mCreditAuthorizer);
        when(mCreditAuthorizer.authorize(card, cost)).thenReturn(false);
        //act
        bookingCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        //assert
        verify(mBookingUI).displayMessage("Credit Not Authorized");
    }


    @Test
    void creditDetailsEnteredRealCardRealRoom(){
        //arrange
        room = new Room(roomId, RoomType.SINGLE);
        card = new CreditCard(creditCardType, cardNumber, ccv);
        bookingCTL.bookingUI = mBookingUI;
        bookingCTL.hotel = mHotel;
        bookingCTL.guest = mGuest;
        bookingCTL.room = room;
        bookingCTL.cost = cost;
        bookingCTL.occupantNumber = occupantNumber;
        bookingCTL.arrivalDate = arrivalDate;
        bookingCTL.stayLength = stayLength;
        ArgumentCaptor<String> roomDescriptionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> roomIDCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Date> arrivalDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Integer> stayLengthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> vendorCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> cardNumCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Double> costCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Long> confirmationNumCaptor = ArgumentCaptor.forClass(Long.class);
        bookingCTL.setState(BookingCTL.State.CREDIT);
        when(bookingCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(card);
        when(mGuest.getName()).thenReturn(guestName);
        when(mHotel.book(room, mGuest, arrivalDate, stayLength, occupantNumber, card)).thenReturn(confirmationNumber);

        //act
        bookingCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        //assert
        verify(mBookingUI).displayConfirmedBooking(roomDescriptionCaptor.capture(), roomIDCaptor.capture(),
                arrivalDateCaptor.capture(), stayLengthCaptor.capture(), nameCaptor.capture(),
                vendorCaptor.capture(), cardNumCapture.capture(), costCaptor.capture(),
                confirmationNumCaptor.capture());
        assertEquals(BookingCTL.State.COMPLETED, bookingCTL.getState());
        verify(mBookingUI).setState(BookingUI.State.COMPLETED);
        assertEquals(roomDescription, roomDescriptionCaptor.getValue());
        assertTrue(roomId == roomIDCaptor.getValue());
        assertEquals(arrivalDate, arrivalDateCaptor.getValue());
        assertTrue(stayLength == stayLengthCaptor.getValue());
        assertEquals(guestName, nameCaptor.getValue());
        assertEquals("Visa", vendorCaptor.getValue());
        assertTrue(card.getNumber() == cardNumCapture.getValue());
        assertTrue(cost == costCaptor.getValue());
        assertTrue(confirmationNumber == confirmationNumCaptor.getValue());
    }

    @Test
    void creditDetailsEnteredRealCardRealRoomRealGuest(){
        //arrange
        room = new Room(roomId, RoomType.SINGLE);
        card = new CreditCard(creditCardType, cardNumber, ccv);
        guest = new Guest(guestName, address, phoneNum);
        bookingCTL.bookingUI = mBookingUI;
        bookingCTL.guest = guest;
        bookingCTL.room = room;
        bookingCTL.cost = cost;
        bookingCTL.occupantNumber = occupantNumber;
        bookingCTL.arrivalDate = arrivalDate;
        bookingCTL.stayLength = stayLength;
        ArgumentCaptor<String> roomDescriptionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> roomIDCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Date> arrivalDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Integer> stayLengthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> vendorCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> cardNumCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Double> costCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Long> confirmationNumCaptor = ArgumentCaptor.forClass(Long.class);
        bookingCTL.setState(BookingCTL.State.CREDIT);
        when(bookingCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(card);
        when(mHotel.book(room, guest, arrivalDate, stayLength, occupantNumber, card)).thenReturn(confirmationNumber);

        //act
        bookingCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        //assert
        verify(mBookingUI).displayConfirmedBooking(roomDescriptionCaptor.capture(), roomIDCaptor.capture(),
                arrivalDateCaptor.capture(), stayLengthCaptor.capture(), nameCaptor.capture(),
                vendorCaptor.capture(), cardNumCapture.capture(), costCaptor.capture(),
                confirmationNumCaptor.capture());
        assertEquals(BookingCTL.State.COMPLETED, bookingCTL.getState());
        verify(mBookingUI).setState(BookingUI.State.COMPLETED);
        assertEquals(roomDescription, roomDescriptionCaptor.getValue());
        assertTrue(roomId == roomIDCaptor.getValue());
        assertEquals(arrivalDate, arrivalDateCaptor.getValue());
        assertTrue(stayLength == stayLengthCaptor.getValue());
        assertEquals(guestName, nameCaptor.getValue());
        assertEquals("Visa", vendorCaptor.getValue());
        assertTrue(card.getNumber() == cardNumCapture.getValue());
        assertTrue(cost == costCaptor.getValue());
        assertTrue(confirmationNumber == confirmationNumCaptor.getValue());
    }


    @Test
    void creditDetailsEnteredRealCardRealRoomRealGuestRealHotel(){
        //arrange
        hotel = new Hotel();
        room = new Room(roomId, RoomType.SINGLE);
        card = new CreditCard(creditCardType, cardNumber, ccv);
        guest = new Guest(guestName, address, phoneNum);
        bookingCTL.hotel = hotel;
        bookingCTL.bookingUI = mBookingUI;
        bookingCTL.guest = guest;
        bookingCTL.room = room;
        bookingCTL.cost = cost;
        bookingCTL.occupantNumber = occupantNumber;
        bookingCTL.arrivalDate = arrivalDate;
        bookingCTL.stayLength = stayLength;
        ArgumentCaptor<String> roomDescriptionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> roomIDCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Date> arrivalDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Integer> stayLengthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> vendorCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> cardNumCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Double> costCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Long> confirmationNumCaptor = ArgumentCaptor.forClass(Long.class);
        bookingCTL.setState(BookingCTL.State.CREDIT);
        when(bookingCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(card);
        //when(mHotel.book(room, guest, arrivalDate, stayLength, occupantNumber, card)).thenReturn(confirmationNumber);

        //act
        bookingCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        //assert
        verify(mBookingUI).displayConfirmedBooking(roomDescriptionCaptor.capture(), roomIDCaptor.capture(),
                arrivalDateCaptor.capture(), stayLengthCaptor.capture(), nameCaptor.capture(),
                vendorCaptor.capture(), cardNumCapture.capture(), costCaptor.capture(),
                confirmationNumCaptor.capture());
        assertEquals(BookingCTL.State.COMPLETED, bookingCTL.getState());
        verify(mBookingUI).setState(BookingUI.State.COMPLETED);
        assertEquals(roomDescription, roomDescriptionCaptor.getValue());
        assertTrue(roomId == roomIDCaptor.getValue());
        assertEquals(arrivalDate, arrivalDateCaptor.getValue());
        assertTrue(stayLength == stayLengthCaptor.getValue());
        assertEquals(guestName, nameCaptor.getValue());
        assertEquals("Visa", vendorCaptor.getValue());
        assertTrue(cardNumber == cardNumCapture.getValue());
        assertTrue(cost == costCaptor.getValue());
        assertTrue(confirmationNumber == confirmationNumCaptor.getValue());
    }
}
