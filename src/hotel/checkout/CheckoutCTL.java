package hotel.checkout;

import java.text.SimpleDateFormat;
import java.util.List;

import hotel.credit.CreditAuthorizer;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import hotel.entities.Booking;
import hotel.entities.Guest;
import hotel.entities.Hotel;
import hotel.entities.ServiceCharge;
import hotel.utils.IOUtils;

public class CheckoutCTL {
    
    private enum State {ROOM, ACCEPT, CREDIT, CANCELLED, COMPLETED}
    
    private Hotel hotel;
    private State state;
    private CheckoutUI checkoutUi;
    private double total;
    private int roomId;
    
    
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
            
            total = 0;
            List<ServiceCharge> charges = booking.getCharges();
            for (ServiceCharge sc : charges) {
                total += sc.getCost();
                String chargeStr = String.format("    %-12s:%10s",
                        sc.getDescription(), String.format("$%.2f", sc.getCost()));
                sb.append(chargeStr).append("\n");
            }
            sb.append(String.format("Total: $%.2f\n", total));
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
    
    
    public void creditDetailsEntered(CreditCardType type, int number, int ccv) {
        
        if (state == State.CREDIT) {
            CreditCard card = new CreditCard(type, number, ccv);
            CreditAuthorizer creditAuthorizer = new CreditAuthorizer();
            boolean approval = creditAuthorizer.authorize(card, total);
            if (approval) {
                this.hotel.checkout(roomId);
                checkoutUi.displayMessage("$" + total + " has been debited from your card");
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
