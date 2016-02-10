package com.mgm.dmp.common.vo;

import java.io.Serializable;

public class DefaultProgramsResponse implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4533184259227269122L;

    private String propertyId;
    private String defRateProgramId;
    private String defProgramTransient;
    private String defProgramSapphire;
    private String defProgramPearl;
    private String defProgramGold;
    private String defProgramPlatinum;
    private String defProgramNOIR;
    private int noOfTotalMonths;

    /**
     * @return the propertyId
     */
    public String getPropertyId() {
        return propertyId;
    }

    /**
     * @param propertyId
     *            the propertyId to set
     */
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

	/**
     * @return the defRateProgramId
     */
    public String getDefRateProgramId() {
        return defRateProgramId;
    }

    /**
     * @param defRateProgramId
     *            the defRateProgramId to set
     */
    public void setDefRateProgramId(String defRateProgramId) {
        this.defRateProgramId = defRateProgramId;
    }

    /**
     * @return the defProgramTransient
     */
    public String getDefProgramTransient() {
        return defProgramTransient;
    }

    /**
     * @param defProgramTransient
     *            the defProgramTransient to set
     */
    public void setDefProgramTransient(String defProgramTransient) {
        this.defProgramTransient = defProgramTransient;
    }

    /**
     * @return the defProgramSapphire
     */
    public String getDefProgramSapphire() {
        return defProgramSapphire;
    }

    /**
     * @param defProgramSapphire
     *            the defProgramSapphire to set
     */
    public void setDefProgramSapphire(String defProgramSapphire) {
        this.defProgramSapphire = defProgramSapphire;
    }

    /**
     * @return the defProgramPearl
     */
    public String getDefProgramPearl() {
        return defProgramPearl;
    }

    /**
     * @param defProgramPearl
     *            the defProgramPearl to set
     */
    public void setDefProgramPearl(String defProgramPearl) {
        this.defProgramPearl = defProgramPearl;
    }

    /**
     * @return the defProgramGold
     */
    public String getDefProgramGold() {
        return defProgramGold;
    }

    /**
     * @param defProgramGold
     *            the defProgramGold to set
     */
    public void setDefProgramGold(String defProgramGold) {
        this.defProgramGold = defProgramGold;
    }

    /**
     * @return the defProgramPlatinum
     */
    public String getDefProgramPlatinum() {
        return defProgramPlatinum;
    }

    /**
     * @param defProgramPlatinum
     *            the defProgramPlatinum to set
     */
    public void setDefProgramPlatinum(String defProgramPlatinum) {
        this.defProgramPlatinum = defProgramPlatinum;
    }

    /**
     * @return the defProgramNOIR
     */
    public String getDefProgramNOIR() {
        return defProgramNOIR;
    }

    /**
     * @param defProgramNOIR
     *            the defProgramNOIR to set
     */
    public void setDefProgramNOIR(String defProgramNOIR) {
        this.defProgramNOIR = defProgramNOIR;
    }

    /**
     * @return the noOfTotalMonths
     */
    public int getNoOfTotalMonths() {
        return noOfTotalMonths;
    }

    /**
     * @param noOfTotalMonths
     *            the noOfTotalMonths to set
     */
    public void setNoOfTotalMonths(int noOfTotalMonths) {
        this.noOfTotalMonths = noOfTotalMonths;
    }
 @Override
	public String toString() {
		return "DefaultProgramsResponse [propertyId=" + propertyId
				+ ", defRateProgramId=" + defRateProgramId
				+ ", defProgramTransient=" + defProgramTransient
				+ ", defProgramSapphire=" + defProgramSapphire
				+ ", defProgramPearl=" + defProgramPearl + ", defProgramGold="
				+ defProgramGold + ", defProgramPlatinum=" + defProgramPlatinum
				+ ", defProgramNOIR=" + defProgramNOIR + ", noOfTotalMonths="
				+ noOfTotalMonths + "]";
	}

}
