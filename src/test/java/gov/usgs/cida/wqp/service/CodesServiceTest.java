package gov.usgs.cida.wqp.service;

import static gov.usgs.cida.wqp.exception.WqpExceptionId.METHOD_PARAM_EMPTY;
import static gov.usgs.cida.wqp.exception.WqpExceptionId.METHOD_PARAM_NULL;
import static gov.usgs.cida.wqp.exception.WqpExceptionId.URL_PARSING_EXCEPTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.wqp.BaseSpringTest;
import gov.usgs.cida.wqp.exception.WqpException;
import gov.usgs.cida.wqp.parameter.Parameters;
import gov.usgs.cida.wqp.util.WqpEnvProperties;

import java.net.URL;

import org.junit.Test;

public class CodesServiceTest extends BaseSpringTest implements WqpEnvProperties {

	@Test
	public void testMakeUrl_nullParam() throws Exception {
		try {
			new CodesService(null, null, 0).makeCodesUrl(null, "provider");
			fail("should have thrown exception on null parameter");
		} catch (WqpException e) {
			assertEquals("Expect param exception", METHOD_PARAM_NULL, e.getExceptionid());
		}
	}
	@Test
	public void testMakeUrl_nullCode() throws Exception {
		try {
			new CodesService(null, null, 0).makeCodesUrl(Parameters.PROVIDERS, null);
			fail("should have thrown exception on null code");
		} catch (WqpException e) {
			assertEquals("Expect param exception", METHOD_PARAM_NULL, e.getExceptionid());
		}
	}
	@Test
	public void testMakeUrl_emptyCode() throws Exception {
		try {
			new CodesService(null, null, 0).makeCodesUrl(Parameters.PROVIDERS, "");
			fail("should have thrown exception on empty string");
		} catch (WqpException e) {
			assertEquals("Expect param exception", METHOD_PARAM_EMPTY, e.getExceptionid());
		}
	}
	
	@Test
	public void testMakeUrl_default() throws Exception {
		String baseUrl = "https://wqp.codes.usgs.gov/codes/";
		URL actualUrl = new CodesService(baseUrl, "json", 0).makeCodesUrl(Parameters.PROVIDERS, "provider");
		
		String expectedUrl = baseUrl +"/"+ Parameters.PROVIDERS +"?value=provider&mimeType=json";
		
		assertEquals(expectedUrl, actualUrl.toString());
	}
	
	
	@Test
	public void testMakeUrl_customMimeType() throws Exception {
		String baseUrl = "https://wqp.codes.usgs.gov/codes/";
		String mimeType = "xml";
		URL actualUrl = new CodesService(baseUrl, mimeType, 0).makeCodesUrl(Parameters.PROVIDERS, "provider");
			
		String expectedUrl = baseUrl +"/"+ Parameters.PROVIDERS +"?value=provider&mimeType="+mimeType;
			
		assertEquals(expectedUrl, actualUrl.toString());
	}

	
	@Test
	public void testMakeUrl_badURL() throws Exception {
//		TODO note that URL does not see this as a bad URL https://wqp.codes.usgs.gov***/& bad?? \t URL&";
//		TODO do we want more comprehensive validation?
		String baseUrl = "ht//tps://wqp.codes.usgs.gov/bad/URL/";
		
		try {
			URL actualUrl = new CodesService(baseUrl, null, 0).makeCodesUrl(Parameters.PROVIDERS, "provider");
			System.out.println(actualUrl.toString());
			fail("should have thrown exception on bad URL");
		} catch (WqpException e) {
			assertEquals("Expect URL exception", URL_PARSING_EXCEPTION, e.getExceptionid());
		}
	}

	
	@Test
	public void testValidation_nullParam() throws Exception {
		try {
			new CodesService(null, null, 0).validate(null, "provider");
			fail("should have thrown exception on null parameter");
		} catch (WqpException e) {
			assertTrue("Expect param exception", e.getExceptionid() == METHOD_PARAM_NULL);
		}
	}
	@Test
	public void testValidation_nullCode() throws Exception {
		try {
			new CodesService(null, null, 0).validate(Parameters.PROVIDERS, null);
			fail("should have thrown exception on null code");
		} catch (WqpException e) {
			assertEquals("Expect param exception", METHOD_PARAM_NULL, e.getExceptionid());
		}
	}
	@Test
	public void testValidation_emptyCode() throws Exception {
		boolean actual = new CodesService(null, null, 0).validate(Parameters.PROVIDERS, "");
		assertFalse("Empty string code is always invalid", actual);
	}
	

	@Test
	public void test() throws Exception {
		CodesService service = new CodesService("http://cida-eros-wqpqa.er.usgs.gov:8080/qw_portal_services/codes", "json", 0);
		boolean x = service.validate(Parameters.PROVIDERS, "STEWARDS");
		assertTrue(x);
	}

//	@Test
//	public void testValidation_validCode() throws Exception {
//		// mock the fetcher
//		boolean actual = new CodesService(){
//			@Override
//			public String fetch(Parameters codeType, String code) throws WqpException {
//				return "provider";
//			}
//		}.validate(Parameters.PROVIDERS, "provider");
//		
//		assertTrue("Returned code is always valid", actual);
//	}

//	@Test
//	public void testValidation_invalidCode() throws Exception {
//		// mock the fetcher
//		boolean actual = new CodesService(){
//			@Override
//			public String fetch(Parameters codeType, String code) throws WqpException {
//				return "someOtherCode";
//			}
//		}.validate(Parameters.PROVIDERS, "invalidCode");
//		
//		assertFalse("Returned code is invalid if it does not contian the code", actual);
//	}
	
	
//	@Test
//	public void testFetch_throwsExceptionWhenNoUrl() {
//		WqpEnv.set(CODES_URL, "");
//		try {
//			new CodesService().fetch(Parameters.PROVIDERS, "b");
//			fail("should have thrown exception when no codes url");
//		} catch (WqpException e) {
//			assertEquals("Expect config exception", UNDEFINED_WQP_CONFIG_PARAM, e.getExceptionid());
//		}
//	}

//	@Test
//	public void testFetch_testDataPipe() throws Exception {
//		final String baseStr = "a byte stream";
//		
//		// mock the url stream
//		String actualStr = new CodesService(){
//			protected UrlStreamContainer makeProvider(Parameters codeType, String code) throws WqpException {
//				UrlStreamContainer urlContainer = Mockito.mock(UrlStreamContainer.class);
//				ByteArrayInputStream bais = new ByteArrayInputStream(baseStr.getBytes());
//				try {
//					TestUtils.refectSetValue(urlContainer, "logger", logger);
//					Mockito.when(urlContainer.getName()).thenReturn("TestStream");
//					Mockito.when(urlContainer.open()).thenReturn(bais);
//				} catch (StreamInitException e) {
//					fail("Failed to mock url container open()");
//				}
//				
//				return urlContainer;
//			};
//		}.fetch(Parameters.PROVIDERS, "b");
//		
//		assertEquals("Expect stream string returned", baseStr, actualStr);
//	}

//	@Test
//	public void testFetch_testServerNotAvailable() throws Exception {
//		try {
//			// mock the url stream
//			new CodesService(){
//				protected UrlStreamContainer makeProvider(Parameters codeType, String code) throws WqpException {
//					UrlStreamContainer urlContainer = Mockito.mock(UrlStreamContainer.class);
//					InputStream out = new InputStream() {
//						@Override
//						public int read() throws IOException {
//							throw new RuntimeException();
//						}
//					};
//					try {
//						TestUtils.refectSetValue(urlContainer, "logger", logger);
//						Mockito.when(urlContainer.getName()).thenReturn("TestStream");
//						Mockito.when(urlContainer.open()).thenReturn(out);
//					} catch (StreamInitException e) {
//						fail("Failed to mock url container open()");
//					}
//					
//					return urlContainer;
//				};
//			}.fetch(Parameters.PROVIDERS, "b");
//			
//			fail("should throw connection exception");
//		} catch (WqpException e) {
//			assertEquals("Expecting server request error", SERVER_REQUEST_IO_ERROR, e.getExceptionid());
//		}
//	}

	
//	@Test
//	public void testFetch_testServer404() throws Exception {
//		// mock the url stream
//		String actual = new CodesService(){
//			@SuppressWarnings("unchecked")
//			protected UrlStreamContainer makeProvider(Parameters codeType, String code) throws WqpException {
//				UrlStreamContainer urlContainer = Mockito.mock(UrlStreamContainer.class);
//				try {
//					TestUtils.refectSetValue(urlContainer, "logger", logger);
//					Mockito.when(urlContainer.getName()).thenReturn("TestStream");
//					Mockito.when(urlContainer.open()).thenThrow(FileNotFoundException.class); // this is unchecked, why?
//				} catch (StreamInitException e) {
//					fail("Failed to mock url container open()");
//				}
//				
//				return urlContainer;
//			};
//		}.fetch(Parameters.PROVIDERS, "b");
//		
//		String expect = "";
//		assertEquals(expect,actual);
//	}
	
//	@Test
//	public void testMakeProvider() throws Exception {
//		String baseUrl = "https://wqp.codes.usgs.gov/codes/";
//		WqpEnv.set(CODES_URL, baseUrl);
//		UrlStreamContainer urlContainer = new CodesService().makeProvider(Parameters.PROVIDERS, "provider");
//		assertNotNull("expect container instance",urlContainer);
//		
//		Object value = TestUtils.reflectValue(urlContainer, "url");
//		assertNotNull("expect url instance", value);
//		
//		URL actualUrl = (URL)value;
//		String expectedUrl = baseUrl +"/"+ Parameters.PROVIDERS +"?value=provider&mimeType=json";
//		assertEquals(expectedUrl, actualUrl.toString());
//	}
	
	
//	// integration test
//	public static void main(String[] args) throws Exception {
//		WqpEnv.set(CODES_URL, "http://cida-eros-wqpdev.er.usgs.gov:8082/qw_portal_services/codes/");
//		
//		String value = new CodesService().fetch(Parameters.PROVIDERS, "NWIS");
//		System.out.println(value);
//		
//		boolean valid = new CodesService().validate(Parameters.PROVIDERS, "NWIS");
//		System.out.println(valid);
//
//		boolean invalid = new CodesService().validate(Parameters.PROVIDERS, "SWIN");
//		System.out.println(invalid);
//	}
	
	
}
