package hotel.entities;

import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

//John Galvin 11330960
@ExtendWith(MockitoExtension.class)
class BookingTestIntegration {

    @Mock Guest testGuest = new Guest("John","20 Pelican Street",04);

    Room testRoom;
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    Date testArrivalDate;
    int testStayLength;
    int testOccupants;
    @Mock CreditCard testCreditCard = new CreditCard(CreditCardType.MASTERCARD,9999,123);;
    ServiceType testServiceType;
    double testCost;
    //@Spy List<ServiceCharge> charges = new ArrayList<>();

    Booking testBooking;


    BookingTestIntegration()  {
    }


    @BeforeEach
    void setUp() throws ParseException {
        //init all things in here
        MockitoAnnotations.initMocks(this);
        testRoom = new Room(1,RoomType.SINGLE);
        testArrivalDate = format.parse("13-11-2018");
        testStayLength = 3;
        testOccupants = 1;
        testBooking = new Booking(testGuest,testRoom,testArrivalDate,testStayLength,testOccupants,testCreditCard);

    }


    @Test
    void testCheckInIdealWithRealRoom() {
        //arrange
        assertTrue(testBooking.isPending());
        assertTrue(testRoom.isReady());

        //act
        testBooking.checkIn();

        //assert
        assertTrue(testBooking.isCheckedIn());
        assertTrue(testRoom.isOccupied());
    }

    @Test
    void testCheckInThrowsExceptionWithRealRoom() {
        //arrange
        testBooking.checkIn();
        assertFalse(testBooking.isPending());
        //act
        Executable e = () -> testBooking.checkIn();

        //assert
        Throwable R = assertThrows(RuntimeException.class,e);
        assertEquals("Booking Entity cannot call checkIn except in PENDING State",R.getMessage());
    }





}