package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TicketServiceImplTest {

    private TicketPaymentService paymentService;
    private SeatReservationService reservationService;
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        paymentService = Mockito.mock(TicketPaymentService.class);
        reservationService = Mockito.mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, reservationService);
    }
    @Test
    void testPurchaseTickets_InvalidAccountId() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(0L, adultRequest);
        });
    }
    @Test
    void testPurchaseTickets_InvalidNullAccount() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(null, adultRequest);
        });
    }
    @Test
    void testPurchaseTickets_PurchaseZeroTickets() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);

        assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(0L, adultRequest);
        });
    }
    @Test
    void testPurchaseTickets_PurchaseChildTicket() {
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);

        assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(1L, childRequest);
        });
    }
    @Test
    void testPurchaseTickets_PurchaseInfantTicket() {
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

        assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(1L, infantRequest);
        });
    }
    @Test
    void testPurchaseTickets_EmptyRequest() {
        assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(1L);
        });
    }

    @Test
    void testPurchaseTickets_NegativeTickets() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1);

        assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(2L, adultRequest);
        });
    }
    @Test
    void testPurchaseTickets_ExceedMaxTickets() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

        assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(1L, adultRequest);
        });
    }
    @Test
    void testPurchaseTickets_MultipleRequestsSameType() throws InvalidPurchaseException {
        TicketTypeRequest adultRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest adultRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);

        ticketService.purchaseTickets(1L, adultRequest1, adultRequest2);

        verify(paymentService, times(1)).makePayment(1L, 125); // 5 adults * £25 = £125
        verify(reservationService, times(1)).reserveSeat(1L, 5); // 5 adults = 5 seats
    }

    @Test
    void testPurchaseTickets_ZeroAdultTicketsWithChildAndInfant() {
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(1L, childRequest, infantRequest);
        });
    }
    @Test
    void testPurchaseTickets_ValidateSeatAllocation() throws InvalidPurchaseException {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(1L, adultRequest, childRequest, infantRequest);

        verify(reservationService, times(1)).reserveSeat(1L, 5); // 2 adults + 3 children = 5 seats
    }
    @Test
    void testPurchaseTickets_ValidatePaymentCalculation() throws InvalidPurchaseException {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(1L, adultRequest, childRequest, infantRequest);

        verify(paymentService, times(1)).makePayment(1L, 95); // 2 adults * £25 + 3 children * £15 = £95
    }
}