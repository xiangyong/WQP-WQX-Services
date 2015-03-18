package gov.usgs.cida.wqp.parameter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public enum Parameters {
	ANALYTICAL_METHOD("analyticalmethod"),
	SITEID("siteid"),
	START_DATE_HI("startDateHi"),
	START_DATE_LO("startDateLo"),
	//activityId and pCode are NWIS only
	ACTIVITY_ID("activityId"),
	PCODE("pCode"),
	CHARACTERISTIC_NAME("characteristicName"),
	CHARACTERISTIC_TYPE("characteristicType"),
	SAMPLE_MEDIA("sampleMedia"),
	SAMPLE_TYPE("sampleType"),
	ORGANIZATION("organization"),
	SITE_TYPE("siteType"),
	HUC("huc"),
	COUNTRY("countrycode"),
	STATE("statecode"),
	COUNTY("countycode"),
	LONGITUDE("long"),
	LATITUDE("lat"),
	WITHIN("within"),
	BBOX("bBox"),
	MIMETYPE("mimeType"),
	ZIP("zip"),
	LAYOUT("layout"),
	SCHEMA("schema"),
	PROVIDERS("providers"),
	AVOID("command.avoid"),
	TIMEZONE_TYPE("timezoneType");
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final String parameterName;
	private static Map<String, Parameters> validParameterNames;
	private Parameters(String value) {
		log.trace(getClass().getName());
		parameterName = value;
		putParameterName(value, this);
	}
	private static void putParameterName(String value, Parameters param) {
		if (value == null || getValidParameterNames().containsKey(value)) {
			//This is a configuration error and can only happen if you duplicate values above...not unit testable
			throw new IllegalStateException("overloaded parameter value: " +  value +", please verify the enum definition for Parameters");
		} else {
			getValidParameterNames().put(value, param);
		}
	}
	private static Map<String, Parameters> getValidParameterNames() {
		if (validParameterNames == null) {
			validParameterNames = new HashMap<String, Parameters>();
		}
		return validParameterNames;
	}
	@Override
	public String toString() {
		return parameterName;
	}
	public static boolean isValid(String paramToCheck) {
		return validParameterNames.containsKey(paramToCheck);
	}
	public static Parameters fromName(String value) {
		return isValid(value) ? validParameterNames.get(value) : null;
	}
}