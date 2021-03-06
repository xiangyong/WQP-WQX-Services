package gov.usgs.cida.wqp.mapping;

import static gov.usgs.cida.wqp.mapping.StationColumn.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class StationWqx extends BaseWqx implements IXmlMapping {
	
	public static final Map<String, String> HARD_BREAK = new LinkedHashMap<>();

	public static final Map<String, List<String>> COLUMN_POSITION = new LinkedHashMap<>();
	
	public static final Map<String, List<String>> GROUPING = new LinkedHashMap<>();

	static {
		HARD_BREAK.put(KEY_ORGANIZATION, ROOT_NODE);
		HARD_BREAK.put(KEY_SITE_ID, WQX_ORGANIZATION);
	}

	static {
		COLUMN_POSITION.put(KEY_ORGANIZATION,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_ORGANIZATION_DESCRIPTION,
						WQX_ORGANIZATION_IDENTIFIER)));
		COLUMN_POSITION.put(KEY_ORGANIZATION_NAME,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_ORGANIZATION_DESCRIPTION,
						WQX_ORGANIZATION_FORMAL_NAME)));
		
		COLUMN_POSITION.put(KEY_SITE_ID,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_IDENTITY,
						WQX_MONITORING_LOCATION_IDENTIFIER)));
		COLUMN_POSITION.put(KEY_STATION_NAME,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_IDENTITY,
						WQX_MONITORING_LOCATION_NAME)));
		COLUMN_POSITION.put(KEY_MONITORING_LOCATION_TYPE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_IDENTITY,
						WQX_MONITORING_LOCATION_TYPE)));
		
		
		COLUMN_POSITION.put(KEY_MONITORING_LOCATION_DESCRIPTION,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_IDENTITY,
						WQX_MONITORING_LOCATION_DESCRIPTION)));
		COLUMN_POSITION.put(KEY_HUC_8,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_IDENTITY,
						WQX_HUC_8)));
//		COLUMN_POSITION.put(KEY_HUC_12,
//				new LinkedList<String>(Arrays.asList(
//						WQX_ORGANIZATION,
//						WQX_MONITORING_LOCATION,
//						WQX_MONITORING_LOCATION_IDENTITY,
//						WQX_HUC_12)));
		COLUMN_POSITION.put(KEY_DRAIN_AREA_VALUE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_IDENTITY,
						WQX_DRAIN_AREA,
						WQX_MEASURE_VALUE)));
		COLUMN_POSITION.put(KEY_DRAIN_AREA_UNIT,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_IDENTITY,
						WQX_DRAIN_AREA,
						WQX_MEASURE_UNIT)));
		COLUMN_POSITION.put(KEY_CONTRIB_DRAIN_AREA_VALUE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_IDENTITY,
						WQX_CONTRIB_DRAIN_AREA,
						WQX_MEASURE_VALUE)));
		COLUMN_POSITION.put(KEY_CONTRIB_DRAIN_AREA_UNIT,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_IDENTITY,
						WQX_CONTRIB_DRAIN_AREA,
						WQX_MEASURE_UNIT)));
		
		
		
		COLUMN_POSITION.put(KEY_LATITUDE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_LATITUDE_MEASURE)));
		COLUMN_POSITION.put(KEY_LONGITUDE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_LONGITUDE_MEASURE)));

		
		
		COLUMN_POSITION.put(KEY_SOURCE_MAP_SCALE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_SOURCE_MAP_SCALE)));
		COLUMN_POSITION.put(KEY_HORIZONTAL_ACCY_VALUE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_HORIZONTAL_ACCY,
						WQX_MEASURE_VALUE)));
		COLUMN_POSITION.put(KEY_HORIZONTAL_ACCY_UNIT,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_HORIZONTAL_ACCY,
						WQX_MEASURE_UNIT)));
		COLUMN_POSITION.put(KEY_HORIZONTAL_COLLECTION_METHOD,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_HORIZONTAL_COLLECTION_METHOD)));
		COLUMN_POSITION.put(KEY_HORIZONTAL_DATUM,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_HORIZONTAL_DATUM)));
		COLUMN_POSITION.put(KEY_VERTICAL_MEASURE_VALUE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_VERTICAL_MEASURE,
						WQX_MEASURE_VALUE)));
		COLUMN_POSITION.put(KEY_VERTICAL_MEASURE_UNIT,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_VERTICAL_MEASURE,
						WQX_MEASURE_UNIT)));
		COLUMN_POSITION.put(KEY_VERTICAL_ACCY_VALUE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_VERTICAL_ACCY,
						WQX_MEASURE_VALUE)));
		COLUMN_POSITION.put(KEY_VERTICAL_ACCY_UNIT,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_VERTICAL_ACCY,
						WQX_MEASURE_UNIT)));
		COLUMN_POSITION.put(KEY_VERTICAL_COLLECTION_METHOD,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_VERTICAL_COLLECTION_METHOD)));
		COLUMN_POSITION.put(KEY_VERTICAL_DATUM,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_VERTICAL_DATUM)));
		COLUMN_POSITION.put(KEY_COUNTRY_CODE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_COUNTRY_CODE)));
		COLUMN_POSITION.put(KEY_STATE_CODE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_STATE_CODE)));
		COLUMN_POSITION.put(KEY_COUNTY_CODE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_MONITORING_LOCATION_GEOSPATIAL,
						WQX_COUNTY_CODE)));
		
		
		COLUMN_POSITION.put(KEY_NAT_AQFR_NAME,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_WELL_INFO,
						WQX_NAT_AQFR_NAME)));
		COLUMN_POSITION.put(KEY_AQFR_NAME,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_WELL_INFO,
						WQX_AQFR_NAME)));
		COLUMN_POSITION.put(KEY_AQFR_TYPE_NAME,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_WELL_INFO,
						WQX_AQFR_TYPE_NAME)));
		COLUMN_POSITION.put(KEY_CONSTRUCTION_DATE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_WELL_INFO,
						WQX_CONSTRUCTION_DATE)));
		COLUMN_POSITION.put(KEY_WELL_DEPTH_VALUE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_WELL_INFO,
						WQX_WELL_DEPTH,
						WQX_MEASURE_VALUE)));
		COLUMN_POSITION.put(KEY_WELL_DEPTH_UNIT,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_WELL_INFO,
						WQX_WELL_DEPTH,
						WQX_MEASURE_UNIT)));
		COLUMN_POSITION.put(KEY_HOLE_DEPTH_VALUE,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_WELL_INFO,
						WQX_HOLE_DEPTH,
						WQX_MEASURE_VALUE)));
		COLUMN_POSITION.put(KEY_HOLE_DEPTH_UNIT,
				new LinkedList<String>(Arrays.asList(
						WQX_ORGANIZATION,
						WQX_MONITORING_LOCATION,
						WQX_WELL_INFO,
						WQX_HOLE_DEPTH,
						WQX_MEASURE_UNIT)));
	}
	

	static {
		GROUPING.put(KEY_ORGANIZATION,
				new LinkedList<String>(Arrays.asList(KEY_ORGANIZATION, KEY_ORGANIZATION_NAME)));
		GROUPING.put(KEY_SITE_ID,
				new LinkedList<String>(Arrays.asList(KEY_SITE_ID,
						KEY_STATION_NAME,
						KEY_MONITORING_LOCATION_TYPE,
						KEY_MONITORING_LOCATION_DESCRIPTION,
						KEY_HUC_8,
//						KEY_HUC_12,
						KEY_DRAIN_AREA_VALUE,
						KEY_DRAIN_AREA_UNIT,
						KEY_CONTRIB_DRAIN_AREA_VALUE,
						KEY_CONTRIB_DRAIN_AREA_UNIT,
						KEY_LATITUDE,
						KEY_LONGITUDE,
						KEY_SOURCE_MAP_SCALE,
						KEY_HORIZONTAL_ACCY_VALUE,
						KEY_HORIZONTAL_ACCY_UNIT,
						KEY_HORIZONTAL_COLLECTION_METHOD,
						KEY_HORIZONTAL_DATUM,
						KEY_VERTICAL_MEASURE_VALUE,
						KEY_VERTICAL_MEASURE_UNIT,
						KEY_VERTICAL_ACCY_VALUE,
						KEY_VERTICAL_ACCY_UNIT,
						KEY_VERTICAL_COLLECTION_METHOD,
						KEY_VERTICAL_DATUM,
						KEY_COUNTRY_CODE,
						KEY_STATE_CODE,
						KEY_COUNTY_CODE,
						KEY_NAT_AQFR_NAME,
						KEY_AQFR_NAME,
						KEY_AQFR_TYPE_NAME,
						KEY_CONSTRUCTION_DATE,
						KEY_WELL_DEPTH_VALUE,
						KEY_WELL_DEPTH_UNIT,
						KEY_HOLE_DEPTH_VALUE,
						KEY_HOLE_DEPTH_UNIT)));
	}
	
	public String getEntryNodeName() {
		return WQX_PROVIDER;
	}

	public Map<String, List<String>> getStructure() {
		return COLUMN_POSITION;
	}

	public Map<String, String> getHardBreak() {
		return HARD_BREAK;
	}

	public Map<String, List<String>> getGrouping() {
		return GROUPING;
	}
}