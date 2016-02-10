package com.mgm.dmp.common.model;

import java.io.Serializable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.model.phoenix.Show;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ShowOccurrencesDetails implements Serializable {

	private static final long serialVersionUID = 5776676290688698209L;
	@JsonProperty("shows")
	private List<Show> filteredShows;
	
	public List<Show> getFilteredShows() {
		return filteredShows;
	}


	public void setFilteredShows(List<Show> filteredShows) {
		this.filteredShows = filteredShows;
	}

}
