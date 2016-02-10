package com.mgm.dmp.common.util;

import java.io.Serializable;

import java.util.Comparator;

import com.mgm.dmp.common.model.PriceCodes;

public class ShowPriceComparator implements Comparator<PriceCodes>,Serializable{

	private static final long serialVersionUID = 6562984593579836260L;

	public int compare(PriceCodes priceCode1, PriceCodes priceCode2) {
		if(priceCode1.getFullPrice().getValue() > (priceCode2.getFullPrice().getValue())){		
			return 1;
		}else{
			return -1;
		}
	}
}
