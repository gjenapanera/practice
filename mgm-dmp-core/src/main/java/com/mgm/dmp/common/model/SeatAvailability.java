package com.mgm.dmp.common.model;

import java.util.List;

public class SeatAvailability extends AbstractDmpBaseVO {

	private static final long serialVersionUID = 37212958924420029L;
	
	private List<SeatAvailabilitySections> seatAvailabilitySections;
	private List<SeatAvailabilitySections> manifestSeatSection;
	/**
	 * @return the seatAvailabilitySections
	 */
	public List<SeatAvailabilitySections> getSeatAvailabilitySections() {
		return seatAvailabilitySections;
	}
	/**
	 * @param seatAvailabilitySections the seatAvailabilitySections to set
	 */
	public void setSeatAvailabilitySections(
			List<SeatAvailabilitySections> seatAvailabilitySections) {
		this.seatAvailabilitySections = seatAvailabilitySections;
	}
	/**
	 * @return the manifestSeatSection
	 */
	public List<SeatAvailabilitySections> getManifestSeatSection() {
		return manifestSeatSection;
	}
	/**
	 * @param manifestSeatSection the manifestSeatSection to set
	 */
	public void setManifestSeatSection(
			List<SeatAvailabilitySections> manifestSeatSection) {
		this.manifestSeatSection = manifestSeatSection;
	}

}
