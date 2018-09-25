package hotel.checkin;

import hotel.entities.Hotel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class CheckinCTLTest {
    @Mock Hotel testHotel;
    @Mock CheckinCTL testCHECK = new CheckinCTL(testHotel);
    @InjectMocks CheckinCTL testCheckinCTL = new CheckinCTL(testHotel);
    boolean confirmed = true;
    @Mock CheckinUI testCheckinUI = new CheckinUI(testCheckinCTL);

    @BeforeEach
    void setUp()  {

    }


    @Test
    void checkInConfirmedIdealUseCaseWhereConfirmedIsTrue() {
        //State ENUM in CheckinCTL set to PUBLIC for testing.
        //ARRANGE
        //testCheckinCTL.state = CheckinCTL.State.CONFIRMING;
        //assertEquals(CheckinCTL.State.CONFIRMING,testCheckinCTL.state);

        //ACT
        testCheckinCTL.checkInConfirmed(confirmed);

        //ASSERT
        //assertEquals(CheckinCTL.State.COMPLETED,testCheckinCTL.state);
        verify(testCheckinUI).setState(CheckinUI.State.COMPLETED);
        verify(testCheckinUI).displayMessage("Checking in completed");

    }

    @Test
    void checkInConfirmedIdealUseCaseWhereConfirmedIsFalse() {
        //State ENUM in CheckinCTL set to PUBLIC for testing.

        //ARRANGE
        confirmed = false;
        //testCheckinCTL.state = CheckinCTL.State.CONFIRMING;
        //assertEquals(CheckinCTL.State.CONFIRMING,testCheckinCTL.state);

        //ACT
        testCheckinCTL.checkInConfirmed(confirmed);

        //ASSERT
        verify(testCheckinUI).setState(CheckinUI.State.CANCELLED);
        //assertEquals(CheckinCTL.State.CANCELLED,testCheckinCTL.state);
        verify(testCheckinUI).displayMessage("Checking in cancelled");
    }

    @Test
    void checkInConfirmedthrowsException() {
        //State ENUM in CheckinCTL set to PUBLIC for testing.
        //ARRANGE
        //assertNotEquals(CheckinCTL.State.CONFIRMING,testCheckinCTL.state);

        //ACT
        Executable e = () -> testCheckinCTL.checkInConfirmed(confirmed);
        Throwable R = assertThrows(RuntimeException.class,e);

        //Assert
        assertEquals("Check in is not ready to be confirmed.",R.getMessage());


    }
}