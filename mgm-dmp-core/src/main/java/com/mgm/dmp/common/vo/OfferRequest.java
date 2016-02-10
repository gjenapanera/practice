/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ssahu6
 * 
 */
@JsonInclude(Include.NON_NULL)
public class OfferRequest extends AbstractBaseRequest {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3907730973859783596L;

	public enum OfferType {
		SHOW(-1), ROOM(-1);

		private int count = -1;

		OfferType(int num) {
			count = num;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int num) {
			count = num;
		}
	}

	@NotNull(message = "invalid.offer.type", groups = { Default.class })
	@Size(message = "invalid.offer.type", min = 1, groups = { Default.class })
	private List<OfferType> offerTypes;

	@NotNull(message = "invalid.program.id", groups = { EligibleOfferValidation.class })
	private String programId;
	private String programType;
	
	private Date offerStartDate;
	private Date offerEndDate;
	private int numAdults;
	private Date bookDate;

	private boolean filterViewable = false;
	
	private boolean filterBookable = true;
	
	/**
	 * @return the filterViewable
	 */
	public boolean isFilterViewable() {
		return filterViewable;
	}

	/**
	 * @param filterViewable the filterViewable to set
	 */
	public void setFilterViewable(boolean filterViewable) {
		this.filterViewable = filterViewable;
	}

	/**
	 * @return the filterBookable
	 */
	public boolean isFilterBookable() {
		return filterBookable;
	}

	/**
	 * @param filterBookable the filterBookable to set
	 */
	public void setFilterBookable(boolean filterBookable) {
		this.filterBookable = filterBookable;
	}

	
	/**
	 * @return the bookDate
	 */
	public Date getBookDate() {
		return bookDate;
	}

	/**
	 * @param bookDate the bookDate to set
	 */
	public void setBookDate(Date bookDate) {
		this.bookDate = bookDate;
	}

	/**
	 * @return the offerType
	 */
	public List<OfferType> getOfferTypes() {
		return offerTypes;
	}

	/**
	 * @param offerType
	 *            the offerType to set
	 */
	public void setOfferTypes(List<OfferType> offerTypes) {
		this.offerTypes = offerTypes;
	}

	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}

	/**
	 * @param programId
	 *            the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}

	/**
	 * @return the programType
	 */
	public String getProgramType() {
		return programType;
	}

	/**
	 * @param programType the programType to set
	 */
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	
	/**
	 * @return the offerStartDate
	 */
	public Date getOfferStartDate() {
		return offerStartDate;
	}

	/**
	 * @param offerStartDate
	 *            the offerStartDate to set
	 */
	public void setOfferStartDate(Date offerStartDate) {
		this.offerStartDate = offerStartDate;
	}

	/**
	 * @return the offerEndDate
	 */
	public Date getOfferEndDate() {
		return offerEndDate;
	}

	/**
	 * @param offerEndDate
	 *            the offerEndDate to set
	 */
	public void setOfferEndDate(Date offerEndDate) {
		this.offerEndDate = offerEndDate;
	}

	/**
	 * @return the numAdults
	 */
	public int getNumAdults() {
		return numAdults;
	}

	/**
	 * @param numAdults
	 *            the numAdults to set
	 */
	public void setNumAdults(int numAdults) {
		this.numAdults = numAdults;
	}

	public interface EligibleOfferValidation extends Default {
	}

}
