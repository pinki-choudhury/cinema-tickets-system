package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;
import java.util.Map;

public class TicketServiceImpl implements TicketService {
    private static final int MAX_TICKETS = 25;
    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;
    private final Map<TicketTypeRequest.Type, Integer> ticketPrices;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
        ticketPrices = new HashMap<>();
        ticketPrices.put(TicketTypeRequest.Type.ADULT, 25);
        ticketPrices.put(TicketTypeRequest.Type.CHILD, 15);
        ticketPrices.put(TicketTypeRequest.Type.INFANT, 0);

    }
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validateAccount(accountId);
        validatePurchaseRule(accountId, ticketTypeRequests);

        int totalAmount = calculateTotalAmount(ticketTypeRequests);
        int totalSeats = calculateTotalSeats(ticketTypeRequests);

        paymentService.makePayment(accountId, totalAmount);
        reservationService.reserveSeat(accountId, totalSeats);
    }
    private void validateAccount(Long accountId) throws InvalidPurchaseException{

        // All accounts with an id greater than zero are valid
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid account Id.");
        }
    }
    private void validatePurchaseRule(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        int adultCount = 0;
        int totalTickets = 0;

        for (TicketTypeRequest ticketType : ticketTypeRequests) {
            if (ticketType.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException("Ticket quantity must be greater than zero.");
            }
            totalTickets += ticketType.getNoOfTickets();
            if (ticketType.getTicketType() == TicketTypeRequest.Type.ADULT) {
                adultCount += ticketType.getNoOfTickets();
            }
        }
        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException("Cannot purchase more than " + MAX_TICKETS + "tickets at a time");
        }
        if (adultCount == 0) {
            throw new InvalidPurchaseException("At least one adult ticket must be purchased");
        }
    }

    private int calculateTotalAmount(TicketTypeRequest... ticketTypeRequests) {
        int totalAmount = 0;
        for (TicketTypeRequest ticketType : ticketTypeRequests) {
            // Get the price from the map and multiply by the number of tickets
            totalAmount += ticketPrices.get(ticketType.getTicketType()) * ticketType.getNoOfTickets();
        }
        return totalAmount;
    }
    private int calculateTotalSeats(TicketTypeRequest... ticketTypeRequests) {
        int totalSeats = 0;
        for (TicketTypeRequest ticketType : ticketTypeRequests) {
            //Infants are not allocated any seat
            if (ticketType.getTicketType() != TicketTypeRequest.Type.INFANT) {
                totalSeats += ticketType.getNoOfTickets();
            }
        }
        return totalSeats;
    }


}
