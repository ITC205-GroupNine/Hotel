package hotel.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


//import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;

@ExtendWith(MockitoExtension.class)
class BookingTest {

    @Mock Guest testGuest = new Guest("John","20 Pelican Street",04);
    @Mock Room testRoom = new Room(1,RoomType.SINGLE);
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    Date testArrivalDate = format.parse("13-11-2018");
    int testStayLength = 3;
    int testOccupants = 1;
    @Mock CreditCard testCreditCard = new CreditCard(CreditCardType.MASTERCARD,9999,123);;
    ServiceType testServiceType;
    double testCost;
    @Spy List<ServiceCharge> charges = new ArrayList<>();

    @InjectMocks Booking testBooking = new Booking(testGuest,testRoom,testArrivalDate,testStayLength,testOccupants,testCreditCard);


    BookingTest() throws ParseException {
    }


    @BeforeEach
    void setUp()  {

    }


    @Test
    void testCheckInIdealCase() {
        //arrange

        //act
        assertTrue(testBooking.isPending());
        testBooking.checkIn();

        //assert
        //verify(testRoom).checkin();
        assertTrue(testBooking.isCheckedIn());
    }

    @Test
    void testCheckInThrowsException() {
        //arrange

        //act
        testBooking.checkIn();
        //verify(testRoom).checkin();

        //assert
        assertTrue(testBooking.isCheckedIn());
        Executable e = () -> testBooking.checkIn();
        Throwable R = assertThrows(RuntimeException.class,e);
        assertEquals("Booking Entity cannot call checkIn except in PENDING State",R.getMessage());
    }


    @Test
    void addServiceChargeIdealUseCase() {
        //arrange
        testBooking.checkIn();
        assertTrue(testBooking.isCheckedIn());
        testServiceType = ServiceType.BAR_FRIDGE;
        testCost = 20.00;

        //act test comment
        testBooking.addServiceCharge(testServiceType,testCost);

        //assert
        assertEquals(1,charges.size());

    }

    @Test
    void addServiceChargeThrowsException() {
        //arrange
        assertFalse(testBooking.isCheckedIn());
        testServiceType = ServiceType.BAR_FRIDGE;
        testCost = 20.00;

        //act
        Executable e = () -> testBooking.addServiceCharge(testServiceType,testCost);
        Throwable R = assertThrows(RuntimeException.class,e);

        //assert
        assertEquals("Booking Entity charges cannot be added except in CHECKED_IN state",R.getMessage());

    }

    @Test
    void checkOutIdealUseCase() {
        //arrange
        testBooking.checkIn();
        assertTrue(testBooking.isCheckedIn());

        //act
        testBooking.checkOut();
        //verify(testRoom).checkout(testBooking);


        //assert
        assertFalse(testBooking.isCheckedIn());
    }

    @Test
    void checkOutThrowsException() {
        //arrange
        assertFalse(testBooking.isCheckedIn());

        //act
        Executable e = () -> testBooking.checkOut();
        Throwable R = assertThrows(RuntimeException.class,e);

        //assert
        assertEquals("Booking Entity charges cannot be added except in CHECKED_IN state",R.getMessage());
    }
}