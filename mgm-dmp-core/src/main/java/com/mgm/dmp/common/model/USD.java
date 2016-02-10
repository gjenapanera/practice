package com.mgm.dmp.common.model;

import java.text.DecimalFormat;



public class USD extends Price{
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7675336817331382686L;
	private String delim;
	private String curr;
	
	public String getDelim() {
		return delim;
	}

	public final void setDelim(String delim) {
		this.delim = delim;
	}

	public String getCurr() {
		return curr;
	}

	public final void setCurr(String curr) {
		this.curr = curr;
	}
	
	public USD(){
		
	}

	public USD(Double price){
		 if (price != null) {
	        	this.setCurr("$");
	        	this.setDelim(".");
	        	DecimalFormat df = new DecimalFormat("0.00");      
	            String strPrice = df.format(price);
	            if (strPrice.indexOf('.') != -1) {
	                this.setNote(strPrice.substring(0, strPrice.indexOf('.')));
	                String cent = strPrice.substring(strPrice.indexOf('.')+1, strPrice.length());
	                if(cent.length()==1){
	                	cent = cent + "0";
	                }
	                this.setCent(cent);
	                this.setValue(price);
	            } else {
	                this.setNote(strPrice);
	                this.setCent(".00");
	                this.setValue(price);
	            }
	        } else {
	            this.setCent(null);
	            this.setCurr(null);
	            this.setDelim(null);
	            this.setNote(null);
	            this.setValue(null);
	        }
	}
	
	public String toString(){
	    return curr + this.getNote() + delim + this.getCent();
	}

}
