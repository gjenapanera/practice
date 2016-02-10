/**
 * 
 */
package com.mgm.dmp.dao.impl.helper;

import org.springframework.stereotype.Component;

import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.CreateGuestBookRequest;
import com.mgm.dmp.common.vo.CustomerPreferencesRequest;

/**
 * @author Aditya
 *
 */
@Component
public class AccountsDAOHelper {
	
	public CreateCustomerRequest convertFrom(CreateGuestBookRequest createGuestBookRequest){
		CreateCustomerRequest createCustomerRequest = null;
		if(null!=createGuestBookRequest){
			createCustomerRequest = new CreateCustomerRequest();
			createCustomerRequest.setAddressType(createGuestBookRequest.getAddressType());
			createCustomerRequest.setCity(createGuestBookRequest.getCity());
			createCustomerRequest.setCountry(createGuestBookRequest.getCountry());
			createCustomerRequest.setCustomerEmail(createGuestBookRequest.getCustomerEmail());
			createCustomerRequest.setDateOfBirth(createGuestBookRequest.getDateOfBirth());
			createCustomerRequest.setEnroll(createGuestBookRequest.isEnroll());
			createCustomerRequest.setFirstName(createGuestBookRequest.getFirstName());
			createCustomerRequest.setLastName(createGuestBookRequest.getLastName());
			createCustomerRequest.setHgpNo(createGuestBookRequest.getHgpNo());
			createCustomerRequest.setPatronType(createGuestBookRequest.getPatronType());
			createCustomerRequest.setMlifeNo(createGuestBookRequest.getMlifeNo());
			createCustomerRequest.setCustomerId(createGuestBookRequest.getCustomerId());
			createCustomerRequest.setNotSearchUserByMlifeNo(createGuestBookRequest.isNotSearchUserByMlifeNo());
			createCustomerRequest.setPhoneNumber(createGuestBookRequest.getPhoneNumber());
			createCustomerRequest.setPhoneType(createGuestBookRequest.getPhoneType());
			createCustomerRequest.setPostalCode(createGuestBookRequest.getPostalCode());
			createCustomerRequest.setPropertyId(createGuestBookRequest.getPropertyId());
			createCustomerRequest.setState(createGuestBookRequest.getState());
			createCustomerRequest.setStreet1(createGuestBookRequest.getStreet1());
			createCustomerRequest.setStreet2(createGuestBookRequest.getStreet2());
			createCustomerRequest.setSwrrNo(createGuestBookRequest.getSwrrNo());
		}
		return createCustomerRequest;
	}
	
	public CustomerPreferencesRequest convertTo(CreateGuestBookRequest createGuestBookRequest){
		CustomerPreferencesRequest customerPreferencesRequest = null;
		if(null!=createGuestBookRequest){
			customerPreferencesRequest = new CustomerPreferencesRequest();
			customerPreferencesRequest.setCommunicationPreferences(createGuestBookRequest.getCommunicationPreferences());
			customerPreferencesRequest.setCustomerEmail(createGuestBookRequest.getCustomerEmail());
			customerPreferencesRequest.setCustomerId(createGuestBookRequest.getCustomerId());
			customerPreferencesRequest.setNotSearchUserByMlifeNo(createGuestBookRequest.isNotSearchUserByMlifeNo());
			customerPreferencesRequest.setPreferredProperties(createGuestBookRequest.getPreferredProperties());
			customerPreferencesRequest.setPropertyId(createGuestBookRequest.getPropertyId());
			customerPreferencesRequest.setReceivePartnerOffers(createGuestBookRequest.isReceivePartnerOffers());
		}
		return customerPreferencesRequest;
	}

}
