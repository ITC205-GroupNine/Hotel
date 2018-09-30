package hotel.checkout;

import hotel.credit.CreditAuthorizer;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import hotel.entities.Guest;
import hotel.entities.Hotel;
import hotel.entities.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CheckOutCTLIntegrationTest {
    
    @Mock Hotel mHotel;
    @Mock Room mRoom;
    @Mock Guest mGuest;
    @Mock CreditAuthorizer mCreditAuthorizer;
    @Mock
    CheckoutUI mCheckoutUI;
    @Spy @InjectMocks
    CheckoutCTL checkoutCTL = new CheckoutCTL(mHotel);
    
    private Hotel hotel;
    private CreditCard card;
    CreditCardType creditCardType;
    int cardNumber;
    int ccv;
    int cost;
    int roomId;
    int vendor;
    
    
    @BeforeEach
    void setUp() {
        //MockitoAnnotations.initMocks(this);
        
        creditCardType = CreditCardType.VISA;
        cardNumber = 5;
        ccv = 1;
        cost = 0;
        roomId = 101;
        vendor = 1;
    }
    
    @AfterEach
    void tearDown() {
    }
    
    
    @Test
    void creditDetailsEnteredApprovedRealCard() {
        //arrange
        card = new CreditCard(creditCardType, cardNumber, ccv);
        checkoutCTL.checkoutUi = mCheckoutUI;
        checkoutCTL.hotel = mHotel;
        checkoutCTL.cost = cost;
        checkoutCTL.roomId = roomId;
        //ArgumentCaptor<Integer> cardNumCapture = ArgumentCaptor.forClass(Integer.class);
        checkoutCTL.setState(CheckoutCTL.State.CREDIT);
        when(checkoutCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(card);
        
        //act
        checkoutCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        
        //assert
        mHotel.checkout(roomId);
        assertEquals(CheckoutCTL.State.COMPLETED, checkoutCTL.getState());
        verify(mCheckoutUI).setState(CheckoutUI.State.COMPLETED);
        //verify(checkoutUi).totalCaptor.capture();
        assertEquals(CheckoutCTL.State.COMPLETED, checkoutCTL.state);
        verify(mCheckoutUI).setState(CheckoutUI.State.COMPLETED);
    }
    
    @Test
    void creditDetailsEnteredNotApprovedRealCard() {
        //arrange
        card = new CreditCard(creditCardType, cardNumber, ccv);
        checkoutCTL.checkoutUi = mCheckoutUI;
        checkoutCTL.setState(CheckoutCTL.State.CREDIT);
        when(checkoutCTL.getCard(creditCardType, cardNumber, ccv)).thenReturn(card);
        when(checkoutCTL.getCreditAuthorizer()).thenReturn(mCreditAuthorizer);
        when(mCreditAuthorizer.authorize(card, cost)).thenReturn(false);
        
        //act
        checkoutCTL.creditDetailsEntered(creditCardType, cardNumber, ccv);
        
        //assert
        verify(mCheckoutUI).displayMessage("Credit has not been approved");
    }
}
