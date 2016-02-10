package com.mgm.dmp.common.vo;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMin;
import javax.validation.groups.Default;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.validation.AvailabilityListValidation;
import com.mgm.dmp.common.validation.ValidAvailabilityListRequest;
import com.mgm.dmp.common.validation.ValidationAware;
import com.mgmresorts.aurora.common.RoomPricingType;

@JsonInclude(Include.NON_NULL)
@ValidAvailabilityListRequest(
        message = "invalid.checkinoutdate", groups = { AvailabilityListValidation.class })
public class RoomAvailabilityRequest extends AbstractReservationRequest implements
        ValidationAware.ValidAvailabilityListRequest {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6255077472535411416L;

    @DecimalMin(
            value = "1", inclusive = true, message = "invalid.numadults", groups = { AvailabilityValidation.class,
                    AvailabilityListValidation.class, BookRoomTypeValidation.class })
    private int numAdults;

    private String promoCode;

    private String roomTypeId;

    private Boolean programRate = Boolean.FALSE;

    private Boolean firstProgramRequest = Boolean.FALSE;

    private Boolean wantCommentary = Boolean.FALSE;

    private Boolean offerMode = Boolean.TRUE;
    
    @JsonIgnore
    private Boolean defaultProgramApplied = Boolean.FALSE;
    
    @DecimalMin(
            value = "1", inclusive = true, message = "invalid.tripduration", groups = {
                    AvailabilityRateValidation.class, AvailabilityListValidation.class })
    private int maxTripDuration;

    // @NotNull(message = "invalid.maximum.number.of.reservations", groups = {
    // AvailabilityListValidation.class })
    private String maximumNumberOfReservations;

    @DecimalMin(
            value = "1", inclusive = true, message = "invalid.totalMonths", groups = { AvailabilityValidation.class })
    private int totalCalendarMonths;

    private RoomPricingType priceType;

    @JsonFormat(
            pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
    private Date checkInDate;
    private int checkInDay;

    @JsonFormat(
            pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
    private Date checkOutDate;
    private int checkOutDay;

    @JsonFormat(
            pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
    private Date calendarStartDate;

    @JsonFormat(
            pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
    private Date calendarEndDate;

    private String agentId;

    private String selectedRoomTypeId;

	private String selectedRoomId;
	
    private boolean firstRequest;

    private String customerTier;
    
    private List<String> crossPropertyIds;	
    
    private int stayLength;
    
	/**
	 * @return the stayLength
	 */
	public int getStayLength() {
		return stayLength;
	}

	/**
	 * @param stayLength the stayLength to set
	 */
	public void setStayLength(int stayLength) {
		this.stayLength = stayLength;
	}

	/**
	 * @return the crossPropertyIds
	 */
	public List<String> getCrossPropertyIds() {
		return crossPropertyIds;
	}

	/**
	 * @param crossPropertyIds the crossPropertyIds to set
	 */
	public void setCrossPropertyIds(List<String> crossPropertyIds) {
		this.crossPropertyIds = crossPropertyIds;
	}

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public Date getCalendarStartDate() {
        return calendarStartDate;
    }

    public void setCalendarStartDate(Date calendarStartDate) {
        this.calendarStartDate = calendarStartDate;
    }

    public Date getCalendarEndDate() {
        return calendarEndDate;
    }

    public void setCalendarEndDate(Date calendarEndDate) {
        this.calendarEndDate = calendarEndDate;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
        setCheckInDay(DateUtil.getDayOfWeek(checkInDate, getPropertyId()));
    }

    /**
	 * @return the checkInDay
	 */
	public int getCheckInDay() {
		return checkInDay;
	}

	/**
	 * @param checkInDay the checkInDay to set
	 */
	public void setCheckInDay(int checkInDay) {
		this.checkInDay = checkInDay;
	}

	public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
        setCheckOutDay(DateUtil.getDayOfWeek(checkOutDate, getPropertyId()));
    }

	/**
	 * @return the checkOutDay
	 */
	public int getCheckOutDay() {
		return checkOutDay;
	}

	/**
	 * @param checkOutDay the checkOutDay to set
	 */
	public void setCheckOutDay(int checkOutDay) {
		this.checkOutDay = checkOutDay;
	}

	public int getNumAdults() {
        return numAdults;
    }

    public void setNumAdults(int numAdults) {
        this.numAdults = numAdults;
    }

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public Boolean getProgramRate() {
        return programRate;
    }

    public void setProgramRate(Boolean programRate) {
        this.programRate = programRate;
    }

    public Boolean isFirstProgramRequest() {
        return firstProgramRequest;
    }

    public void setFirstProgramRequest(Boolean firstProgramRequest) {
        this.firstProgramRequest = firstProgramRequest;
    }

    public Boolean getWantCommentary() {
        return wantCommentary;
    }

    public void setWantCommentary(Boolean wantCommentary) {
        this.wantCommentary = wantCommentary;
    }

    public Boolean getOfferMode() {
		return offerMode;
	}

	public void setOfferMode(Boolean offerMode) {
		this.offerMode = offerMode;
	}

	public int getMaxTripDuration() {
        return maxTripDuration;
    }

    public void setMaxTripDuration(int maxTripDuration) {
        this.maxTripDuration = maxTripDuration;
    }

    public String getMaximumNumberOfReservations() {
        return maximumNumberOfReservations;
    }

    public void setMaximumNumberOfReservations(String maximumNumberOfReservations) {
        this.maximumNumberOfReservations = maximumNumberOfReservations;
    }

    public int getTotalCalendarMonths() {
        return totalCalendarMonths;
    }

    public void setTotalCalendarMonths(int totalCalendarMonths) {
        this.totalCalendarMonths = totalCalendarMonths;
    }

    public RoomPricingType getPriceType() {
        return priceType;
    }

    public void setPriceType(RoomPricingType priceType) {
        this.priceType = priceType;
    }

    /**
     * @return the firstRequest
     */
    public boolean isFirstRequest() {
        return firstRequest;
    }

    /**
     * @param firstRequest
     *            the firstRequest to set
     */
    public void setFirstRequest(boolean firstRequest) {
        this.firstRequest = firstRequest;
    }

    /**
     * Validation as program id should not null if its present in request.
     * 
     * @return
     */
    @JsonIgnore
    @AssertTrue(
            message = "invalid.agentId", groups = { AgentCodeValidation.class })
    public boolean isAgentIdNotEmpty() {
        if (StringUtils.isBlank(this.getAgentId())) {
            return false;
        }
        return true;
    }

    @AssertTrue(
            message = "invalid.selectedRoomTypeId", groups = { BookRoomTypeValidation.class })
    public boolean isRoomTypeIdNotEmpty() {
        if (StringUtils.isBlank(this.getSelectedRoomTypeId())) {
            return false;
        }
        return true;
    }

    /**
     * Validation to check - check in date greater than or equals to today's
     * date. - check out date greater than or equals to today's date. - check
     * out date not exceed the trip max days.
     * 
     * @return
     */
    @JsonIgnore
    @Override
    public boolean validAvailabilityListRequest() {
        if (null != this.getCheckInDate()) {
            DateTime dateTimeToday = new DateTime(DateTimeZone.forTimeZone(DateUtil.getPropertyTimeZone(this
                    .getPropertyId())));
            Date today = dateTimeToday.millisOfDay().withMinimumValue().toDate();
            if (!DateUtils.isSameDay(this.getCheckInDate(), today) && this.getCheckInDate().before(today)) {
                return false;
            }
        }

        if (null != this.getCheckInDate() && null != this.getCheckOutDate()) {
            int diff = Days.daysBetween(new DateTime(this.getCheckInDate()), new DateTime(this.getCheckOutDate()))
                    .getDays();
            if (diff > maxTripDuration) {
                return false;
            }
            if (!DateUtils.isSameDay(this.getCheckInDate(), this.getCheckOutDate())
                    && this.getCheckOutDate().before(this.getCheckInDate())) {
                return false;
            }
        }
        return true;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getSelectedRoomTypeId() {
        return selectedRoomTypeId;
    }

    public void setSelectedRoomTypeId(String selectedRoomTypeId) {
        this.selectedRoomTypeId = selectedRoomTypeId;
    }
	
	public String getSelectedRoomId() {
		return selectedRoomId;
	}

	public void setSelectedRoomId(String selectedRoomId) {
		this.selectedRoomId = selectedRoomId;
	}

    /**
     * @return the customerTier
     */
    public String getCustomerTier() {
        return customerTier;
    }

    /**
     * @param customerTier
     *            the customerTier to set
     */
    public void setCustomerTier(String customerTier) {
        this.customerTier = customerTier;
    }

    public interface AvailabilityValidation extends Default {
    }

    public interface AvailabilityRateValidation extends AvailabilityValidation {
    }

    public interface AgentCodeValidation extends Default {
    }

    public interface BookRoomTypeValidation extends Default {
    }

	/**
	 * @return the defaultProgramApplied
	 */
	public Boolean getDefaultProgramApplied() {
		return defaultProgramApplied;
	}

	/**
	 * @param defaultProgramApplied the defaultProgramApplied to set
	 */
	public void setDefaultProgramApplied(Boolean defaultProgramApplied) {
		this.defaultProgramApplied = defaultProgramApplied;
	}
	
}
