package helpers.api;

public class RequestApiBuilder {
	
	private String name;	
	private Long countryId;
	private Long locationId;
	private Long budgetTypeId;
	private Long expirationTypeId;
	
	private boolean needsTransportation;
	private boolean needsHousing;
	private boolean needsActivities;
	
	private Long destinationCountryId;
	private String destinationCity;
	
	private long startDate;
	private long endDate;
	private int amountOfNights;
	private boolean isFlexible;
	private int amountOfAdults;
	private int amountOfChildren;

	private long transportationTypeId;
	private String transportationDetails;

	private long housingTypeId;
	private String roomTypology;
	private long mealTypeId;
	private int minimumStartAmount;
	
	private String childrenDescription;
	private String experiencesDescription;
	
	public RequestApiBuilder(String name, Long countryId, Long locationId,
			Long budgetTypeId, Long expirationTypeId,
			boolean needsTransportation, boolean needsHousing,
			boolean needsActivities, Long destinationCountryId,
			String destinationCity, long startDate, long endDate,
			int amountOfNights, boolean isFlexible, int amountOfAdults,
			int amountOfChildren, long transportationTypeId,
			String transportationDetails, long housingTypeId,
			String roomTypology, long mealTypeId, int minimumStartAmount,
			String childrenDescription, String experiencesDescription) {
		super();
		this.name = name;
		this.countryId = countryId;
		this.locationId = locationId;
		this.budgetTypeId = budgetTypeId;
		this.expirationTypeId = expirationTypeId;
		this.needsTransportation = needsTransportation;
		this.needsHousing = needsHousing;
		this.needsActivities = needsActivities;
		this.destinationCountryId = destinationCountryId;
		this.destinationCity = destinationCity;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amountOfNights = amountOfNights;
		this.isFlexible = isFlexible;
		this.amountOfAdults = amountOfAdults;
		this.amountOfChildren = amountOfChildren;
		this.transportationTypeId = transportationTypeId;
		this.transportationDetails = transportationDetails;
		this.housingTypeId = housingTypeId;
		this.roomTypology = roomTypology;
		this.mealTypeId = mealTypeId;
		this.minimumStartAmount = minimumStartAmount;
		this.childrenDescription = childrenDescription;
		this.experiencesDescription = experiencesDescription;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getCountryId() {
		return countryId;
	}
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}
	public Long getLocationId() {
		return locationId;
	}
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	public Long getBudgetTypeId() {
		return budgetTypeId;
	}
	public void setBudgetTypeId(Long budgetTypeId) {
		this.budgetTypeId = budgetTypeId;
	}
	public Long getExpirationTypeId() {
		return expirationTypeId;
	}
	public void setExpirationTypeId(Long expirationTypeId) {
		this.expirationTypeId = expirationTypeId;
	}
	public boolean isNeedsTransportation() {
		return needsTransportation;
	}
	public void setNeedsTransportation(boolean needsTransportation) {
		this.needsTransportation = needsTransportation;
	}
	public boolean isNeedsHousing() {
		return needsHousing;
	}
	public void setNeedsHousing(boolean needsHousing) {
		this.needsHousing = needsHousing;
	}
	public boolean isNeedsActivities() {
		return needsActivities;
	}
	public void setNeedsActivities(boolean needsActivities) {
		this.needsActivities = needsActivities;
	}
	public Long getDestinationCountryId() {
		return destinationCountryId;
	}
	public void setDestinationCountryId(Long destinationCountryId) {
		this.destinationCountryId = destinationCountryId;
	}
	public String getDestinationCity() {
		return destinationCity;
	}
	public void setDestinationCity(String destinationCity) {
		this.destinationCity = destinationCity;
	}
	public long getStartDate() {
		return startDate;
	}
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	public int getAmountOfNights() {
		return amountOfNights;
	}
	public void setAmountOfNights(int amountOfNights) {
		this.amountOfNights = amountOfNights;
	}
	public boolean isFlexible() {
		return isFlexible;
	}
	public void setFlexible(boolean isFlexible) {
		this.isFlexible = isFlexible;
	}
	public int getAmountOfAdults() {
		return amountOfAdults;
	}
	public void setAmountOfAdults(int amountOfAdults) {
		this.amountOfAdults = amountOfAdults;
	}
	public int getAmountOfChildren() {
		return amountOfChildren;
	}
	public void setAmountOfChildren(int amountOfChildren) {
		this.amountOfChildren = amountOfChildren;
	}
	public long getTransportationTypeId() {
		return transportationTypeId;
	}
	public void setTransportationTypeId(long transportationTypeId) {
		this.transportationTypeId = transportationTypeId;
	}
	public String getTransportationDetails() {
		return transportationDetails;
	}
	public void setTransportationDetails(String transportationDetails) {
		this.transportationDetails = transportationDetails;
	}
	public long getHousingTypeId() {
		return housingTypeId;
	}
	public void setHousingTypeId(long housingTypeId) {
		this.housingTypeId = housingTypeId;
	}
	public String getRoomTypology() {
		return roomTypology;
	}
	public void setRoomTypology(String roomTypology) {
		this.roomTypology = roomTypology;
	}
	public long getMealTypeId() {
		return mealTypeId;
	}
	public void setMealTypeId(long mealTypeId) {
		this.mealTypeId = mealTypeId;
	}
	public int getMinimumStartAmount() {
		return minimumStartAmount;
	}
	public void setMinimumStartAmount(int minimumStartAmount) {
		this.minimumStartAmount = minimumStartAmount;
	}
	public String getChildrenDescription() {
		return childrenDescription;
	}
	public void setChildrenDescription(String childrenDescription) {
		this.childrenDescription = childrenDescription;
	}
	public String getExperiencesDescription() {
		return experiencesDescription;
	}
	public void setExperiencesDescription(String experiencesDescription) {
		this.experiencesDescription = experiencesDescription;
	}
	

	
}
