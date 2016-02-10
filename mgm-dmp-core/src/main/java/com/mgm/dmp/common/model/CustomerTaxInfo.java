package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgmresorts.aurora.messages.CustomerTaxBySite;
import com.mgmresorts.aurora.messages.CustomerTaxInformation;

/**
 * The Class CustomerTaxInfo.
 * @author Sapient
 * 
 * 	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	06/13/2014			sselvr		 Created
 */
public class CustomerTaxInfo implements Serializable {

	private static final long serialVersionUID = -7507717720865387543L;
	private String propertyID;
	private String propertyName;
	private Price slotWin;
	private Price tableWin;
	private Price totalWin;
	private Map<String, CustomerTaxInfo> customerTaxInfoMap;
	/**
	 * @return the propertyID
	 */
	public String getPropertyID() {
		return propertyID;
	}
	/**
	 * @param propertyID the propertyID to set
	 */
	public void setPropertyID(String propertyID) {
		this.propertyID = propertyID;
	}
	
	/**
	 * @return the slotWin
	 */
	public Price getSlotWin() {
		return slotWin;
	}
	/**
	 * @param slotWin the slotWin to set
	 */
	public void setSlotWin(Double slotWin) {
		this.slotWin = new USD(slotWin);
	}
	/**
	 * @return the tableWin
	 */
	public Price getTableWin() {
		return tableWin;
	}
	/**
	 * @param tableWin the tableWin to set
	 */
	public void setTableWin(Double tableWin) {
		this.tableWin = new USD(tableWin);
	}
	/**
	 * @return the totalWin
	 */
	public Price getTotalWin() {
		return totalWin;
	}
	/**
	 * @param totalWin the totalWin to set
	 */
	public void setTotalWin(Double totalWin) {
		this.totalWin = new USD(totalWin);
	}
	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}
	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
	@JsonIgnore
	public List<CustomerTaxInfo> convertFrom(final CustomerTaxInformation customerTaxInformation){
		
		List<CustomerTaxInfo> customerTaxInfoVOList = new ArrayList<CustomerTaxInfo>();
		CustomerTaxInfo customerTaxInfo;
		CustomerTaxBySite[] customerTaxBySiteArr = customerTaxInformation.getSiteTotals();	
		
		for(final CustomerTaxBySite customerTaxBySite : customerTaxBySiteArr){
			
			if(getCustomerTaxInfoMap().containsKey(customerTaxBySite.getSiteId())){	
				//This loop is execute only full quarter scenario
			customerTaxInfo = getCustomerTaxInfoMap().get(customerTaxBySite.getSiteId());
			customerTaxInfo.setTableWin(customerTaxInfo.getTableWin().getValue()+customerTaxBySite.getTableWin());
			customerTaxInfo.setSlotWin(customerTaxInfo.getSlotWin().getValue()+customerTaxBySite.getSlotWin());	
			customerTaxInfo.setTotalWin(customerTaxInfo.getTotalWin().getValue()+customerTaxBySite.getTotalWin());
			} else {
				//This loop is execute both full  and unique quarter flow
				customerTaxInfo = new CustomerTaxInfo();
				customerTaxInfo.setPropertyID(customerTaxBySite.getSiteId());
				customerTaxInfo.setTableWin(customerTaxBySite.getTableWin());
				customerTaxInfo.setSlotWin(customerTaxBySite.getSlotWin());	
				customerTaxInfo.setTotalWin(customerTaxBySite.getTotalWin());
				customerTaxInfoVOList.add(customerTaxInfo);
				getCustomerTaxInfoMap().put(customerTaxBySite.getSiteId(), customerTaxInfo);
			}
		}
		
		if(getCustomerTaxInfoMap().containsKey(DmpCoreConstant.CUSTOMER_TAX_INFO_TOTAL)){
			//This loop is execute only full quarter scenario
			customerTaxInfo = getCustomerTaxInfoMap().get(DmpCoreConstant.CUSTOMER_TAX_INFO_TOTAL);
			customerTaxInfo.setTableWin(customerTaxInfo.getTableWin().getValue()+customerTaxInformation.getTotalTableWin());
			customerTaxInfo.setSlotWin(customerTaxInfo.getSlotWin().getValue()+customerTaxInformation.getTotalSlotWin());	
			customerTaxInfo.setTotalWin(customerTaxInfo.getTotalWin().getValue()+customerTaxInformation.getTotalWin());
		} else {
			//This loop is execute both full  and unique quarter flow
		customerTaxInfo = new CustomerTaxInfo();
		customerTaxInfo.setPropertyName(DmpCoreConstant.CUSTOMER_TAX_INFO_TOTAL);
		customerTaxInfo.setTableWin(customerTaxInformation.getTotalTableWin());
		customerTaxInfo.setSlotWin(customerTaxInformation.getTotalSlotWin());	
		customerTaxInfo.setTotalWin(customerTaxInformation.getTotalWin());
		customerTaxInfoVOList.add(customerTaxInfo);
		getCustomerTaxInfoMap().put(DmpCoreConstant.CUSTOMER_TAX_INFO_TOTAL, customerTaxInfo);
		}
		
		return customerTaxInfoVOList;
	}
	/**
	 * @return the customerTaxInfoMaap
	 */
	@JsonIgnore
	public Map<String, CustomerTaxInfo> getCustomerTaxInfoMap() {
		if(null == customerTaxInfoMap){
			Map<String, CustomerTaxInfo> taxMap = new HashMap<String, CustomerTaxInfo>();
			this.customerTaxInfoMap = taxMap;
		}
		return customerTaxInfoMap;
	}
	/**
	 * @param customerTaxInfoMaap the customerTaxInfoMaap to set
	 */
	public void setCustomerTaxInfoMap(
			Map<String, CustomerTaxInfo> customerTaxInfoMap) {
		this.customerTaxInfoMap = customerTaxInfoMap;
	}
	
}
