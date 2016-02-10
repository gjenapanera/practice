package com.mgm.dmp.dao.impl.mock;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgm.dmp.dao.AuroraConnectionDAO;
import com.mgmresorts.aurora.messages.*;

public class AuroraMockConnectionDAOImpl implements AuroraConnectionDAO {
	
	protected static final Logger LOG = LoggerFactory.getLogger(AuroraMockConnectionDAOImpl.class);
	
	private String mockResponseLocation;
	private long minMockDelay = 50L;
	private long maxMockDelay = 200L;

	public AuroraMockConnectionDAOImpl(String mockResponseLocation, long minMockDelay, long maxMockDelay) {
		this.mockResponseLocation = mockResponseLocation;
		this.minMockDelay = minMockDelay;
		this.maxMockDelay = maxMockDelay;
	}

	@Override
	public void closeConnection() {
	}

	@Override
	public GetCustomerByWebCredentialsResponse getCustomerByWebCredentials(
			GetCustomerByWebCredentialsRequest request) {
		
		return jsonResponseUnmarshallar("getCustomerByWebCredentials", GetCustomerByWebCredentialsResponse.class);
	}

	@Override
	public GetDiningAvailabilityResponse getDiningAvailability(
			GetDiningAvailabilityRequest request) {
		
		return jsonResponseUnmarshallar("getDiningAvailability", GetDiningAvailabilityResponse.class);
	}

	@Override
	public SaveDiningReservationResponse saveDiningReservation(
			SaveDiningReservationRequest request) {
		return jsonResponseUnmarshallar("saveDiningReservation", SaveDiningReservationResponse.class);
	}

	@Override
	public MakeDiningReservationResponse makeDiningReservation(
			MakeDiningReservationRequest request) {
		return jsonResponseUnmarshallar("makeDiningReservation", MakeDiningReservationResponse.class);
	}

	@Override
	public CancelDiningReservationResponse cancelDiningReservation(
			CancelDiningReservationRequest request) {
		return jsonResponseUnmarshallar("cancelDiningReservation", CancelDiningReservationResponse.class);
	}

	@Override
	public RemoveDiningReservationResponse removeDiningReservation(
			RemoveDiningReservationRequest request) {
		return jsonResponseUnmarshallar("removeDiningReservation", RemoveDiningReservationResponse.class);
	}

	@Override
	public IsProgramApplicableResponse isProgramApplicable(
			IsProgramApplicableRequest request) {
		return jsonResponseUnmarshallar("isProgramApplicable", IsProgramApplicableResponse.class);
	}

	@Override
	public CreateCustomerItineraryResponse createCustomerItinerary(
			CreateCustomerItineraryRequest request) {
		return jsonResponseUnmarshallar("createCustomerItinerary", CreateCustomerItineraryResponse.class);
	}

	@Override
	public AddCustomerItineraryResponse addCustomerItinerary(
			AddCustomerItineraryRequest request) {
		return jsonResponseUnmarshallar("addCustomerItinerary", AddCustomerItineraryResponse.class);
	}

	@Override
	public UpdateCustomerItineraryResponse updateCustomerItinerary(
			UpdateCustomerItineraryRequest request) {
		return jsonResponseUnmarshallar("updateCustomerItinerary", UpdateCustomerItineraryResponse.class);
	}

	@Override
	public GetCustomerItinerariesResponse getCustomerItineraries(
			GetCustomerItinerariesRequest request) {
		return jsonResponseUnmarshallar("getCustomerItineraries", GetCustomerItinerariesResponse.class);
	}

	@Override
	public GetCustomerItineraryByDiningConfirmationNumberResponse getCustomerItineraryByDiningConfirmationNumber(
			GetCustomerItineraryByDiningConfirmationNumberRequest request) {
		return jsonResponseUnmarshallar("getCustomerItineraryByDiningConfirmationNumber", GetCustomerItineraryByDiningConfirmationNumberResponse.class);
	}

	@Override
	public GetCustomerItineraryByRoomConfirmationNumberResponse getCustomerItineraryByRoomConfirmationNumber(
			GetCustomerItineraryByRoomConfirmationNumberRequest request) {
		return jsonResponseUnmarshallar("getCustomerItineraryByRoomConfirmationNumber", GetCustomerItineraryByRoomConfirmationNumberResponse.class);
	}

	@Override
	public GetCustomerItineraryByShowConfirmationNumberResponse getCustomerItineraryByShowConfirmationNumber(
			GetCustomerItineraryByShowConfirmationNumberRequest request) {
		return jsonResponseUnmarshallar("getCustomerItineraryByShowConfirmationNumber", GetCustomerItineraryByShowConfirmationNumberResponse.class);
	}

	@Override
	public AddCustomerResponse addCustomer(AddCustomerRequest request) {
		return jsonResponseUnmarshallar("addCustomer", AddCustomerResponse.class);
	}

	@Override
	public UpdateCustomerResponse updateCustomer(UpdateCustomerRequest request) {
		return jsonResponseUnmarshallar("updateCustomer", UpdateCustomerResponse.class);
	}

	@Override
	public SearchCustomerResponse searchCustomer(SearchCustomerRequest request) {
		return jsonResponseUnmarshallar("searchCustomer", SearchCustomerResponse.class);
	}

	@Override
	public CreateCustomerWebCredentialsResponse createCustomerWebCredentials(
			CreateCustomerWebCredentialsRequest request) {
		return jsonResponseUnmarshallar("createCustomerWebCredentials", CreateCustomerWebCredentialsResponse.class);
	}

	@Override
	public ActivateCustomerWebCredentialsResponse activateCustomerWebCredentials(
			ActivateCustomerWebCredentialsRequest request) {
		return jsonResponseUnmarshallar("activateCustomerWebCredentials", ActivateCustomerWebCredentialsResponse.class);
	}

	@Override
	public ValidateCustomerWebCredentialsResponse validateCustomerWebCredentials(
			ValidateCustomerWebCredentialsRequest request) {
		return jsonResponseUnmarshallar("validateCustomerWebCredentials", ValidateCustomerWebCredentialsResponse.class);
	}

	@Override
	public ResetCustomerWebPasswordResponse resetCustomerWebPassword(
			ResetCustomerWebPasswordRequest request) {
		return jsonResponseUnmarshallar("resetCustomerWebPassword", ResetCustomerWebPasswordResponse.class);
	}

	@Override
	public ChangeCustomerWebEmailAddressResponse changeCustomerWebEmailAddress(
			ChangeCustomerWebEmailAddressRequest request) {
		return jsonResponseUnmarshallar("changeCustomerWebEmailAddress", ChangeCustomerWebEmailAddressResponse.class);
	}

	@Override
	public ChangeCustomerWebPasswordAdminResponse changeCustomerWebPasswordAdmin(
			ChangeCustomerWebPasswordAdminRequest request) {
		return jsonResponseUnmarshallar("changeCustomerWebPasswordAdmin", ChangeCustomerWebPasswordAdminResponse.class);
	}

	@Override
	public ChangeCustomerWebSecretQuestionAnswerResponse changeCustomerWebSecretQuestionAnswer(
			ChangeCustomerWebSecretQuestionAnswerRequest request) {
		return jsonResponseUnmarshallar("changeCustomerWebSecretQuestionAnswer", ChangeCustomerWebSecretQuestionAnswerResponse.class);
	}

	@Override
	public ValidateCustomerWebSecretAnswerResponse validateCustomerWebSecretAnswer(
			ValidateCustomerWebSecretAnswerRequest request) {
		return jsonResponseUnmarshallar("validateCustomerWebSecretAnswer", ValidateCustomerWebSecretAnswerResponse.class);
	}

	@Override
	public GetCustomerBalancesFullResponse getCustomerBalancesFull(
			GetCustomerBalancesFullRequest request) {
		return jsonResponseUnmarshallar("getCustomerBalancesFull", GetCustomerBalancesFullResponse.class);
	}

	@Override
	public GetCustomerTaxInformationResponse getCustomerTaxInformation(
			GetCustomerTaxInformationRequest request) {
		return jsonResponseUnmarshallar("getCustomerTaxInformation", GetCustomerTaxInformationResponse.class);
	}

	@Override
	public SendEmailResponse sendEmail(SendEmailRequest request) {
		return jsonResponseUnmarshallar("SendEmailRequest", SendEmailResponse.class);
	}

	@Override
	public GetCustomerGuestBookPreferencesResponse getCustomerGuestBookPreferences(
			GetCustomerGuestBookPreferencesRequest request) {
		return jsonResponseUnmarshallar("getCustomerGuestBookPreferences", GetCustomerGuestBookPreferencesResponse.class);
	}

	@Override
	public SetCustomerGuestBookPreferencesResponse setCustomerGuestBookPreferences(
			SetCustomerGuestBookPreferencesRequest request) {
		return jsonResponseUnmarshallar("setCustomerGuestBookPreferences", SetCustomerGuestBookPreferencesResponse.class);
	}

	@Override
	public GetCustomerByIdResponse getCustomerById(
			GetCustomerByIdRequest request) {
		return jsonResponseUnmarshallar("getCustomerById", GetCustomerByIdResponse.class);
	}

	@Override
	public GetRoomPricingAndAvailabilityResponse getRoomPricingAndAvailabilityEx(
			GetRoomPricingAndAvailabilityExRequest request) {
		return jsonResponseUnmarshallar("getRoomPricingAndAvailabilityEx", GetRoomPricingAndAvailabilityResponse.class);
	}

	@Override
	public GetProgramByOperaPromoCodeResponse getProgramByOperaPromoCode(
			GetProgramByOperaPromoCodeRequest request) {
		return jsonResponseUnmarshallar("getProgramByOperaPromoCode", GetProgramByOperaPromoCodeResponse.class);
	}

	@Override
	public GetApplicableProgramsResponse getApplicablePrograms(
			GetApplicableProgramsRequest request) {
		return jsonResponseUnmarshallar("getApplicablePrograms", GetApplicableProgramsResponse.class);
	}

	@Override
	public GetCustomerOffersResponse getCustomerOffers(
			GetCustomerOffersRequest request) {
		return jsonResponseUnmarshallar("getCustomerOffers", GetCustomerOffersResponse.class);
	}

	@Override
	public UpdateRoomReservationResponse updateRoomReservation(
			UpdateRoomReservationRequest request) {
		return jsonResponseUnmarshallar("updateRoomReservation", UpdateRoomReservationResponse.class);
	}

	@Override
	public MakeRoomReservationResponse makeRoomReservation(
			MakeRoomReservationRequest request) {
		return jsonResponseUnmarshallar("makeRoomReservation", MakeRoomReservationResponse.class);
	}

	@Override
	public GetRoomComponentAvailabilityResponse getRoomComponentAvailability(
			GetRoomComponentAvailabilityRequest request) {
		return jsonResponseUnmarshallar("getRoomComponentAvailability", GetRoomComponentAvailabilityResponse.class);
	}

	@Override
	public RemoveRoomReservationResponse removeRoomReservation(
			RemoveRoomReservationRequest request) {
		return jsonResponseUnmarshallar("removeRoomReservation", RemoveRoomReservationResponse.class);
	}

	@Override
	public SaveRoomReservationResponse saveRoomReservation(
			SaveRoomReservationRequest request) {
		return jsonResponseUnmarshallar("saveRoomReservation", SaveRoomReservationResponse.class);
	}

	@Override
	public CancelRoomReservationResponse cancelRoomReservation(
			CancelRoomReservationRequest request) {
		return jsonResponseUnmarshallar("cancelRoomReservation", CancelRoomReservationResponse.class);
	}

	@Override
	public HoldBestAvailableShowTicketsResponse holdBestAvailableShowTickets(
			HoldBestAvailableShowTicketsRequest request) {
		return jsonResponseUnmarshallar("holdBestAvailableShowTickets", HoldBestAvailableShowTicketsResponse.class);
	}

	@Override
	public GetShowProgramsByArchticsPromoCodeResponse getShowProgramsByArchticsPromoCode(
			GetShowProgramsByArchticsPromoCodeRequest request) {
		return jsonResponseUnmarshallar("getShowProgramsByArchticsPromoCode", GetShowProgramsByArchticsPromoCodeResponse.class);
	}

	@Override
	public GetShowProgramEventsResponse getShowProgramEvents(
			GetShowProgramEventsRequest request) {
		return jsonResponseUnmarshallar("getShowProgramEvents", GetShowProgramEventsResponse.class);
	}

	@Override
	public GetApplicableShowProgramsResponse getApplicableShowPrograms(
			GetApplicableShowProgramsRequest request) {
		return jsonResponseUnmarshallar("getApplicableShowPrograms", GetApplicableShowProgramsResponse.class);
	}

	@Override
	public GetShowPricingAndAvailabilityResponse getShowPricingAndAvailability(
			GetShowPricingAndAvailabilityRequest request) {
		return jsonResponseUnmarshallar("getShowPricingAndAvailability", GetShowPricingAndAvailabilityResponse.class);
	}

	@Override
	public GetShowPricingAndAvailabilityResponse getShowPricingAndAvailabilityByProgram(
			GetShowPricingAndAvailabilityByProgramRequest request) {
		return jsonResponseUnmarshallar("getShowPricingAndAvailabilityByProgram", GetShowPricingAndAvailabilityResponse.class);
	}

	@Override
	public HoldSpecificShowTicketsResponse holdSpecificShowTickets(
			HoldSpecificShowTicketsRequest request) {
		return jsonResponseUnmarshallar("holdSpecificShowTickets", HoldSpecificShowTicketsResponse.class);
	}

	@Override
	public ReleaseShowTicketsResponse releaseShowTickets(
			ReleaseShowTicketsRequest request) {
		return jsonResponseUnmarshallar("releaseShowTickets", ReleaseShowTicketsResponse.class);
	}

	@Override
	public SaveShowReservationResponse saveShowReservation(
			SaveShowReservationRequest request) {
		return jsonResponseUnmarshallar("saveShowReservation", SaveShowReservationResponse.class);
	}

	@Override
	public RemoveShowReservationResponse removeShowReservation(
			RemoveShowReservationRequest request) {
		return jsonResponseUnmarshallar("removeShowReservation", RemoveShowReservationResponse.class);
	}

	@Override
	public UpdateShowReservationResponse updateShowReservation(
			UpdateShowReservationRequest request) {
		return jsonResponseUnmarshallar("updateShowReservation", UpdateShowReservationResponse.class);
	}

	@Override
	public MakeShowReservationResponse makeShowReservation(
			MakeShowReservationRequest request) {
		return jsonResponseUnmarshallar("makeShowReservation", MakeShowReservationResponse.class);
	}

	@Override
	public PrintShowTicketsResponse printShowTickets(
			PrintShowTicketsRequest request) {
		return jsonResponseUnmarshallar("printShowTickets", PrintShowTicketsResponse.class);
	}

	@Override
	public IsShowProgramApplicableResponse isShowProgramApplicable(
			IsShowProgramApplicableRequest request) {
		
		return jsonResponseUnmarshallar("isShowProgramApplicable",IsShowProgramApplicableResponse.class);
	}
	
	
	private <T> T jsonResponseUnmarshallar(String jsonFilePathKey, Class<T> clazz) {
		long delay = minMockDelay + ((long)((new SecureRandom()).nextDouble() * (maxMockDelay - minMockDelay)));
		return JsonReaderImpl.mockResponse(mockResponseLocation + "/" + jsonFilePathKey + ".json", clazz, delay);
	}

}
