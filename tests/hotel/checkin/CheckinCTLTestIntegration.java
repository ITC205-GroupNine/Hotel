package hotel.checkin;





import hotel.entities.Room;
import hotel.HotelHelper;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import hotel.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;


//John Galvin 11330960
@ExtendWith(MockitoExtension.class)
class CheckinCTLTestIntegration {
    Hotel testHotel;
    @Mock CheckinUI testCheckinUI;
    Booking testBooking;
    CreditCard testCreditCard;
    Guest tGuest;
    Room testRoom;
    CheckinCTL testCheckinCTL;
    boolean confirmed;
    HotelHelper testHotelHelper;
    String testDisplayConfirmation;
    String testDisplayCancelled;
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    Date testArrivalDate;
    int testStayLength;
    int testOccupants;





    @BeforeEach
    void setUp() throws Exception {
        testHotel = testHotelHelper.loadHotel();
        testCheckinCTL = new CheckinCTL(testHotel);
        testDisplayConfirmation = "Checking in completed";
        testDisplayCancelled = "Checking in cancelled";
        tGuest = new Guest("John","123 Fake Street",1);
        testArrivalDate = format.parse("13-11-2018");
        testStayLength = 1;
        testOccupants =1;
        testCreditCard = new CreditCard(CreditCardType.MASTERCARD,9999,123);
        testRoom = testHotel.findAvailableRoom(RoomType.TWIN_SHARE, testArrivalDate, 1);
        testCheckinCTL.setConfirmationNumber(testHotel.book(testRoom,tGuest,testArrivalDate,testStayLength,testOccupants,testCreditCard));

    }


    @Test
    void checkInConfirmedIdealUseCaseWhereConfirmedIsTrueRealHotel() {

        //ARRANGE
        //CheckinUI removed from private for testing
        //ROOM class attributes made public for testing.
        //added setState and getState under the CheckingCTL class for testing
        testRoom.state = Room.State.READY;
        assertEquals(testRoom.state,Room.State.READY);
        testCheckinCTL.checkInUI = testCheckinUI;
        confirmed = true;
        ArgumentCaptor<String> checkinConfirmed = ArgumentCaptor.forClass(String.class);
        testCheckinCTL.setState(CheckinCTL.State.CONFIRMING);
        assertEquals(CheckinCTL.State.CONFIRMING,testCheckinCTL.getState());


        //ACT
        testCheckinCTL.checkInConfirmed(confirmed);

        //ASSERT
        assertEquals(CheckinCTL.State.COMPLETED,testCheckinCTL.getState());
        verify(testCheckinUI).setState(CheckinUI.State.COMPLETED);
        verify(testCheckinUI).displayMessage(checkinConfirmed.capture());
        assertTrue(testDisplayConfirmation == checkinConfirmed.getValue());


    }

    @Test
    void checkInConfirmedIdealUseCaseWhereConfirmedIsFalseRealHotel() {

        //ARRANGE
        //CheckinUI removed from private for testing
        //ROOM class attributes made public for testing.
        //added setState and getState under the CheckingCTL class for testing
        testRoom.state = Room.State.READY;
        assertEquals(testRoom.state,Room.State.READY);
        testCheckinCTL.checkInUI = testCheckinUI;
        confirmed = false;
        ArgumentCaptor<String> checkinCancelled = ArgumentCaptor.forClass(String.class);
        testCheckinCTL.setState(CheckinCTL.State.CONFIRMING);
        assertEquals(CheckinCTL.State.CONFIRMING,testCheckinCTL.getState());


        //ACT
        testCheckinCTL.checkInConfirmed(confirmed);

        //ASSERT
        assertEquals(CheckinCTL.State.CANCELLED,testCheckinCTL.getState());
        verify(testCheckinUI).setState(CheckinUI.State.CANCELLED);
        verify(testCheckinUI).displayMessage(checkinCancelled.capture());
        assertTrue(testDisplayCancelled == checkinCancelled.getValue());


    }



}