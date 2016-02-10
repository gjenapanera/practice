package com.mgm.dmp.common.model.phoenix;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/** Added by MGM Support in R1.7 for MRIC-1735 **/

@JsonInclude(Include.NON_EMPTY)
public class TicketingProgramHoldValues extends AbstractPhoenixEntity implements Serializable {
	
	private static final long serialVersionUID = -4080216235517823581L;

	@Override
	public Boolean getBookableOnline() {
		
		return null;
	}

	@Override
	public String toString() {
		return "TicketingProgram [Id=" + getId() + ", Name="
				+ getName() + ", ActiveFlag=" + getActiveFlag() + "]";
	}
	
	
	
}
