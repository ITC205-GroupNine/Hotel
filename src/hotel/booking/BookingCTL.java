package hotel.booking;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hotel.credit.CreditAuthorizer;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import hotel.entities.Guest;
import hotel.entities.Hotel;
import hotel.entities.Room;
import hotel.entities.RoomType;
import hotel.utils.IOUtils;

public class BookingCTL {
	
	
	public static enum State {PHONE, ROOM, REGISTER, TIMES, CREDIT, APPROVED, CANCELLED, COMPLETED}
	
	BookingUI bookingUI;
	Hotel hotel;

	Guest guest;
	Room room;
	double cost;
	
	State state;
	int phoneNumber;
	RoomType selectedRoomType;
	int occupantNumber;
	Date arrivalDate;
	int stayLength;


    public BookingCTL(Hotel hotel) {
		this.bookingUI = new BookingUI(this);
		this.hotel = hotel;
		state = State.PHONE;
	}

	
	public void run() {		
		IOUtils.trace("BookingCTL: run");
		bookingUI.run();
	}

    //added for testing
    public State getState() {
        return state;
    }

    //added for testing
    public void setState(State state) {
        this.state = state;
    }


    public void phoneNumberEntered(int phoneNumber) {
		if (state != State.PHONE) {
			String mesg = String.format("BookingCTL: phoneNumberEntered : bad state : %s", state);
			throw new RuntimeException(mesg);
		}
		this.phoneNumber = phoneNumber;
		
		boolean isRegistered = hotel.isRegistered(phoneNumber);
		
		if (isRegistered) {
			guest = hotel.findGuestByPhoneNumber(phoneNumber);
			bookingUI.displayGuestDetails(guest.getName(), guest.getAddress(), guest.getPhoneNumber());
			this.state = State.ROOM;
			bookingUI.setState(BookingUI.State.ROOM);
		}
		else {
			this.state = State.REGISTER;
			bookingUI.setState(BookingUI.State.REGISTER);
		}
	}


	public void guestDetailsEntered(String name, String address) {
		if (state != State.REGISTER) {
			String mesg = String.format("BookingCTL: guestDetailsEntered : bad state : %s", state);
			throw new RuntimeException(mesg);
		}
		guest = hotel.registerGuest(name, address, phoneNumber);
		
		bookingUI.displayGuestDetails(guest.getName(), guest.getAddress(), guest.getPhoneNumber());
		state = State.ROOM;
		bookingUI.setState(BookingUI.State.ROOM);
	}


	public void roomTypeAndOccupantsEntered(RoomType selectedRoomType, int occupantNumber) {
		if (state != State.ROOM) {
			String mesg = String.format("BookingCTL: roomTypeAndOccupantsEntered : bad state : %s", state);
			throw new RuntimeException(mesg);
		}
		this.selectedRoomType = selectedRoomType;
		this.occupantNumber = occupantNumber;
		
		boolean suitable = selectedRoomType.isSuitable(occupantNumber);
		
		if (!suitable) {			
			String notSuitableMessage = "\nRoom type unsuitable, please select another room type\n";
			bookingUI.displayMessage(notSuitableMessage);
		}
		else {
			state = State.TIMES;
			bookingUI.setState(BookingUI.State.TIMES);
		}
	}


	public void bookingTimesEntered(Date arrivalDate, int stayLength) {
		if (state != State.TIMES) {
			String mesg = String.format("BookingCTL: bookingTimesEntered : bad state : %s", state);
			throw new RuntimeException(mesg);
		}
		this.arrivalDate = arrivalDate;
		this.stayLength = stayLength;
		
		room = hotel.findAvailableRoom(selectedRoomType, arrivalDate, stayLength);
		
		if (room == null) {				
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(arrivalDate);
			calendar.add(Calendar.DATE, stayLength);
			Date departureDate = calendar.getTime();
			
			String notAvailableStr = String.format("\n%s is not available between %s and %s\n",
					selectedRoomType.getDescription(),
					format.format(arrivalDate),
					format.format(departureDate));
			
			bookingUI.displayMessage(notAvailableStr);
		}
		else {
			cost = selectedRoomType.calculateCost(arrivalDate, stayLength);
			String description = selectedRoomType.getDescription();
			bookingUI.displayBookingDetails(description, arrivalDate, stayLength, cost);
			state = State.CREDIT;
			bookingUI.setState(BookingUI.State.CREDIT);
		}
	}

    //added for testing so CreditCard can be mocked
	public CreditCard getCard(CreditCardType type, int number, int ccv){
	    return new CreditCard(type, number, ccv);
    }


    //added for testing so CreditAuthorizer can be mocked
    public CreditAuthorizer getCreditAuthorizer(){
	    return CreditAuthorizer.getInstance();
    }


	public void creditDetailsEntered(CreditCardType type, int number, int ccv) {
		if (state == State.CREDIT) {
			CreditCard creditCard = getCard(type, number, ccv);
            CreditAuthorizer creditAuthorizer = getCreditAuthorizer();
			boolean approved = creditAuthorizer.authorize(creditCard, cost);
			if (approved) {
				long confirmationNumber = hotel.book(room, guest, arrivalDate, stayLength, occupantNumber, creditCard);
				String roomDescription = room.getDescription();
				int roomNumber = room.getId();
				String vendor = creditCard.getVendor();
				int cardNumber = creditCard.getNumber();
				String guestName = guest.getName();
				bookingUI.displayConfirmedBooking(roomDescription, roomNumber, arrivalDate,
						stayLength, guestName, vendor, cardNumber, cost, confirmationNumber);
				state = State.COMPLETED;
				bookingUI.setState(BookingUI.State.COMPLETED);
			} else {
				bookingUI.displayMessage("Credit Not Authorized");
			}
		} else {
			throw new RuntimeException("BookingCTL.creditDetailsEntered(): state not set to State.CREDIT");
		}
	}


	public void cancel() {
		IOUtils.trace("BookingCTL: cancel");
		bookingUI.displayMessage("Booking cancelled");
		state = State.CANCELLED;
		bookingUI.setState(BookingUI.State.CANCELLED);
	}
	
	
	public void completed() {
		IOUtils.trace("BookingCTL: completed");
		bookingUI.displayMessage("Booking completed");
	}

	

}
