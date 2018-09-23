package hotel.checkout;


import hotel.credit.CreditAuthorizer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.function.Executable;

import hotel.entities.Hotel;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import hotel.checkout.CheckoutCTLTest;

import java.util.function.BooleanSupplier;

@ExtendWith(MockitoExtension.class)
public class CheckoutCTLTest {
    
    @Mock
    Hotel mhotel;
    @Mock
    CreditCard Card;
    @Mock
    CreditAuthorizer CreditAuthorizer;
    @Mock
    CheckoutUI CheckoutUi;
    
    
    private CreditCardType creditCardType;
    private int cardNumber;
    private int ccv;
    int cost;
    
    @BeforeEach
    void setUp() {
        cost = 0;
    }
    
    @AfterEach
    void tearDown() {
    }
    
    
    @InjectMocks
    CheckoutCTL checkOutCTL = spy(new CheckoutCTL(mhotel));
    
    
    @Test
    void creditDetailsEntered() {
        //arrange
        checkOutCTL.state = CheckoutCTL.State.CREDIT;
        when(checkOutCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(Card);
        when(checkOutCTL.getCreditAuthorizer()).thenReturn(CreditAuthorizer);
        checkOutCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        //assert
        assertEquals(CheckoutCTL.State.COMPLETED, checkOutCTL.state);
        verify(CheckoutUi).setState(hotel.checkout.CheckoutUI.State.COMPLETED);
    }
    
    
    @Test
    void creditDetailsEnteredNotApproved() {
        //arrange
        //changed state and enum State to public for testing purposes
        checkOutCTL.state = CheckoutCTL.State.CREDIT;
        //when(checkOutCTL.getCreditAuthorizer()).thenReturn(CreditAuthorizer);
        assertFalse(CheckoutCTL.getApproval());
        //act
        checkOutCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        //assert
        verify(CheckoutUi).displayMessage("Credit has not been approved");
    }
    
    @Test
    void creditDetailsEnteredThrowException() {
        //arrange
        //changed state to package private for testing purposes
        checkOutCTL.state = CheckoutCTL.State.COMPLETED;
        //act
        Executable e = () -> checkOutCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        
        Throwable t = assertThrows(RuntimeException.class, e);
        //assert
        assertEquals("State must be CREDIT to enter credit details", t.getMessage());
    }
}

