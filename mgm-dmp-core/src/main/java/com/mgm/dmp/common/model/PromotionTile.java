/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_NULL)
public class PromotionTile implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6603841774871350308L;
	private String id;
	private String button;
	private String category;
	private String categoryCopy;
	private DateRange date;
	private String tileState;
	private Keyfact keyfact;
	private PromoPhoneNumber phoneNumber;
	private SocialChannel socialChannel;
	private List<Keyfact> keyfacts;
	private List<SocialChannel> socialChannels;
	private List<PromoPhoneNumber> phoneNumbers;
	private OperatingHour operatingHour;
	private List<OperatingHour> operatingHours;
	private Float utcOffset;
	private Description description;
	private String heading;
	private Image image;
	private Size size = new Size();
	private String ccmType;
	private String type;
	private String url;
	private int priority;
	private int defaultSize;
	private String startDate;
	private String redeemStartDate;
	private String endDate;
	private String propertyId;
	private String contentId;
	private String contentPath;
	private Boolean newWindow;
	private String target = "_parent";
	private Boolean bookableOnline;
	private String virtualTour;
	private String bookingUrl;
	private String amenityType;
	private Boolean hideActionMenu;
	private String shareTitle;
	private String shareDescription;
	private String shareImage;
	private Float latitude = (float) 0.0d;
	private Float longitude = (float) 0.0d;
	private String price;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the button
	 */
	public String getButton() {
		return button;
	}

	/**
	 * @param button the button to set
	 */
	public void setButton(String button) {
		this.button = button;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the categoryCopy
	 */
	public String getCategoryCopy() {
		return categoryCopy;
	}

	/**
	 * @param categoryCopy the categoryCopy to set
	 */
	public void setCategoryCopy(String categoryCopy) {
		this.categoryCopy = categoryCopy;
	}

	/**
	 * @return the date
	 */
	public DateRange getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(DateRange date) {
		this.date = date;
	}

	/**
	 * @return the tileState
	 */
	public String getTileState() {
		return tileState;
	}

	/**
	 * @param tileState the tileState to set
	 */
	public void setTileState(String tileState) {
		this.tileState = tileState;
	}
	
	/**
	 * @return the keyfact
	 */
	public Keyfact getKeyfact() {
		return keyfact;
	}

	/**
	 * @param keyfact the keyfact to set
	 */
	public void setKeyfact(Keyfact keyfact) {
		this.keyfact = keyfact;
	}
	
	/**
	 * @return the PhoneNumber
	 */
	public PromoPhoneNumber getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param PhoneNumber the PhoneNumber to set
	 */
	public void setPhoneNumber(PromoPhoneNumber phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the keyfacts
	 */
	public List<Keyfact> getKeyfacts() {
		return keyfacts;
	}

	/**
	 * @param keyfacts the keyfacts to set
	 */
	public void setKeyfacts(List<Keyfact> keyfacts) {
		this.keyfacts = keyfacts;
	}
	/**
	 * @return the phoneNumbers
	 */
	public List<PromoPhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	/**
	 * @param phoneNumbers the phoneNumbers to set
	 */
	public void setPhoneNumbers(List<PromoPhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	
	/**
	 * @return the socialChannel
	 */
	public SocialChannel getSocialChannel() {
		return socialChannel;
	}

	/**
	 * @param socialChannel the socialChannel to set
	 */
	public void setSocialChannel(SocialChannel socialChannel) {
		this.socialChannel = socialChannel;
	}

	/**
	 * @return the socialChannels
	 */
	public List<SocialChannel> getSocialChannels() {
		return socialChannels;
	}

	/**
	 * @param socialChannels the socialChannels to set
	 */
	public void setSocialChannels(List<SocialChannel> socialChannels) {
		this.socialChannels = socialChannels;
	}

	/**
	 * @return the operatingHour
	 */
	public OperatingHour getOperatingHour() {
		return operatingHour;
	}

	/**
	 * @param operatingHour the operatingHour to set
	 */
	public void setOperatingHour(OperatingHour operatingHour) {
		this.operatingHour = operatingHour;
	}

	/**
	 * @return the operatingHours
	 */
	public List<OperatingHour> getOperatingHours() {
		return operatingHours;
	}

	/**
	 * @param operatingHours the operatingHours to set
	 */
	public void setOperatingHours(List<OperatingHour> operatingHours) {
		this.operatingHours = operatingHours;
	}

	/**
	 * @return the utcOffset
	 */
	public Float getUtcOffset() {
		return utcOffset;
	}

	/**
	 * @param utcOffset the utcOffset to set
	 */
	public void setUtcOffset(Float utcOffset) {
		this.utcOffset = utcOffset;
	}

	/**
	 * @return the description
	 */
	public Description getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(Description description) {
		this.description = description;
	}

	/**
	 * @return the heading
	 */
	public String getHeading() {
		return heading;
	}

	/**
	 * @param heading the heading to set
	 */
	public void setHeading(String heading) {
		this.heading = heading;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * @return the size
	 */
	public Size getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Size size) {
		this.size = size;
	}

	/**
	 * @return the ccmType
	 */
	public String getCcmType() {
		return ccmType;
	}

	/**
	 * @param ccmType the ccmType to set
	 */
	public void setCcmType(String ccmType) {
		this.ccmType = ccmType;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the defaultSize
	 */
	public int getDefaultSize() {
		return defaultSize;
	}

	/**
	 * @param defaultSize the defaultSize to set
	 */
	public void setDefaultSize(int defaultSize) {
		this.defaultSize = defaultSize;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the redeemStartDate
	 */
	public String getRedeemStartDate() {
		return redeemStartDate;
	}

	/**
	 * @param redeemStartDate the redeemStartDate to set
	 */
	public void setRedeemStartDate(String redeemStartDate) {
		this.redeemStartDate = redeemStartDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the propertyId
	 */
	public String getPropertyId() {
		return propertyId;
	}

	/**
	 * @param propertyId the propertyId to set
	 */
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	/**
	 * @return the contentId
	 */
	public String getContentId() {
		return contentId;
	}

	/**
	 * @param contentId the contentId to set
	 */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	/**
	 * @return the contentPath
	 */
	public String getContentPath() {
		return contentPath;
	}

	/**
	 * @param contentPath the contentPath to set
	 */
	public void setContentPath(String contentPath) {
		setId(contentPath);
	}

	/**
	 * @return the newWindow
	 */
	public Boolean getNewWindow() {
		return newWindow;
	}

	/**
	 * @param newWindow the newWindow to set
	 */
	public void setNewWindow(Boolean newWindow) {
		this.newWindow = newWindow;
		if(this.newWindow) {
			setTarget("_blank");
		}
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the bookableOnline
	 */
	public Boolean getBookableOnline() {
		return bookableOnline;
	}

	/**
	 * @param bookableOnline the bookableOnline to set
	 */
	public void setBookableOnline(Boolean bookableOnline) {
		this.bookableOnline = bookableOnline;
	}

	@Override
	public String toString() {
		return defaultSize + "";
	}

	/**
	 * @return the virtualTour
	 */
	public String getVirtualTour() {
		return virtualTour;
	}

	/**
	 * @param virtualTour the virtualTour to set
	 */
	public void setVirtualTour(String virtualTour) {
		this.virtualTour = virtualTour;
	}

	/**
	 * @return the bookingUrl
	 */
	public String getBookingUrl() {
		return bookingUrl;
	}

	/**
	 * @param bookingUrl the bookingUrl to set
	 */
	public void setBookingUrl(String bookingUrl) {
		this.bookingUrl = bookingUrl;
	}

	/**
	 * @return the amenityType
	 */
	public String getAmenityType() {
		return amenityType;
	}

	/**
	 * @param amenityType the amenityType to set
	 */
	public void setAmenityType(String amenityType) {
		this.amenityType = amenityType;
	}

	/**
	 * @return the hideActionMenu
	 */
	public Boolean getHideActionMenu() {
		return hideActionMenu;
	}

	/**
	 * @param hideActionMenu the hideActionMenu to set
	 */
	public void setHideActionMenu(Boolean hideActionMenu) {
		this.hideActionMenu = hideActionMenu;
	}

	/**
	 * @return the shareTitle
	 */
	public String getShareTitle() {
		return shareTitle;
	}

	/**
	 * @param shareTitle the shareTitle to set
	 */
	public void setShareTitle(String shareTitle) {
		this.shareTitle = shareTitle;
	}

	/**
	 * @return the shareDescription
	 */
	public String getShareDescription() {
		return shareDescription;
	}

	/**
	 * @param shareDescription the shareDescription to set
	 */
	public void setShareDescription(String shareDescription) {
		this.shareDescription = shareDescription;
	}

	/**
	 * @return the shareImage
	 */
	public String getShareImage() {
		return shareImage;
	}

	/**
	 * @param shareImage the shareImage to set
	 */
	public void setShareImage(String shareImage) {
		this.shareImage = shareImage;
	}

	/**
	 * @return the latitude
	 */
	public Float getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Float getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}
	
}
