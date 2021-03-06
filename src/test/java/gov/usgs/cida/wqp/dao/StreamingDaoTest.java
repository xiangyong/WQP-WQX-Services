package gov.usgs.cida.wqp.dao;


import static org.junit.Assert.fail;
import gov.usgs.cida.wqp.BaseSpringTest;
import gov.usgs.cida.wqp.DBIntegrationTest;
import gov.usgs.cida.wqp.dao.intfc.IDao;
import gov.usgs.cida.wqp.dao.intfc.IStreamingDao;
import gov.usgs.cida.wqp.parameter.Parameters;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(DBIntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testData/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/simpleStation.xml")
})
public class StreamingDaoTest extends BaseSpringTest {

	@Autowired 
	IStreamingDao streamingDao;
	
	private class TestResultHandler implements ResultHandler<Object> {
		//TODO put the results somewhere to check them and allow them to be cleared between queries
//		public ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		@Override
		public void handleResult(ResultContext<?> context) {
//			results.add((Map<String, Object>) context.getResultObject());
		}
	}

	TestResultHandler handler;

	@Before
	public void init() {
		handler = new TestResultHandler();
	}

	@After
	public void cleanup() {
		handler = null;
	}
	
	@Test
	public void stationTests() {
		singleParameterTests(IDao.STATION_NAMESPACE);
		multipleParameterTests(IDao.STATION_NAMESPACE);
	}
	
	@Test
	public void stationKmlTests() {
		singleParameterTests(IDao.STATION_KML_NAMESPACE);
		multipleParameterTests(IDao.STATION_KML_NAMESPACE);
	}
	
	@Test
	public void simpleStationTests() {
		singleParameterTests(IDao.SIMPLE_STATION_NAMESPACE);
		multipleParameterTests(IDao.SIMPLE_STATION_NAMESPACE);
	}
	
	@Test
	public void resultTests() {
		singleParameterTests(IDao.RESULT_NAMESPACE);
		multipleParameterTests(IDao.RESULT_NAMESPACE);
	}
	
	@Test
	public void bioResultTests() {
		singleParameterTests(IDao.BIOLOGICAL_RESULT_NAMESPACE);
		multipleParameterTests(IDao.BIOLOGICAL_RESULT_NAMESPACE);
	}
	
	private void singleParameterTests(String nameSpace) {
		//TODO - Real test data and verification. 
		//TODO - These tests just validate that the queries have no syntax errors, not that they are logically correct.
		Map<String, Object> parms = new HashMap<>();

		//MyBatis is happy with no parms or ResultHandler - it will read the entire database, load up the list,
		// and not complain or expose it to you (unless you run out of memory). We have a check to make sure the 
		//resultHandler is not null. (The tests were failing on Jenkins with "java.lang.OutOfMemoryError: Java heap space")
		try {
			streamingDao.stream(null, null, null);
		} catch (RuntimeException e) {
			if (!"A ResultHandler is required for the StreamingDao.stream".equalsIgnoreCase(e.getMessage())) {
				fail("Expected a RuntimeException, but got " + e.getLocalizedMessage());
			}
		}
		try {
		streamingDao.stream(nameSpace, null, null);
		} catch (RuntimeException e) {
			if (!"A ResultHandler is required for the StreamingDao.stream".equalsIgnoreCase(e.getMessage())) {
				fail("Expected a RuntimeException, but got " + e.getLocalizedMessage());
			}
		}
		try {
		streamingDao.stream(nameSpace, parms, null);
		} catch (RuntimeException e) {
			if (!"A ResultHandler is required for the StreamingDao.stream".equalsIgnoreCase(e.getMessage())) {
				fail("Expected a RuntimeException, but got " + e.getLocalizedMessage());
			}
		}
		
		streamingDao.stream(nameSpace, parms, handler);

		parms.put(Parameters.BBOX.toString(), new String[]{"-89", "43", "-88", "44"});
		streamingDao.stream(nameSpace, parms, handler);

		//"command.avoid"
//		parms.clear();
//		parms.put(Parameters.AVOID.toString(), new String[]{"STORET"});
//		streamingDao.stream(nameSpace, parms, handler);
		
		parms.clear();
		parms.put(Parameters.COUNTRY.toString(), new String[]{"US"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.COUNTY.toString(), new String[]{"US:55:027"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.HUC.toString(), new String[]{"07"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.HUC.toString(), new String[]{"0708"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.HUC.toString(), new String[]{"070801"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.HUC.toString(), new String[]{"07090002"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.MIN_RESULTS.toString(), "3");
		streamingDao.stream(nameSpace, parms, handler);

		try {
			parms.clear();
			parms.put(Parameters.NLDIURL.toString(), getSourceFile("manySites.txt").split(","));
			streamingDao.stream(nameSpace, parms, handler);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		parms.clear();
		parms.put(Parameters.ORGANIZATION.toString(), new String[]{"USGS-WI"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.PROVIDERS.toString(), new String[]{"STEWARDS"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.SITEID.toString(), new String[]{"11NPSWRD-BICA_MFG_B"});
		streamingDao.stream(nameSpace, parms, handler);

		try {
			parms.clear();
			parms.put(Parameters.SITEID.toString(), getSourceFile("manySites.txt").split(","));
			streamingDao.stream(nameSpace, parms, handler);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		parms.clear();
		parms.put(Parameters.SITE_TYPE.toString(), new String[]{"Stream"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.STATE.toString(), new String[]{"US:55"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.WITHIN.toString(), new String[]{"10"});
		parms.put(Parameters.LATITUDE.toString(), new String[]{"43.3836014"});
		parms.put(Parameters.LONGITUDE.toString(), new String[]{"-88.9773314"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.ANALYTICAL_METHOD.toString(), new String[]{"https://www.nemi.gov/methods/method_summary/8896/"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.ASSEMBLAGE.toString(), new String[]{"Fish/Nekton"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.CHARACTERISTIC_NAME.toString(), new String[]{"Nitrate"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.CHARACTERISTIC_TYPE.toString(), new String[]{"Nutrient"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.PCODE.toString(), new String[]{"00004"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.PROJECT.toString(), new String[]{"NAWQA"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.PROJECT.toString(), new String[]{"EPABEACH"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.SAMPLE_MEDIA.toString(), new String[]{"Water"});
		streamingDao.stream(nameSpace, parms, handler);

		//TODO - Activity is not currently supported
		//parms.put(Parameters.ACTIVITY_ID.toString(), new String[]{"abc"});
		//streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.START_DATE_HI.toString(), new String[]{"10-11-2012"});
		streamingDao.stream(nameSpace, parms, handler);

		parms.clear();
		parms.put(Parameters.START_DATE_LO.toString(), new String[]{"10-11-2012"});
		streamingDao.stream(nameSpace, parms, handler);
		
		parms.clear();
		parms.put(Parameters.SUBJECT_TAXONOMIC_NAME.toString(), new String[]{"Acipenser"});
		streamingDao.stream(nameSpace, parms, handler);
		
	}
	
	private void multipleParameterTests(String nameSpace) {
		//TODO - Real test data and verification. 
		//TODO - These tests just validate that the queries have no syntax errors, not that they are logically correct.
		Map<String, Object> parms = new HashMap<>();

		parms.put(Parameters.ANALYTICAL_METHOD.toString(), new String[]{"https://www.nemi.gov/methods/method_summary/4665/",
			"https://www.nemi.gov/methods/method_summary/8896/"});
		parms.put(Parameters.BBOX.toString(), new String[]{"-89", "43", "-88", "44"});
		parms.put(Parameters.ASSEMBLAGE.toString(), new String[]{"Fish/Nekton", "Benthic Macroinvertebrate"});
		parms.put(Parameters.CHARACTERISTIC_NAME.toString(), new String[]{"Beryllium", "Nitrate"});
		parms.put(Parameters.CHARACTERISTIC_TYPE.toString(), new String[]{"Inorganics, Minor, Metals", "Nutrient"});
		parms.put(Parameters.COUNTRY.toString(), new String[]{"MX", "US"});
		parms.put(Parameters.COUNTY.toString(), new String[]{"US:19:015", "US:30:003", "US:55:017", "US:55:021", "US:55:027"});
		parms.put(Parameters.HUC.toString(), new String[]{"07","0708","070801","07090002", "07080105"});
		parms.put(Parameters.LATITUDE.toString(), new String[]{"43.3836014"});
		parms.put(Parameters.LONGITUDE.toString(), new String[]{"-88.9773314"});
		parms.put(Parameters.MIN_RESULTS.toString(), "3");
		parms.put(Parameters.ORGANIZATION.toString(), new String[]{"ARS", "11NPSWRD", "USGS-WI", "WIDNR_WQX"});
		parms.put(Parameters.PROJECT.toString(), new String[]{"NAWQA", "CEAP", "EPABEACH"});
		parms.put(Parameters.PROVIDERS.toString(), new String[]{"NWIS", "STEWARDS", "STORET"});
		parms.put(Parameters.SITEID.toString(), new String[]{"11NPSWRD-BICA_MFG_B", "WIDNR_WQX-10030952", "USGS-05425700",
			"USGS-431925089002701", "ARS-IAWC-IAWC225", "ARS-IAWC-IAWC410"});
		parms.put(Parameters.SITE_TYPE.toString(), new String[]{"Lake, Reservoir, Impoundment", "Land", "Stream", "Well"});
		parms.put(Parameters.STATE.toString(), new String[]{"US:19", "US:30", "US:55"});
		parms.put(Parameters.PCODE.toString(), new String[]{"00032", "00004"});
		parms.put(Parameters.SAMPLE_MEDIA.toString(), new String[]{"Other", "Sediment", "Water"});
		parms.put(Parameters.START_DATE_HI.toString(), new String[]{"10-11-2012"});
		parms.put(Parameters.START_DATE_LO.toString(), new String[]{"10-11-2012"});
		parms.put(Parameters.SUBJECT_TAXONOMIC_NAME.toString(), new String[]{"Acipenser", "Lota lota"});
		parms.put(Parameters.WITHIN.toString(), new String[]{"1000"});
		streamingDao.stream(nameSpace, parms, handler);
	}
	
}
