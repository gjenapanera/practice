package com.mgm.dmp.dao.impl.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpSystemException;
import com.mgm.dmp.dao.AuroraConnectionDAO;
import com.mgmresorts.aurora.messages.*;

public class AuroraMaintenanceModeDAOImpl implements AuroraConnectionDAO {
	
	protected static final Logger LOG = LoggerFactory.getLogger(AuroraMaintenanceModeDAOImpl.class);
	
	public AuroraMaintenanceModeDAOImpl() {
	}

	@Override
	public void closeConnection() {
	}

	@Override
	public GetCustomerByWebCredentialsResponse getCustomerByWebCredentials(
			GetCustomerByWebCredentialsRequest request) {
		
		return serveMaintenanceMode(GetCustomerByWebCredentialsResponse.class);
	}

	@Override
	public GetDiningAvailabilityResponse getDiningAvailability(
			GetDiningAvailabilityRequest request) {
		
		return serveMaintenanceMode(GetDiningAvailabilityResponse.class);
	}

	@Override
	public SaveDiningReservationResponse saveDiningReservation(
			SaveDiningReservationRequest request) {
		return serveMaintenanceMode(SaveDiningReservationResponse.class);
	}

	@Override
	public MakeDiningReservationResponse makeDiningReservation(
			MakeDiningReservationRequest request) {
		return serveMaintenanceMode(MakeDiningReservationResponse.class);
	}

	@Override
	public CancelDiningReservationResponse cancelDiningReservation(
			CancelDiningReservationRequest request) {
		return serveMaintenanceMode(CancelDiningReservationResponse.class);
	}

	@Override
	public RemoveDiningReservationResponse removeDiningReservation(
			RemoveDiningReservationRequest request) {
		return serveMaintenanceMode(RemoveDiningReservationResponse.class);
	}

	@Override
	public IsProgramApplicableResponse isProgramApplicable(
			IsProgramApplicableRequest request) {
		return serveMaintenanceMode(IsProgramApplicableResponse.class);
	}

	@Override
	public CreateCustomerItineraryResponse createCustomerItinerary(
			CreateCustomerItineraryRequest request) {
		return serveMaintenanceMode(CreateCustomerItineraryResponse.class);
	}

	@Override
	public AddCustomerItineraryResponse addCustomerItinerary(
			AddCustomerItineraryRequest request) {
		return serveMaintenanceMode(AddCustomerItineraryResponse.class);
	}

	@Override
	public UpdateCustomerItineraryResponse updateCustomerItinerary(
			UpdateCustomerItineraryRequest request) {
		return serveMaintenanceMode(UpdateCustomerItineraryResponse.class);
	}

	@Override
	public GetCustomerItinerariesResponse getCustomerItineraries(
			GetCustomerItinerariesRequest request) {
		return serveMaintenanceMode(GetCustomerItinerariesResponse.class);
	}

	@Override
	public GetCustomerItineraryByDiningConfirmationNumberResponse getCustomerItineraryByDiningConfirmationNumber(
			GetCustomerItineraryByDiningConfirmationNumberRequest request) {
		return serveMaintenanceMode(GetCustomerItineraryByDiningConfirmationNumberResponse.class);
	}

	@Override
	public GetCustomerItineraryByRoomConfirmationNumberResponse getCustomerItineraryByRoomConfirmationNumber(
			GetCustomerItineraryByRoomConfirmationNumberRequest request) {
		return serveMaintenanceMode(GetCustomerItineraryByRoomConfirmationNumberResponse.class);
	}

	@Override
	public GetCustomerItineraryByShowConfirmationNumberResponse getCustomerItineraryByShowConfirmationNumber(
			GetCustomerItineraryByShowConfirmationNumberRequest request) {
		return serveMaintenanceMode(GetCustomerItineraryByShowConfirmationNumberResponse.class);
	}

	@Override
	public AddCustomerResponse addCustomer(AddCustomerRequest request) {
		return serveMaintenanceMode(AddCustomerResponse.class);
	}

	@Override
	public UpdateCustomerResponse updateCustomer(UpdateCustomerRequest request) {
		return serveMaintenanceMode(UpdateCustomerResponse.class);
	}

	@Override
	public SearchCustomerResponse searchCustomer(SearchCustomerRequest request) {
		return serveMaintenanceMode(SearchCustomerResponse.class);
	}

	@Override
	public CreateCustomerWebCredentialsResponse createCustomerWebCredentials(
			CreateCustomerWebCredentialsRequest request) {
		return serveMaintenanceMode(CreateCustomerWebCredentialsResponse.class);
	}

	@Override
	public ActivateCustomerWebCredentialsResponse activateCustomerWebCredentials(
			ActivateCustomerWebCredentialsRequest request) {
		return serveMaintenanceMode(ActivateCustomerWebCredentialsResponse.class);
	}

	@Override
	public ValidateCustomerWebCredentialsResponse validateCustomerWebCredentials(
			ValidateCustomerWebCredentialsRequest request) {
		return serveMaintenanceMode(ValidateCustomerWebCredentialsResponse.class);
	}

	@Override
	public ResetCustomerWebPasswordResponse resetCustomerWebPassword(
			ResetCustomerWebPasswordRequest request) {
		return serveMaintenanceMode(ResetCustomerWebPasswordResponse.class);
	}

	@Override
	public ChangeCustomerWebEmailAddressResponse changeCustomerWebEmailAddress(
			ChangeCustomerWebEmailAddressRequest request) {
		return serveMaintenanceMode(ChangeCustomerWebEmailAddressResponse.class);
	}

	@Override
	public ChangeCustomerWebPasswordAdminResponse changeCustomerWebPasswordAdmin(
			ChangeCustomerWebPasswordAdminRequest request) {
		return serveMaintenanceMode(ChangeCustomerWebPasswordAdminResponse.class);
	}

	@Override
	public ChangeCustomerWebSecretQuestionAnswerResponse changeCustomerWebSecretQuestionAnswer(
			ChangeCustomerWebSecretQuestionAnswerRequest request) {
		return serveMaintenanceMode(ChangeCustomerWebSecretQuestionAnswerResponse.class);
	}

	@Override
	public ValidateCustomerWebSecretAnswerResponse validateCustomerWebSecretAnswer(
			ValidateCustomerWebSecretAnswerRequest request) {
		return serveMaintenanceMode(ValidateCustomerWebSecretAnswerResponse.class);
	}

	@Override
	public GetCustomerBalancesFullResponse getCustomerBalancesFull(
			GetCustomerBalancesFullRequest request) {
		return serveMaintenanceMode(GetCustomerBalancesFullResponse.class);
	}

	@Override
	public GetCustomerTaxInformationResponse getCustomerTaxInformation(
			GetCustomerTaxInformationRequest request) {
		return serveMaintenanceMode(GetCustomerTaxInformationResponse.class);
	}

	@Override
	public SendEmailResponse sendEmail(SendEmailRequest request) {
		return serveMaintenanceMode(SendEmailResponse.class);
	}

	@Override
	public GetCustomerGuestBookPreferencesResponse getCustomerGuestBookPreferences(
			GetCustomerGuestBookPreferencesRequest request) {
		return serveMaintenanceMode(GetCustomerGuestBookPreferencesResponse.class);
	}

	@Override
	public SetCustomerGuestBookPreferencesResponse setCustomerGuestBookPreferences(
			SetCustomerGuestBookPreferencesRequest request) {
		return serveMaintenanceMode(SetCustomerGuestBookPreferencesResponse.class);
	}

	@Override
	public GetCustomerByIdResponse getCustomerById(
			GetCustomerByIdRequest request) {
		return serveMaintenanceMode(GetCustomerByIdResponse.class);
	}

	@Override
	public GetRoomPricingAndAvailabilityResponse getRoomPricingAndAvailabilityEx(
			GetRoomPricingAndAvailabilityExRequest request) {
		return serveMaintenanceMode(GetRoomPricingAndAvailabilityResponse.class);
	}

	@Override
	public GetProgramByOperaPromoCodeResponse getProgramByOperaPromoCode(
			GetProgramByOperaPromoCodeRequest request) {
		return serveMaintenanceMode(GetProgramByOperaPromoCodeResponse.class);
	}

	@Override
	public GetApplicableProgramsResponse getApplicablePrograms(
			GetApplicableProgramsRequest request) {
		return serveMaintenanceMode(GetApplicableProgramsResponse.class);
	}

	@Override
	public GetCustomerOffersResponse getCustomerOffers(
			GetCustomerOffersRequest request) {
		return serveMaintenanceMode(GetCustomerOffersResponse.class);
	}

	@Override
	public UpdateRoomReservationResponse updateRoomReservation(
			UpdateRoomReservationRequest request) {
		return serveMaintenanceMode(UpdateRoomReservationResponse.class);
	}

	@Override
	public MakeRoomReservationResponse makeRoomReservation(
			MakeRoomReservationRequest request) {
		return serveMaintenanceMode(MakeRoomReservationResponse.class);
	}

	@Override
	public GetRoomComponentAvailabilityResponse getRoomComponentAvailability(
			GetRoomComponentAvailabilityRequest request) {
		return serveMaintenanceMode(GetRoomComponentAvailabilityResponse.class);
	}

	@Override
	public RemoveRoomReservationResponse removeRoomReservation(
			RemoveRoomReservationRequest request) {
		return serveMaintenanceMode(RemoveRoomReservationResponse.class);
	}

	@Override
	public SaveRoomReservationResponse saveRoomReservation(
			SaveRoomReservationRequest request) {
		return serveMaintenanceMode(SaveRoomReservationResponse.class);
	}

	@Override
	public CancelRoomReservationResponse cancelRoomReservation(
			CancelRoomReservationRequest request) {
		return serveMaintenanceMode(CancelRoomReservationResponse.class);
	}

	@Override
	public HoldBestAvailableShowTicketsResponse holdBestAvailableShowTickets(
			HoldBestAvailableShowTicketsRequest request) {
		return serveMaintenanceMode(HoldBestAvailableShowTicketsResponse.class);
	}

	@Override
	public GetShowProgramsByArchticsPromoCodeResponse getShowProgramsByArchticsPromoCode(
			GetShowProgramsByArchticsPromoCodeRequest request) {
		return serveMaintenanceMode(GetShowProgramsByArchticsPromoCodeResponse.class);
	}

	@Override
	public GetShowProgramEventsResponse getShowProgramEvents(
			GetShowProgramEventsRequest request) {
		return serveMaintenanceMode(GetShowProgramEventsResponse.class);
	}

	@Override
	public GetApplicableShowProgramsResponse getApplicableShowPrograms(
			GetApplicableShowProgramsRequest request) {
		return serveMaintenanceMode(GetApplicableShowProgramsResponse.class);
	}

	@Override
	public GetShowPricingAndAvailabilityResponse getShowPricingAndAvailability(
			GetShowPricingAndAvailabilityRequest request) {
		return serveMaintenanceMode(GetShowPricingAndAvailabilityResponse.class);
	}

	@Override
	public GetShowPricingAndAvailabilityResponse getShowPricingAndAvailabilityByProgram(
			GetShowPricingAndAvailabilityByProgramRequest request) {
		return serveMaintenanceMode(GetShowPricingAndAvailabilityResponse.class);
	}

	@Override
	public HoldSpecificShowTicketsResponse holdSpecificShowTickets(
			HoldSpecificShowTicketsRequest request) {
		return serveMaintenanceMode(HoldSpecificShowTicketsResponse.class);
	}

	@Override
	public ReleaseShowTicketsResponse releaseShowTickets(
			ReleaseShowTicketsRequest request) {
		return serveMaintenanceMode(ReleaseShowTicketsResponse.class);
	}

	@Override
	public SaveShowReservationResponse saveShowReservation(
			SaveShowReservationRequest request) {
		return serveMaintenanceMode(SaveShowReservationResponse.class);
	}

	@Override
	public RemoveShowReservationResponse removeShowReservation(
			RemoveShowReservationRequest request) {
		return serveMaintenanceMode(RemoveShowReservationResponse.class);
	}

	@Override
	public UpdateShowReservationResponse updateShowReservation(
			UpdateShowReservationRequest request) {
		return serveMaintenanceMode(UpdateShowReservationResponse.class);
	}

	@Override
	public MakeShowReservationResponse makeShowReservation(
			MakeShowReservationRequest request) {
		return serveMaintenanceMode(MakeShowReservationResponse.class);
	}

	@Override
	public PrintShowTicketsResponse printShowTickets(
			PrintShowTicketsRequest request) {
		return serveMaintenanceMode(PrintShowTicketsResponse.class);
	}

	@Override
	public IsShowProgramApplicableResponse isShowProgramApplicable(
			IsShowProgramApplicableRequest request) {
		
		return serveMaintenanceMode(IsShowProgramApplicableResponse.class);
	}
	
	
	private <T> T serveMaintenanceMode(Class<T> clazz) {
		throw new DmpSystemException(DMPErrorCode.SYSTEM_DOWN, DmpCoreConstant.AURORA, null);
	}

}
