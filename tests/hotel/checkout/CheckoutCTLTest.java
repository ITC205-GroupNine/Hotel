package hotel.checkout;


import hotel.credit.CreditAuthorizer;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import hotel.entities.Hotel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckoutCTLTest {
    
    @Mock
    Hotel mhotel;
    @Mock
    CreditCard creditCard;
    @Mock
    CreditAuthorizer creditAuthorizer;
    @Mock
    CheckoutUI checkoutUi;
    
    
    private CreditCardType creditCardType;
    private int cardNumber;
    private int ccv;
    int total;
    
    @BeforeEach
    void setUp() {
        total = 0;
        cardNumber = 6;
        ccv = 1;
        
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
        when(checkOutCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(creditCard);
        when(checkOutCTL.getCreditAuthorizer()).thenReturn(creditAuthorizer);
    
        //act
        checkOutCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        checkOutCTL.state = CheckoutCTL.State.COMPLETED;
        
        //assert
        assertEquals(CheckoutCTL.State.COMPLETED, checkOutCTL.state);
        verify(checkoutUi).displayMessage("Credit has not been approved");
    }
    
    
    @Test
    void creditDetailsEnteredNotApproved() {
        //arrange
        //changed state and enum State to public for testing purposes
        checkOutCTL.state = CheckoutCTL.State.CREDIT;
        when(checkOutCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(creditCard);
        when(checkOutCTL.getCreditAuthorizer()).thenReturn(creditAuthorizer);
        when(creditAuthorizer.authorize(creditCard, total)).thenReturn(false);
        //act
        checkOutCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        //assert
        //assert
        verify(checkoutUi).displayMessage("Credit has not been approved");
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