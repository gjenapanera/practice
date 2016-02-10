/**
 * 
 */
package com.mgm.dmp.common.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author ssahu6
 *
 */
public enum Availability {
	AVAILABLE("AVAILABLE"), FREENIGHT("FREENIGHT"), SOLDOUT("SOLD-OUT"), NOARRIVAL("NO-ARRIVAL"), OFFER("OFFER"), NOTAVAILABLE("UNAVAILABLE");
	
	private final String status;

    /**
     * @param text
     */
    private Availability(final String status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    @JsonValue
    public String toString() {
        return status;
    }
}
