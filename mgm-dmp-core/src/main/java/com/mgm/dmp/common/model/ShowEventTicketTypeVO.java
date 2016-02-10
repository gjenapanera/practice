package com.mgm.dmp.common.model;

import java.util.ArrayList;
import java.util.List;

import com.mgmresorts.aurora.messages.ShowEventTicketType;

/**
 * The Class ShowEventTicketTypeVO.
 *
 * @author Sapient
 * 
 *	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	03/09/2014			sselvr		 Created
 * 	03/17/2014			sselvr		 review comments(Method name should be
 * 									 either create or convert)
 */
public class ShowEventTicketTypeVO extends AbstractDmpBaseVO {

	private static final long serialVersionUID = -1550862221086188992L;
	
	private String code;
	private double price;
	private String pricingMethod;
	private List<ShowEventTicketTypeVO> ticketTypes;

	/**
	 * Gets the code.
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code            the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
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
	 * @param price            the price to set
	 */
	public void setPrice(final double price) {
		this.price = price;
	}

	/**
	 * Gets the pricing method.
	 *
	 * @return the pricingMethod
	 */
	public String getPricingMethod() {
		return pricingMethod;
	}

	/**
	 * Sets the pricing method.
	 *
	 * @param pricingMethod            the pricingMethod to set
	 */
	public void setPricingMethod(final String pricingMethod) {
		this.pricingMethod = pricingMethod;
	}

	/**
	 * Gets the ticket types.
	 *
	 * @return the ticketTypes
	 */
	public List<ShowEventTicketTypeVO> getTicketTypes() {
		return ticketTypes;
	}

	/**
	 * Sets the ticket types.
	 *
	 * @param ticketTypes            the ticketTypes to set
	 */
	public void setTicketTypes(final List<ShowEventTicketTypeVO> ticketTypes) {
		this.ticketTypes = ticketTypes;
	}
	
	/**
	 * Creates the from.
	 *
	 * @param showEventTicketTypeArr the show event ticket type arr
	 * @return the list
	 */
	public List<ShowEventTicketTypeVO> createFrom(final ShowEventTicketType[] showEventTicketTypeArr){
		final List<ShowEventTicketTypeVO> showEventTicketTypeVOs = new ArrayList<ShowEventTicketTypeVO>();
			for(final ShowEventTicketType showEventTicketType : showEventTicketTypeArr){
				if(null != showEventTicketType) {
					showEventTicketTypeVOs.add(createFrom(showEventTicketType));
				}
			}
			return showEventTicketTypeVOs;
	 }
	 
	/**
	 * Creates the from.
	 *
	 * @param showEventTicketType the show event ticket type
	 * @return the show event ticket type vo
	 */
	public ShowEventTicketTypeVO createFrom(final ShowEventTicketType showEventTicketType){
		ShowEventTicketTypeVO showEventTicketTypeVO = new ShowEventTicketTypeVO();
		showEventTicketTypeVO.setCode(showEventTicketType.getCode());
		showEventTicketTypeVO.setPrice(showEventTicketType.getPrice());
		showEventTicketTypeVO.setPricingMethod(showEventTicketType.getPricingMethod());
		return showEventTicketTypeVO;
	}

}
