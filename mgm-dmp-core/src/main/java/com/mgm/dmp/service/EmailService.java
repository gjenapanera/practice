package com.mgm.dmp.service;

import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.common.vo.BookAllReservationRequest;
import com.mgm.dmp.common.vo.DiningReservationRequest;
import com.mgm.dmp.common.vo.EmailRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;

public interface EmailService {
   void sendEmail(EmailRequest emailRequest);
   EmailRequest getCachedMailTemplate(EmailRequest emailRequest);
   void sendRoomShowBookingConfirmationEmail(BookAllReservationRequest bookAllreservationRequest,String programId,String promoCode);
   void sendDiningReservationConfirmation(DiningReservationRequest reservationRequest, AbstractReservation reservation);
   void sendRoomCancellationConfirmation(ItineraryRequest itineraryRequest, AbstractReservation reservation);
   void sendDiningCancellationConfirmation(ItineraryRequest itineraryRequest, AbstractReservation reservation);
}
