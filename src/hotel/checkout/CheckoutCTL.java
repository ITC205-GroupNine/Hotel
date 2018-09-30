package hotel.checkout;

import hotel.credit.CreditAuthorizer;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import hotel.entities.Booking;
import hotel.entities.Guest;
import hotel.entities.Hotel;
import hotel.entities.ServiceCharge;
import hotel.utils.IOUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class CheckoutCTL {
    
    enum State {ROOM, ACCEPT, CREDIT, CANCELLED, COMPLETED}
    
    Hotel hotel;
    State state;
    CheckoutUI checkoutUi;
    double cost;
    int roomId;
    
    public CheckoutCTL(Hotel hotel) {
        this.hotel = hotel;
        this.checkoutUi = new CheckoutUI(this);
    }
    
    
    public void run() {
        IOUtils.trace("BookingCTL: run");
        state = State.ROOM;
        checkoutUi.run();
    }
    
    
    public void roomIdEntered(int roomId) {
        if (state != State.ROOM) {
            String msg = String.format("CheckoutCTL: roomIdEntered : bad state : %s", state);
            throw new RuntimeException(msg);
        }
        this.roomId = roomId;
        Booking booking = hotel.findActiveBookingByRoomId(roomId);
        if (booking == null) {
            String msg = String.format("No active hotel.hotel.booking found for room id %d", roomId);
            checkoutUi.displayMessage(msg);
            //cancel();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Charges for room: %d, hotel.hotel.booking: %d\n",
                    roomId, booking.getConfirmationNumber()));
            
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String dateStr = format.format(booking.getArrivalDate());
            sb.append(String.format("Arrival date: %s, Staylength: %d\n", dateStr, booking.getStayLength()));
            
            Guest guest = booking.getGuest();
            sb.append(String.format("Guest: %s, Address: %s, Phone: %d\n",
                    guest.getName(), guest.getAddress(), guest.getPhoneNumber()));
            
            sb.append("Charges:\n");
            
            cost = 0;
            List<ServiceCharge> charges = booking.getCharges();
            for (ServiceCharge sc : charges) {
                cost += sc.getCost();
                String chargeStr = String.format("    %-12s:%10s",
                        sc.getDescription(), String.format("$%.2f", sc.getCost()));
                sb.append(chargeStr).append("\n");
            }
            sb.append(String.format("Total: $%.2f\n", cost));
            String msg = sb.toString();
            checkoutUi.displayMessage(msg);
            state = State.ACCEPT;
            checkoutUi.setState(CheckoutUI.State.ACCEPT);
        }
    }
    
    
    public void chargesAccepted(boolean accepted) {
        if (state != State.ACCEPT) {
            String msg = String.format("CheckoutCTL: roomIdEntered : bad state : %s", state);
            throw new RuntimeException(msg);
        }
        if (!accepted) {
            checkoutUi.displayMessage("Charges not accepted");
            cancel();
        } else {
            checkoutUi.displayMessage("Charges accepted");
            state = State.CREDIT;
            checkoutUi.setState(CheckoutUI.State.CREDIT);
        }
    }
    public CheckoutCTL.State getState() {
        return state;
    }
    
    //added for testing
    public void setState(CheckoutCTL.State state) {
        this.state = state;
    }
    
    //added for testing so CreditCard can be mocked
    public CreditCard getCard(CreditCardType type, int number, int ccv){
        return new CreditCard(type, number, ccv);
    }
    
    //added for testing so CreditAuthorizer can be mocked
    public CreditAuthorizer getCreditAuthorizer(){
        return CreditAuthorizer.getInstance();
    }
    
    
    void creditDetailsEntered(CreditCardType type, int number, int ccv) {
        
        if (state == State.CREDIT) {
            CreditCard creditCard = getCard(type, number, ccv);
            CreditAuthorizer creditAuthorizer = getCreditAuthorizer();
            boolean approval = creditAuthorizer.authorize(creditCard, cost);
            if (approval) {
                this.hotel.checkout(roomId);
                checkoutUi.displayMessage("$" + cost + " has been debited from your card");
                state = State.COMPLETED;
                checkoutUi.setState(CheckoutUI.State.COMPLETED);
            } else {
                checkoutUi.displayMessage("Credit has not been approved");
            }
        }
        else {
            throw new RuntimeException("State must be CREDIT to enter credit details");
        }
    }
    
    
    public void cancel() {
        checkoutUi.displayMessage("Checking out cancelled");
        state = State.CANCELLED;
        checkoutUi.setState(CheckoutUI.State.CANCELLED);
    }
    
    
    public void completed() {
        checkoutUi.displayMessage("Checking out completed");
    }
    
    
}