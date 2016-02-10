package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgmresorts.aurora.common.CustomerAddress;
import com.mgmresorts.aurora.common.CustomerPhoneNumber;
import com.mgmresorts.aurora.common.CustomerProfile;
import com.mgmresorts.aurora.common.CustomerTitle;
import com.mgmresorts.aurora.messages.CustomerBalancesFull;

/**
 * @author ssahu6
 * 
 */
@JsonInclude(Include.NON_NULL)
public class Customer implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8276849990470510647L;

	private long id = -1;
	private int mlifeNo;
	private String hgpNo;
	private String swrrNo;
	private String tier;
	private String hgpTier;
	private String title;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String patronType;
	private boolean modifiable;
	
	private static final String NOIR_TIER_NAME = "Noir";
	
	@JsonFormat(pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date dateOfBirth;

	private int tierCredits;
	private int creditsOnCheckout;
	private Address[] address;
	private PhoneNumber[] phoneNumbers;
	private List<BalanceInfo> balanceInfos;
	private List<String> customerOffers;
	private int secretQuestionId;
	
	/**
	 * @return the secretQuestionId
	 */
	public int getSecretQuestionId() {
		return secretQuestionId;
	}

	/**
	 * @param secretQuestionId the secretQuestionId to set
	 */
	public void setSecretQuestionId(int secretQuestionId) {
		this.secretQuestionId = secretQuestionId;
	}

	private boolean isLoggedIn;
	

	public Customer(){
		
	}
	
	public Customer(long customerId){
		this.setId(customerId);
	}
	

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public final void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the mlifeNo
	 */
	public int getMlifeNo() {
		return mlifeNo;
	}

	/**
	 * @param mlifeNo
	 *            the mlifeNo to set
	 */
	public void setMlifeNo(int mlifeNo) {
		this.mlifeNo = mlifeNo;
	}

	/**
	 * @return the hgpNo
	 */
	public String getHgpNo() {
		return hgpNo;
	}

	/**
	 * @param hgpNo
	 *            the hgpNo to set
	 */
	public void setHgpNo(String hgpNo) {
		this.hgpNo = hgpNo;
	}

	/**
	 * @return the tier
	 */
	public String getTier() {
		return tier;
	}

	/**
	 * @param tier
	 *            the tier to set
	 */
	public void setTier(String tier) {
		
		String portTier = StringUtils.lowerCase(tier);
		this.tier = StringUtils.capitalize(portTier);

	}

	/**
	 * @return the hgpTier
	 */
	public String getHgpTier() {
		return hgpTier;
	}

	/**
	 * @param hgpTier
	 *            the hgpTier to set
	 */
	public void setHgpTier(String hgpTier) {
		this.hgpTier = hgpTier;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the emailAddress1
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *            the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the tierCredits
	 */
	public int getTierCredits() {
		return tierCredits;
	}

	/**
	 * @param tierCredits
	 *            the tierCredits to set
	 */
	public void setTierCredits(int tierCredits) {
		this.tierCredits = tierCredits;
	}

	/**
	 * @return the address
	 */
	public Address[] getAddress() {
		return address;
	}

	/**
	 * @param newAddress
	 *            the address to set
	 */
	public void setAddress(Address[] newAddress) {
		if (newAddress == null) {
			this.address = new Address[0];
		} else {
			this.address = Arrays.copyOf(newAddress, newAddress.length);
		}
	}

	/**
	 * @return the phoneNumbers
	 */
	public PhoneNumber[] getPhoneNumbers() {
		return phoneNumbers;
	}

	/**
	 * @param phoneNumbers
	 *            the phoneNumbers to set
	 */
	public void setPhoneNumbers(PhoneNumber[] newPhoneNumbers) {
		if (newPhoneNumbers == null) {
			this.phoneNumbers = new PhoneNumber[0];
		} else {
			this.phoneNumbers = Arrays.copyOf(newPhoneNumbers,
					newPhoneNumbers.length);
		}
	}

	/**
	 * @return the balanceInfos
	 */
	public List<BalanceInfo> getBalanceInfos() {
		return balanceInfos;
	}

	/**
	 * @param balanceInfos
	 *            the balanceInfos to set
	 */
	public void setBalanceInfos(List<BalanceInfo> balanceInfos) {
		this.balanceInfos = balanceInfos;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth
	 *            the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * Creates the CustomerProfile request object and set CustomerProfileVO
	 * values.
	 * 
	 * @return the customer profile
	 */
	public CustomerProfile createTo() {

		CustomerProfile customerProfile = CustomerProfile.create(); 
		int count;
		customerProfile.setId(getId());
		customerProfile.setFirstName(getFirstName());
		customerProfile.setLastName(getLastName());
		customerProfile.setMlifeNo(getMlifeNo());

		if (null != getTitle()) {
			customerProfile.setTitle(CustomerTitle.valueOf(getTitle()));
		}

		customerProfile.setEmailAddress1(getEmailAddress());
		customerProfile.setHgpNo(getHgpNo());
		customerProfile.setTier(getTier());
		customerProfile.setHgpTier(getHgpTier());
		customerProfile.setSwrrNo(getSwrrNo());
		customerProfile.setDateOfBirth(getDateOfBirth());

		if (null != this.getPhoneNumbers() && this.getPhoneNumbers().length > 0) {
			count = 0;
			CustomerPhoneNumber[] customerPhoneNumberArr = new CustomerPhoneNumber[this
					.getPhoneNumbers().length];

			for (final PhoneNumber phoneNumber : this.getPhoneNumbers()) {
				customerPhoneNumberArr[count++] = phoneNumber.createTo();
			}
			customerProfile.setPhoneNumbers(customerPhoneNumberArr);
		}

		if (null != this.getAddress() && this.getAddress().length > 0) {
			count = 0;
			CustomerAddress[] customerAddressArr = new CustomerAddress[this
					.getAddress().length];

			for (final Address tempAddress : this.getAddress()) {
				customerAddressArr[count++] = tempAddress.createTo();
			}
			customerProfile.setAddresses(customerAddressArr);
		}
		return customerProfile;
	}

	/**
	 * 
	 * Convert from CustomerProfile response to CustomerProfileVO.
	 * 
	 * @param customerProfile
	 *            the customer profile
	 */
	public void convertFrom(final CustomerProfile customerProfile) {

		PhoneNumber[] phoneNumberArr;
		Address[] addressArr;
		PhoneNumber phoneNumber;
		Address lAddress;
		int count = 0;

		this.setId(customerProfile.getId());
		this.setFirstName(customerProfile.getFirstName());
		this.setLastName(customerProfile.getLastName());
		this.setMlifeNo(customerProfile.getMlifeNo());
		this.setHgpNo(customerProfile.getHgpNo());
		this.setEmailAddress(customerProfile.getEmailAddress1());
		this.setTier(customerProfile.getTier());
		this.setHgpTier(customerProfile.getHgpTier());
		this.setSwrrNo(customerProfile.getSwrrNo());
		this.setDateOfBirth(customerProfile.getDateOfBirth());
		if(null != customerProfile.getPatronType()) {
			this.setPatronType(customerProfile.getPatronType().toString());
		}

		if (null != customerProfile.getAddresses()
				&& customerProfile.getAddresses().length > 0) {
			addressArr = new Address[customerProfile.getAddresses().length];
			for (final CustomerAddress customerAddress : customerProfile
					.getAddresses()) {
				lAddress = new Address();
				lAddress.convortFrom(customerAddress);
				addressArr[count++] = lAddress;
			}
			this.setAddress(addressArr);
		}

		if (null != customerProfile.getPhoneNumbers()
				&& customerProfile.getPhoneNumbers().length > 0) {
			count = 0;
			phoneNumberArr = new PhoneNumber[customerProfile.getPhoneNumbers().length];
			for (final CustomerPhoneNumber customerPhoneNumber : customerProfile
					.getPhoneNumbers()) {
				phoneNumber = new PhoneNumber();
				phoneNumber.convertFrom(customerPhoneNumber);
				phoneNumberArr[count++] = phoneNumber;
			}
			this.setPhoneNumbers(phoneNumberArr);
		}

		if (null != customerProfile.getTitle()) {
			this.setTitle(customerProfile.getTitle().name());
		}
		this.setModifiable(customerProfile.getModifiable());
	}


	/**
	 * Convert from.
	 *
	 * @param customerBalancesFull the customer balances full
	 */
	public void convertFrom(final CustomerBalancesFull customerBalancesFull) {
		
		if (null != customerBalancesFull) {
			List<BalanceInfo> balanceInfosList = new ArrayList<BalanceInfo>();
			BalanceInfo balanceInfo = null;
			if(customerBalancesFull.getTierCredits()>0) {
				this.setTierCredits(customerBalancesFull.getTierCredits());
			} else {
				this.setTierCredits(0);
			}
			
			int noirExpressCompsLimit = NumberUtils.toInt(
					ApplicationPropertyUtil
							.getProperty("mlifetier.noir.expresscomps.limit"),
					10000);
			int nonNoirExpressCompsLimit = NumberUtils
					.toInt(ApplicationPropertyUtil
							.getProperty("mlifetier.nonnoir.expresscomps.limit"),
							5000);
			// ExpressComps
			balanceInfo = new BalanceInfo();
			balanceInfo.setBalanceType(BalanceType.ExpressComps);
			if (customerBalancesFull.getSecondCompBalanceLinked() > 0) {
				// MGPT 8801
				double receivedPoints = customerBalancesFull
						.getSecondCompBalanceLinked();
				if (NOIR_TIER_NAME.equalsIgnoreCase(getTier())) {
					if (receivedPoints > noirExpressCompsLimit) {
						receivedPoints = noirExpressCompsLimit;
					}
				} else {
					if (receivedPoints > nonNoirExpressCompsLimit) {
						receivedPoints = nonNoirExpressCompsLimit;
					}
				}
				balanceInfo.setBalanceAmount(new Double(receivedPoints));
			} else {
				balanceInfo.setBalanceAmount(new Double(0));
			}

			balanceInfosList.add(balanceInfo);

			// PointPlay
			balanceInfo = new BalanceInfo();
			balanceInfo.setBalanceType(BalanceType.PointPlay);
			double pointsPlay = (double) customerBalancesFull.getPointBalance() / 100;
			if (pointsPlay > 0) {
				balanceInfo.setBalanceAmount(new Double(pointsPlay));
			} else {
				balanceInfo.setBalanceAmount(new Double(0));
			}
			balanceInfosList.add(balanceInfo);

			// FreePlay
			balanceInfo = new BalanceInfo();
			balanceInfo.setBalanceType(BalanceType.FreePlay);
			double freePlay = customerBalancesFull.getXtraCreditBalanceGlobal()
					+ customerBalancesFull.getXtraCreditBalanceLocal();
			if (freePlay > 0) {
				balanceInfo.setBalanceAmount(freePlay);
			} else {
				balanceInfo.setBalanceAmount(new Double(0));
			}

			balanceInfosList.add(balanceInfo);

			// HGSPoints
			balanceInfo = new BalanceInfo();
			balanceInfo.setBalanceType(BalanceType.HGSPoints);

			if (customerBalancesFull.getGiftPointsLinked() > 0) {
				balanceInfo.setBalanceAmount(customerBalancesFull
						.getGiftPointsLinked());
			} else {
				balanceInfo.setBalanceAmount(new Double(0));
			}
			balanceInfosList.add(balanceInfo);

			this.setBalanceInfos(balanceInfosList);
		}
		
	}

	/**
	 * @param availability
	 */
	@JsonIgnore
	public void excludeBalanceInfos() {
		setBalanceInfos(null);
	}

	/**
	 * @return the isLoggedIn
	 */
	public boolean getIsLoggedIn() {
		return isLoggedIn;
	}

	/**
	 * @param isLoggedIn the isLoggedIn to set
	 */
	public void setIsLoggedIn(Boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	/**
	 * @return the customerOffers
	 */
	public List<String> getCustomerOffers() {
		return customerOffers;
	}

	/**
	 * @param customerOffers the customerOffers to set
	 */
	public void setCustomerOffers(List<String> customerOffers) {
		this.customerOffers = customerOffers;
	}

	/**
	 * @return the patronType
	 */
	public String getPatronType() {
		return patronType;
	}

	/**
	 * @param patronType the patronType to set
	 */
	public void setPatronType(String patronType) {
		this.patronType = patronType;
	}

    /**
     * @return the swrrNo
     */
    public String getSwrrNo() {
        return swrrNo;
    }

    /**
     * @param swrrNo the swrrNo to set
     */
    public void setSwrrNo(String swrrNo) {
        this.swrrNo = swrrNo;
    }

    /**
     * @return the creditsOnCheckout
     */
    public int getCreditsOnCheckout() {
        return creditsOnCheckout;
    }

    /**
     * @param creditsOnCheckout the creditsOnCheckout to set
     */
    public void setCreditsOnCheckout(int creditsOnCheckout) {
        this.creditsOnCheckout = creditsOnCheckout;
    }

	/**
	 * @return the modifiable
	 */
	public boolean isModifiable() {
		return modifiable;
	}

	/**
	 * @param modifiable the modifiable to set
	 */
	public void setModifiable(boolean modifiable) {
		this.modifiable = modifiable;
	}

}
