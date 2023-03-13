package com.mockitotutorial.happyhotel.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Test12Bdd {

    @InjectMocks
    private BookingService bookingService;
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private RoomService roomServiceMock;
    @Spy
    private BookingDAO bookingDAOMock;
    @Mock
    private MailSender mailSenderMock;
    @Captor
    private ArgumentCaptor<Double> doubleCaptor;

    @Test
    void should_CountAvailableSpaces_When_OneRoomAvailable() {
        // given
        given(this.roomServiceMock.getAvailableRooms()).willReturn(Collections.singletonList(new Room("Room 1", 2)));
        int expected = 2;
        // when
        int actual = bookingService.getAvailablePlaceCount();
        // then
        assertEquals(expected, actual);
    }

    @Test
    void should_CountAvailableSpaces_When_MultipleRoomsAvailable() {
        // given
        List<Room> rooms = Arrays.asList(new Room("Room 1", 2), new Room("Room 1", 5));
        given(this.roomServiceMock.getAvailableRooms()).willReturn(rooms);
        int expected = 7;
        // when
        int actual = bookingService.getAvailablePlaceCount();
        // then
        assertEquals(expected, actual);
    }

    @Test
    void should_InvokePayment_When_Prepaid() {
        // given
        BookingRequest bookingRequest = new BookingRequest("1", LocalDate.of(2020, 01, 01), LocalDate.of(2020, 01, 05), 2, true);
        // when
        bookingService.makeBooking(bookingRequest);
        // then
        then(paymentServiceMock).should(times(1)).pay(bookingRequest, 400.0);
        verifyNoMoreInteractions(paymentServiceMock);
    }

    @Test
    void should_InvokePayment_When_NoPrepaid() {
        // given
        BookingRequest bookingRequest = new BookingRequest("1", LocalDate.of(2020, 01, 01), LocalDate.of(2020, 01, 05), 2, false);
        // when
        bookingService.makeBooking(bookingRequest);
        // then
        then(paymentServiceMock).should(never()).pay(any(), anyDouble());
        verifyNoMoreInteractions(paymentServiceMock);
    }
}
