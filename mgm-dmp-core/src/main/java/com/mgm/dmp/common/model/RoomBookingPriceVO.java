package com.mgm.dmp.common.model;

import java.util.Calendar;

import com.mgm.dmp.common.util.DateUtil;
import com.mgmresorts.aurora.common.RoomBooking;

/**
 * The Class RoomBookingPriceVO.
 *
 * @author Sapient
 * 
 * 	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	03/09/2014			sselvr		 Created
 * 	03/17/2014			sselvr		 review comments(Method name should be
 * 									 either create or convert)
 */
public class RoomBookingPriceVO extends AbstractDmpBaseVO {

	private static final long serialVersionUID = 6813050122709691652L;
	
	private Calendar date;
	private double basePrice;
	private double price;
	private String programId;
	private String pricingRuleId;
	private Boolean programIdIsRateTable;
	private double overridePrice;
	private String overrideProgramId;
	private String overridePricingRuleId;
	private boolean overrideProgramIdIsRateTable;

	

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 *
	 * @param date the date to set
	 */
	public void setDate(final Calendar date) {
		this.date = date;
	}

	/**
	 * Gets the base price.
	 *
	 * @return the basePrice
	 */
	public double getBasePrice() {
		return basePrice;
	}

	/**
	 * Sets the base price.
	 *
	 * @param basePrice the basePrice to set
	 */
	public void setBasePrice(final double basePrice) {
		this.basePrice = basePrice;
	}

	/**
	 * Gets the price.
	 *
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Sets the price.
	 *
	 * @param price the price to set
	 */
	public void setPrice(final double price) {
		this.price = price;
	}

	/**
	 * Gets the program id.
	 *
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}

	/**
	 * Sets the program id.
	 *
	 * @param programId the programId to set
	 */
	public void setProgramId(final String programId) {
		this.programId = programId;
	}

	/**
	 * Gets the pricing rule id.
	 *
	 * @return the pricingRuleId
	 */
	public String getPricingRuleId() {
		return pricingRuleId;
	}

	/**
	 * Sets the pricing rule id.
	 *
	 * @param pricingRuleId the pricingRuleId to set
	 */
	public void setPricingRuleId(final String pricingRuleId) {
		this.pricingRuleId = pricingRuleId;
	}

	/**
	 * Gets the program id is rate table.
	 *
	 * @return the programIdIsRateTable
	 */
	public Boolean getProgramIdIsRateTable() {
		return programIdIsRateTable;
	}

	/**
	 * Sets the program id is rate table.
	 *
	 * @param programIdIsRateTable the programIdIsRateTable to set
	 */
	public void setProgramIdIsRateTable(final Boolean programIdIsRateTable) {
		this.programIdIsRateTable = programIdIsRateTable;
	}

	/**
	 * Gets the override price.
	 *
	 * @return the overridePrice
	 */
	public double getOverridePrice() {
		return overridePrice;
	}

	/**
	 * Sets the override price.
	 *
	 * @param overridePrice the overridePrice to set
	 */
	public void setOverridePrice(final double overridePrice) {
		this.overridePrice = overridePrice;
	}

	/**
	 * Gets the override program id.
	 *
	 * @return the overrideProgramId
	 */
	public String getOverrideProgramId() {
		return overrideProgramId;
	}

	/**
	 * Sets the override program id.
	 *
	 * @param overrideProgramId the overrideProgramId to set
	 */
	public void setOverrideProgramId(final String overrideProgramId) {
		this.overrideProgramId = overrideProgramId;
	}

	/**
	 * Gets the override pricing rule id.
	 *
	 * @return the overridePricingRuleId
	 */
	public String getOverridePricingRuleId() {
		return overridePricingRuleId;
	}

	/**
	 * Sets the override pricing rule id.
	 *
	 * @param overridePricingRuleId the overridePricingRuleId to set
	 */
	public void setOverridePricingRuleId(final String overridePricingRuleId) {
		this.overridePricingRuleId = overridePricingRuleId;
	}

	/**
	 * Checks if is override program id is rate table.
	 *
	 * @return the overrideProgramIdIsRateTable
	 */
	public boolean isOverrideProgramIdIsRateTable() {
		return overrideProgramIdIsRateTable;
	}

	/**
	 * Sets the override program id is rate table.
	 *
	 * @param overrideProgramIdIsRateTable the overrideProgramIdIsRateTable to set
	 */
	public void setOverrideProgramIdIsRateTable(final boolean overrideProgramIdIsRateTable) {
		this.overrideProgramIdIsRateTable = overrideProgramIdIsRateTable;
	}

	
	/**
	 * Convert from.
	 *
	 * @param roomBooking the room booking
	 */
	public void convertFrom(final RoomBooking roomBooking) {
		this.setBasePrice(roomBooking.getBasePrice());
		// this.set(roomBooking.getCustomerPrice()); missing
		this.setDate(DateUtil.convertDateToCalander(roomBooking
				.getDate()));
		this.setOverridePrice(roomBooking.getOverridePrice());
		this.setOverridePricingRuleId(roomBooking.getOverridePricingRuleId());
		this.setOverrideProgramId(roomBooking.getOverrideProgramId());
		this.setProgramIdIsRateTable(roomBooking
				.getOverrideProgramIdIsRateTable());
		this.setPrice(roomBooking.getPrice());
		this.setPricingRuleId(roomBooking.getPricingRuleId());
		this.setProgramId(roomBooking.getProgramId());
		this.setProgramIdIsRateTable(roomBooking.getProgramIdIsRateTable());

	}

}
