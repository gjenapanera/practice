package com.mgm.dmp.common.model.phoenix;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Program extends AbstractPhoenixEntity{
	
	private String patronPromoId;
	
	private Boolean bookableOnline;

	public Boolean getBookableOnline() {
		return bookableOnline;
	}

	public void setBookableOnline(Boolean bookableOnline) {
		this.bookableOnline = bookableOnline;
	}

	public String getPatronPromoId() {
		return patronPromoId;
	}

	public void setPatronPromoId(String patronPromoId) {
		this.patronPromoId = patronPromoId;
	}		
}
