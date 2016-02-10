package com.mgm.dmp.common.vo;

import javax.validation.constraints.NotNull;

/**
 * The Class CustomerTaxInformationRequest.
 * @author Sapient
 * 
 * 	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	06/13/2014			sselvr		 Created
 */
public class CustomerTaxInformationRequest  extends AbstractBaseRequest {

	private static final long serialVersionUID = -2426902028370122105L;
	
	@NotNull(message="invalid.year", groups={TaxInfoValidation.class})
	private int year;
	
	@NotNull(message="invalid.quarter", groups={TaxInfoValidation.class})
	private int quarter;

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}
	/**
	 * @return the quarter
	 */
	public int getQuarter() {
		return quarter;
	}
	/**
	 * @param quarter the quarter to set
	 */
	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}
	
	public interface TaxInfoValidation {}
}
