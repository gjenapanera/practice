package com.mgm.dmp.common.vo;

import java.io.Serializable;

public class RoomProgramsResponse implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5996031136583149531L;
    
    private String programId;
    
    public RoomProgramsResponse(String programId) {
        this.programId = programId;
    }
    /**
     * @return the programId
     */
    public String getProgramId() {
        return programId;
    }
    /**
     * @param programId the programId to set
     */
    public void setProgramId(String programId) {
        this.programId = programId;
    }
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoomProgramsResponse [programId=");
		builder.append(programId);
		builder.append("]");
		return builder.toString();
	}
    
}
