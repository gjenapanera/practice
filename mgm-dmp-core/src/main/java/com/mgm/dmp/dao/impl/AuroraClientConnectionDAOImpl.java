package com.mgm.dmp.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgm.dmp.dao.AuroraConnectionDAO;
import com.mgmresorts.aurora.messages.*;
import com.mgmresorts.aurora.service.Client;


public class AuroraClientConnectionDAOImpl implements AuroraConnectionDAO {
	
	protected static final Logger LOG = LoggerFactory.getLogger(AuroraClientConnectionDAOImpl.class);
	
	private Client auroraClient = null;
	
	public AuroraClientConnectionDAOImpl(String auroraUrl, String[] cred, int auroraResponseTimeout) {
		Credentials credentialsObj = new Credentials();
		credentialsObj.setUsername(cred[1]);
		credentialsObj.setPassword(cred[2]);
		auroraClient = new Client(cred[1]);
		auroraClient.setResponseTimeout(auroraResponseTimeout);
		auroraClient = auroraClient.open(auroraUrl, credentialsObj);
		if (null != auroraClient) {
			LOG.info("Opened Aurora client connetion for : {} : {} : {}",
					cred[1], cred[2], auroraClient);
		} else {
			LOG.error("Unable to open Aurora client connetion for : {} : {}",
					cred[1], cred[2]);
		}
	}
	
	@Override
	public void closeConnection() {
		if(null != auroraClient) {
    		LOG.info("Invoked Pre Destroy to Close Aurora Client Connection.");
        	auroraClient.close();
        	auroraClient = null;
    	}
		
	}

	@Override
	public GetCustomerByWebCredentialsResponse getCustomerByWebCredentials(
			GetCustomerByWebCredentialsRequest request) {		
		return auroraClient.getCustomerByWebCredentials(request);
	}

	@Override
	public GetDiningAvailabilityResponse getDiningAvailability(
			GetDiningAvailabilityRequest request) {
		
		return auroraClient.getDiningAvailability(request);
	}

	@Override
	public SaveDiningReservationResponse saveDiningReservation(
			SaveDiningReservationRequest request) {
		
		return auroraClient.saveDiningReservation(request);
	}

	@Override
	public MakeDiningReservationResponse makeDiningReservation(
			MakeDiningReservationRequest request) {
		
		return auroraClient.makeDiningReservation(request);
	}

	@Override
	public CancelDiningReservationResponse cancelDiningReservation(
			CancelDiningReservationRequest request) {
		
		return auroraClient.cancelDiningReservation(request);
	}

	@Override
	public RemoveDiningReservationResponse removeDiningReservation(
			RemoveDiningReservationRequest request) {
		
		return auroraClient.removeDiningReservation(request);
	}

	@Override
	public IsProgramApplicableResponse isProgramApplicable(
			IsProgramApplicableRequest request) {
		
		return auroraClient.isProgramApplicable(request);
	}

	@Override
	public CreateCustomerItineraryResponse createCustomerItinerary(
			CreateCustomerItineraryRequest request) {
		
		return auroraClient.createCustomerItinerary(request);
	}

	@Override
	public AddCustomerItineraryResponse addCustomerItinerary(
			AddCustomerItineraryRequest request) {
		
		return auroraClient.addCustomerItinerary(request);
	}

	@Override
	public UpdateCustomerItineraryResponse updateCustomerItinerary(
			UpdateCustomerItineraryRequest request) {
		
		return auroraClient.updateCustomerItinerary(request);
	}

	@Override
	public GetCustomerItinerariesResponse getCustomerItineraries(
			GetCustomerItinerariesRequest request) {
		
		return auroraClient.getCustomerItineraries(request);
	}

	@Override
	public GetCustomerItineraryByDiningConfirmationNumberResponse getCustomerItineraryByDiningConfirmationNumber(
			GetCustomerItineraryByDiningConfirmationNumberRequest request) {
		
		return auroraClient.getCustomerItineraryByDiningConfirmationNumber(request);
	}

	@Override
	public GetCustomerItineraryByRoomConfirmationNumberResponse getCustomerItineraryByRoomConfirmationNumber(
			GetCustomerItineraryByRoomConfirmationNumberRequest request) {
		
		return auroraClient.getCustomerItineraryByRoomConfirmationNumber(request);
	}

	@Override
	public GetCustomerItineraryByShowConfirmationNumberResponse getCustomerItineraryByShowConfirmationNumber(
			GetCustomerItineraryByShowConfirmationNumberRequest request) {
		
		return auroraClient.getCustomerItineraryByShowConfirmationNumber(request);
	}

	@Override
	public AddCustomerResponse addCustomer(AddCustomerRequest request) {
		
		return auroraClient.addCustomer(request);
	}

	@Override
	public UpdateCustomerResponse updateCustomer(UpdateCustomerRequest request) {
		
		return auroraClient.updateCustomer(request);
	}

	@Override
	public SearchCustomerResponse searchCustomer(SearchCustomerRequest request) {
		
		return auroraClient.searchCustomer(request);
	}

	@Override
	public CreateCustomerWebCredentialsResponse createCustomerWebCredentials(
			CreateCustomerWebCredentialsRequest request) {
		
		return auroraClient.createCustomerWebCredentials(request);
	}

	@Override
	public ActivateCustomerWebCredentialsResponse activateCustomerWebCredentials(
			ActivateCustomerWebCredentialsRequest request) {
		
		return auroraClient.activateCustomerWebCredentials(request);
	}

	@Override
	public ValidateCustomerWebCredentialsResponse validateCustomerWebCredentials(
			ValidateCustomerWebCredentialsRequest request) {
		
		return auroraClient.validateCustomerWebCredentials(request);
	}

	@Override
	public ResetCustomerWebPasswordResponse resetCustomerWebPassword(
			ResetCustomerWebPasswordRequest request) {
		
		return auroraClient.resetCustomerWebPassword(request);
	}

	@Override
	public ChangeCustomerWebEmailAddressResponse changeCustomerWebEmailAddress(
			ChangeCustomerWebEmailAddressRequest request) {
		
		return auroraClient.changeCustomerWebEmailAddress(request);
	}

	@Override
	public ChangeCustomerWebPasswordAdminResponse changeCustomerWebPasswordAdmin(
			ChangeCustomerWebPasswordAdminRequest request) {
		
		return auroraClient.changeCustomerWebPasswordAdmin(request);
	}

	@Override
	public ChangeCustomerWebSecretQuestionAnswerResponse changeCustomerWebSecretQuestionAnswer(
			ChangeCustomerWebSecretQuestionAnswerRequest request) {
		
		return auroraClient.changeCustomerWebSecretQuestionAnswer(request);
	}

	@Override
	public ValidateCustomerWebSecretAnswerResponse validateCustomerWebSecretAnswer(
			ValidateCustomerWebSecretAnswerRequest request) {
		
		return auroraClient.validateCustomerWebSecretAnswer(request);
	}

	@Override
	public GetCustomerBalancesFullResponse getCustomerBalancesFull(
			GetCustomerBalancesFullRequest request) {
		
		return auroraClient.getCustomerBalancesFull(request);
	}

	@Override
	public GetCustomerTaxInformationResponse getCustomerTaxInformation(
			GetCustomerTaxInformationRequest request) {
		
		return auroraClient.getCustomerTaxInformation(request);
	}

	@Override
	public SendEmailResponse sendEmail(SendEmailRequest request) {
		
		return auroraClient.sendEmail(request);
	}

	@Override
	public GetCustomerGuestBookPreferencesResponse getCustomerGuestBookPreferences(
			GetCustomerGuestBookPreferencesRequest request) {
		
		return auroraClient.getCustomerGuestBookPreferences(request);
	}

	@Override
	public SetCustomerGuestBookPreferencesResponse setCustomerGuestBookPreferences(
			SetCustomerGuestBookPreferencesRequest request) {
		
		return auroraClient.setCustomerGuestBookPreferences(request);
	}

	@Override
	public GetCustomerByIdResponse getCustomerById(
			GetCustomerByIdRequest request) {
		
		return auroraClient.getCustomerById(request);
	}

	@Override
	public GetRoomPricingAndAvailabilityResponse getRoomPricingAndAvailabilityEx(
			GetRoomPricingAndAvailabilityExRequest request) {
		
		return auroraClient.getRoomPricingAndAvailabilityEx(request);
	}

	@Override
	public GetProgramByOperaPromoCodeResponse getProgramByOperaPromoCode(
			GetProgramByOperaPromoCodeRequest request) {
		
		return auroraClient.getProgramByOperaPromoCode(request);
	}

	@Override
	public GetApplicableProgramsResponse getApplicablePrograms(
			GetApplicableProgramsRequest request) {
		
		return auroraClient.getApplicablePrograms(request);
	}

	@Override
	public GetCustomerOffersResponse getCustomerOffers(
			GetCustomerOffersRequest request) {
		
		return auroraClient.getCustomerOffers(request);
	}

	@Override
	public UpdateRoomReservationResponse updateRoomReservation(
			UpdateRoomReservationRequest request) {
		
		return auroraClient.updateRoomReservation(request);
	}

	@Override
	public MakeRoomReservationResponse makeRoomReservation(
			MakeRoomReservationRequest request) {
		
		return auroraClient.makeRoomReservation(request);
	}

	@Override
	public GetRoomComponentAvailabilityResponse getRoomComponentAvailability(
			GetRoomComponentAvailabilityRequest request) {
		
		return auroraClient.getRoomComponentAvailability(request);
	}

	@Override
	public RemoveRoomReservationResponse removeRoomReservation(
			RemoveRoomReservationRequest request) {
		
		return auroraClient.removeRoomReservation(request);
	}

	@Override
	public SaveRoomReservationResponse saveRoomReservation(
			SaveRoomReservationRequest request) {
		
		return auroraClient.saveRoomReservation(request);
	}

	@Override
	public CancelRoomReservationResponse cancelRoomReservation(
			CancelRoomReservationRequest request) {
		
		return auroraClient.cancelRoomReservation(request);
	}

	@Override
	public HoldBestAvailableShowTicketsResponse holdBestAvailableShowTickets(
			HoldBestAvailableShowTicketsRequest request) {
		
		return auroraClient.holdBestAvailableShowTickets(request);
	}

	@Override
	public GetShowProgramsByArchticsPromoCodeResponse getShowProgramsByArchticsPromoCode(
			GetShowProgramsByArchticsPromoCodeRequest request) {
		
		return auroraClient.getShowProgramsByArchticsPromoCode(request);
	}

	@Override
	public GetShowProgramEventsResponse getShowProgramEvents(
			GetShowProgramEventsRequest request) {
		
		return auroraClient.getShowProgramEvents(request);
	}

	@Override
	public GetApplicableShowProgramsResponse getApplicableShowPrograms(
			GetApplicableShowProgramsRequest request) {
		
		return auroraClient.getApplicableShowPrograms(request);
	}

	@Override
	public GetShowPricingAndAvailabilityResponse getShowPricingAndAvailability(
			GetShowPricingAndAvailabilityRequest request) {
		
		return auroraClient.getShowPricingAndAvailability(request);
	}

	@Override
	public GetShowPricingAndAvailabilityResponse getShowPricingAndAvailabilityByProgram(
			GetShowPricingAndAvailabilityByProgramRequest request) {
		
		return auroraClient.getShowPricingAndAvailabilityByProgram(request);
	}

	@Override
	public HoldSpecificShowTicketsResponse holdSpecificShowTickets(
			HoldSpecificShowTicketsRequest request) {
		
		return auroraClient.holdSpecificShowTickets(request);
	}

	@Override
	public ReleaseShowTicketsResponse releaseShowTickets(
			ReleaseShowTicketsRequest request) {
		
		return auroraClient.releaseShowTickets(request);
	}

	@Override
	public SaveShowReservationResponse saveShowReservation(
			SaveShowReservationRequest request) {
		
		return auroraClient.saveShowReservation(request);
	}

	@Override
	public RemoveShowReservationResponse removeShowReservation(
			RemoveShowReservationRequest request) {
		
		return auroraClient.removeShowReservation(request);
	}

	@Override
	public UpdateShowReservationResponse updateShowReservation(
			UpdateShowReservationRequest request) {
		
		return auroraClient.updateShowReservation(request);
	}

	@Override
	public MakeShowReservationResponse makeShowReservation(
			MakeShowReservationRequest request) {
		
		return auroraClient.makeShowReservation(request);
	}

	@Override
	public PrintShowTicketsResponse printShowTickets(
			PrintShowTicketsRequest request) {
		
		return auroraClient.printShowTickets(request);
	}

	@Override
	public IsShowProgramApplicableResponse isShowProgramApplicable(
			IsShowProgramApplicableRequest request) {
		
		return auroraClient.isShowProgramApplicable(request);
	}

}
