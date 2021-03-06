package gov.usgs.cida.wqp.webservice;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import gov.usgs.cida.wqp.dao.StreamingResultHandler;
import gov.usgs.cida.wqp.dao.intfc.ICountDao;
import gov.usgs.cida.wqp.dao.intfc.IDao;
import gov.usgs.cida.wqp.dao.intfc.IStreamingDao;
import gov.usgs.cida.wqp.mapping.IXmlMapping;
import gov.usgs.cida.wqp.parameter.IParameterHandler;
import gov.usgs.cida.wqp.parameter.ParameterMap;
import gov.usgs.cida.wqp.parameter.Parameters;
import gov.usgs.cida.wqp.service.ILogService;
import gov.usgs.cida.wqp.transform.MapToDelimitedTransformer;
import gov.usgs.cida.wqp.transform.MapToJsonTransformer;
import gov.usgs.cida.wqp.transform.MapToKmlTransformer;
import gov.usgs.cida.wqp.transform.MapToXlsxTransformer;
import gov.usgs.cida.wqp.transform.MapToXmlTransformer;
import gov.usgs.cida.wqp.transform.Transformer;
import gov.usgs.cida.wqp.util.HttpConstants;
import gov.usgs.cida.wqp.util.MimeType;
import gov.usgs.cida.wqp.util.MybatisConstants;
import gov.usgs.cida.wqp.validation.ValidationConstants;

public abstract class BaseController implements HttpConstants, ValidationConstants {

	private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);
	protected final IStreamingDao streamingDao;
	protected final ICountDao countDao;
	protected final IParameterHandler parameterHandler;
	protected final ILogService logService;
	protected final Integer maxResultRows;
	protected final String siteUrlBase;

	private static ThreadLocal<ParameterMap> pm = new ThreadLocal<>();
	private static ThreadLocal<MimeType> mimeType = new ThreadLocal<>();
	private static ThreadLocal<Boolean> zipped = new ThreadLocal<>();
	private static ThreadLocal<BigDecimal> logId = new ThreadLocal<>();
	private static ThreadLocal<Map<String, String>> counts = new ThreadLocal<>();
	
	public BaseController(IStreamingDao inStreamingDao, ICountDao inCountDao,
			IParameterHandler inParameterHandler, ILogService inLogService,
			Integer inMaxResultRows, String inSiteUrlBase) {
		
		LOG.trace(getClass().getName());
		
		streamingDao     = inStreamingDao;
		parameterHandler = inParameterHandler;
		countDao         = inCountDao;
		logService       = inLogService;
		maxResultRows = inMaxResultRows;
		siteUrlBase = inSiteUrlBase;
	}
	
	public static ParameterMap getPm() {
		return pm.get();
	}

	public static void setPm(ParameterMap inPm) {
		pm.set(inPm);
	}

	public static MimeType getMimeType() {
		return mimeType.get();
	}

	public static void setMimeType(MimeType inMimeType) {
		mimeType.set(inMimeType);
	}

	public static boolean getZipped() {
		return zipped.get();
	}

	public static void setZipped(Boolean inZipped) {
		zipped.set(inZipped);
	}

	public static BigDecimal getLogId() {
		return logId.get();
	}

	public static void setLogId(BigDecimal inLogId) {
		logId.set(inLogId);
	}

	public static Map<String, String> getCounts() {
		return counts.get();
	}

	public static void setCounts(Map<String, String> inCounts) {
		counts.set(inCounts);
	}

	public static void remove() {
		pm.remove();
		mimeType.remove();
		zipped.remove();
		logId.remove();
		counts.remove();
	}
	
	protected boolean isZipped(String zipParm, String mimeType) {
		return "yes".equalsIgnoreCase(zipParm) || MimeType.kmz.toString().equalsIgnoreCase(mimeType);
	}

	protected OutputStream getOutputStream(HttpServletResponse response, boolean zipped, String fileName) {
		try {
			OutputStream buffered = new BufferedOutputStream(response.getOutputStream());
			if (zipped) {
				ZipOutputStream zipOut = new ZipOutputStream(buffered);
				zipOut.putNextEntry(new ZipEntry(fileName));
				return (OutputStream) zipOut;
			} else {
				return buffered;
			}
		} catch (IOException e) {
			throw new RuntimeException("Error getting OutputStream", e);
		}
	}

	protected MimeType adjustMimeType(MimeType mimeType, boolean zipped) {
		if (zipped && MimeType.kml.equals(mimeType)) {
			return MimeType.kmz;
		} else {
			return mimeType;
		}
	}

	protected void closeZip(OutputStream out) {
		try {
			((ZipOutputStream) out).closeEntry();
			((ZipOutputStream) out).finish();
		} catch (IOException e) {
			throw new RuntimeException("Error closing ZipOutputStream", e);
		}
	}

	protected String getContentHeader(boolean zipped, MimeType mimeType) {
		if (zipped) {
			switch (mimeType) {
			case kml:
			case kmz:
				return MimeType.kmz.getMimeType();
			default:
				return MIME_TYPE_ZIP;
			}
		} else {
			return mimeType.getMimeType();
		}
	}
	
	protected String getAttachementFileName(boolean zipped, MimeType mimeType, String endpoint) {
		StringBuilder ret = new StringBuilder(endpoint.toLowerCase()).append(".");
		if (zipped) {
			switch (mimeType) {
			case kml:
			case kmz:
				ret.append(MimeType.kmz);
				break;
			default:
				ret.append("zip");
				break;
			}
		} else {
			ret.append(mimeType);
		}
		return ret.toString();
	}
	
	protected void doHeadRequest(HttpServletRequest request, HttpServletResponse response, String mybatisNamespace, String endpoint) {
		LOG.info("Processing Head: {}", request.getQueryString());
		logId.set(logService.logRequest(request, response, null));
		
		try {
			doHeader(request, response, getLogId(), mybatisNamespace, endpoint, null);
		} finally {
			logService.logRequestComplete(getLogId(), String.valueOf(response.getStatus()));
			LOG.info("Processing Head complete: {}", request.getQueryString());
			remove();
		}
	}

	protected boolean doHeader(HttpServletRequest request, HttpServletResponse response, BigDecimal logId,
			String mybatisNamespace, String endpoint, Map<String, Object> postParms) {
		response.setCharacterEncoding(DEFAULT_ENCODING);
		processParameters(request, postParms);
		if ( ! getPm().isValid() ) {
			writeWarningHeaders(response, getPm().getValidationMessages());
			LOG.info("Processing Head invalid params end:{}", getPm().getValidationMessages());
			return false;
		}

		String mimeTypeParam = (String) getPm().getQueryParameters().get(Parameters.MIMETYPE.toString());
		String zipParam = (String) getPm().getQueryParameters().get(Parameters.ZIP.toString());
		zipped.set(isZipped(zipParam, mimeTypeParam));
		mimeType.set(adjustMimeType(MimeType.csv.fromString(mimeTypeParam), getZipped()));

		List<Map<String, Object>> counts = countDao.getCounts(mybatisNamespace, getPm().getQueryParameters());

		response.setCharacterEncoding(DEFAULT_ENCODING);
		response.addHeader(HEADER_CONTENT_TYPE, getContentHeader(getZipped(), getMimeType()));
		if (RequestMethod.POST.toString().equalsIgnoreCase(request.getMethod())
				&& request.getRequestURI().endsWith("count")) {
			//skip the content disposition header on POST counts
		} else {
			response.setHeader(HEADER_CONTENT_DISPOSITION,"attachment; filename=" + getAttachementFileName(getZipped(), getMimeType(), endpoint));
		}
		String totalHeader = addCountHeaders(response, counts);
		
		logService.logHeadComplete(response, logId);
		
		return checkMaxRows(response, response.getHeader(totalHeader));
	}
	
	protected boolean checkMaxRows(HttpServletResponse response, String totalRows) {
		switch (getMimeType()) {
		case kml:
		case kmz:
		case xml:
			if (Integer.valueOf(totalRows) <= maxResultRows) {
				getPm().getQueryParameters().put(Parameters.SORTED.toString(), "yes");
				return true;
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.addHeader(HEADER_WARNING, "This query will return in excess of " + maxResultRows + " results, please refine your query.");
				return false;
			}
		default:
			if (Integer.valueOf(totalRows) > maxResultRows) {
				getPm().getQueryParameters().put(Parameters.SORTED.toString(), "no");
				response.addHeader(HEADER_WARNING, "This query will return in excess of " + maxResultRows + " results, the data will not be sorted.");
			} else {
				if (!getPm().getQueryParameters().containsKey(Parameters.SORTED.toString())) {
					getPm().getQueryParameters().put(Parameters.SORTED.toString(), "yes");
				}
			}
			return true;
		}
	}
	
	protected void processParameters(HttpServletRequest request, Map<String, Object> postParms) {
		setPm(new ParameterMap());
		if (request.getParameterMap().isEmpty() && (null == postParms || postParms.isEmpty())) {
			LOG.debug("No parameters");
			getPm().setValid(false);
		} else {
			LOG.trace("got parameters");
			Map<String, String[]> requestParams = new HashMap<>(request.getParameterMap());
			LOG.debug("requestParams: {}", requestParams);
			setPm(parameterHandler.validateAndTransform(requestParams, postParms));
			LOG.debug("pm: {}", getPm());
			LOG.debug("queryParms: {}", getPm().getQueryParameters());
		}
	}
	
	protected abstract String addCountHeaders(HttpServletResponse response, List<Map<String, Object>> counts);
	
	protected void doGetRequest(HttpServletRequest request, HttpServletResponse response, String mybatisNamespace, String endpoint) {
		LOG.info("Processing Get: {}", request.getQueryString());
		logId.set(logService.logRequest(request, response, null));
		OutputStream responseStream = null;
		String realHttpStatus = String.valueOf(response.getStatus());
		
		try {
			if (doHeader(request, response, getLogId(), mybatisNamespace, endpoint, null)) {
				String namespace = getNamespace(mybatisNamespace, getMimeType());
				responseStream = getOutputStream(response, getZipped(), getZipEntryName(endpoint, getMimeType()));
				Transformer transformer = getTransformer(getMimeType(), responseStream, getLogId());
					
				ResultHandler<?> handler = new StreamingResultHandler(transformer);
				streamingDao.stream(namespace, getPm().getQueryParameters(), handler);
		
				transformer.end();
					
			}		
		} catch (Throwable e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			int hashValue = response.hashCode();
			realHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR.toString() + "-" + hashValue;
			//Note: we are giving the user a generic message.  
			//Server logs can be used to troubleshoot problems.
			String msgText = "Something bad happened. Contact us with Reference Number: " + hashValue;
			LOG.error("logId: {}", BaseController.getLogId());
			LOG.error("status: {}", HttpStatus.INTERNAL_SERVER_ERROR.value());
			LOG.error(msgText, e);
			response.addHeader(HEADER_FATAL_ERROR, msgText);
			if (null != responseStream) {
				try {
					responseStream.write(msgText.getBytes(DEFAULT_ENCODING));
				} catch (IOException e2) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error telling client about exception", e2);
				}
			}
		} finally {
			if (null != responseStream) {
				try {
					if (getZipped()) {
						closeZip(responseStream);
					}
				} catch (Throwable e) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error closing zip", e);
				}
				try {
					responseStream.flush();
				} catch (IOException e) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error flushing response stream", e);
				}
			}
			LOG.info("Processing Get complete: {}", request.getQueryString());
			logService.logRequestComplete(getLogId(), realHttpStatus);
			remove();
		}
	}

	protected void doPostRequest(HttpServletRequest request, HttpServletResponse response, String mybatisNamespace, String endpoint, Map<String, Object> postParms) {
		LOG.info("Processing Post: {}", postParms);
		logId.set(logService.logRequest(request, response, postParms));
		OutputStream responseStream = null;
		String realHttpStatus = String.valueOf(response.getStatus());
		
		try {
			if (doHeader(request, response, getLogId(), mybatisNamespace, endpoint, postParms)) {
				String namespace = getNamespace(mybatisNamespace, getMimeType());
				responseStream = getOutputStream(response, getZipped(), getZipEntryName(endpoint, getMimeType()));
				Transformer transformer = getTransformer(getMimeType(), responseStream, getLogId());
					
				ResultHandler<?> handler = new StreamingResultHandler(transformer);
				streamingDao.stream(namespace, getPm().getQueryParameters(), handler);
		
				transformer.end();
					
			}		
		} catch (Throwable e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			int hashValue = response.hashCode();
			realHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR.toString() + "-" + hashValue;
			//Note: we are giving the user a generic message.  
			//Server logs can be used to troubleshoot problems.
			String msgText = "Something bad happened. Contact us with Reference Number: " + hashValue;
			LOG.error("logId: {}", BaseController.getLogId());
			LOG.error("status: {}", HttpStatus.INTERNAL_SERVER_ERROR.value());
			LOG.error(msgText, e);
			response.addHeader(HEADER_FATAL_ERROR, msgText);
			if (null != responseStream) {
				try {
					responseStream.write(msgText.getBytes(DEFAULT_ENCODING));
				} catch (IOException e2) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error telling client about exception", e2);
				}
			}
		} finally {
			if (null != responseStream) {
				try {
					if (getZipped()) {
						closeZip(responseStream);
					}
				} catch (Throwable e) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error closing zip", e);
				}
				try {
					responseStream.flush();
				} catch (IOException e) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error flushing response stream", e);
				}
			}
			LOG.info("Processing POST complete: {}", request.getQueryString());
			logService.logRequestComplete(getLogId(), realHttpStatus);
			remove();
		}
	}

	protected Map<String, String> doPostCountRequest(HttpServletRequest request, HttpServletResponse response, String mybatisNamespace, String endpoint, Map<String, Object> postParms) {
		LOG.info("Processing Post Count");
		logId.set(logService.logRequest(request, response, postParms));
		Map<String, String> counts = null;
		
		try {
			doHeader(request, response, getLogId(), mybatisNamespace, endpoint, postParms);
			counts = getCounts();
		} finally {
			logService.logRequestComplete(getLogId(), String.valueOf(response.getStatus()));
			LOG.info("Processing Post Count complete");
			remove();
		}
		
		return counts;
	}

	protected String getZipEntryName(String endpoint, MimeType mimeType) {
		if (MimeType.kml.equals(mimeType) || MimeType.kmz.equals(mimeType)) {
			return endpoint.toLowerCase() + "." + MimeType.kml;
		} else {
			return endpoint.toLowerCase() + "." + mimeType;
		}
	}

	protected String getNamespace(String mybatisNamespace, MimeType mimeType) {
		switch (mimeType) {
		case kml:
		case kmz:
			return IDao.STATION_KML_NAMESPACE;
		case geojson:
			return IDao.SIMPLE_STATION_NAMESPACE;
		default:
			return mybatisNamespace;
		}
	}
	
	protected Transformer getTransformer(MimeType mimeType, OutputStream responseStream, BigDecimal logId) {
		Transformer transformer;
		switch (mimeType) {
		case json:
		case geojson:
			transformer = new MapToJsonTransformer(responseStream, null, logService, logId, siteUrlBase);
			break;
		case xlsx:
			transformer = new MapToXlsxTransformer(responseStream, getMapping(), logService, logId);
			break;
		case xml:
			transformer = new MapToXmlTransformer(responseStream, getXmlMapping(), logService, logId);
			break;
		case kml:
		case kmz:
			transformer = new MapToKmlTransformer(responseStream, getKmlMapping(), logService, logId);
			break;
		case tsv:
			transformer = new MapToDelimitedTransformer(responseStream, getMapping(), logService, logId, MapToDelimitedTransformer.TAB);
			break;
		case csv:
		default:
			transformer = new MapToDelimitedTransformer(responseStream, getMapping(), logService, logId, MapToDelimitedTransformer.COMMA);
			break;
		}
		return transformer;
	}

	protected abstract Map<String, String> getMapping();
	
	protected abstract IXmlMapping getXmlMapping();
	
	protected IXmlMapping getKmlMapping() {
		return null;
	}
	
	public void writeWarningHeaders(HttpServletResponse response, Map<String, List<String>> validationMessages) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		for (List<String> msgs : validationMessages.values()) {
			for (String msg : msgs) {
				response.addHeader(HEADER_WARNING, warningHeader(null, msg, null));
			}
		}
	}

	protected String warningHeader(Integer code, String text, String date) {
		StringBuilder rtn = new StringBuilder();
		rtn.append(null == code ? HEADER_WARNING_DEFAULT_CODE : code);
		rtn.append(" WQP \"");
		rtn.append(null == text || 0 == text.length() ? "Unknown error" : text);
		rtn.append("\" ");
		rtn.append(null == date || 0 == date.length() ? new Date().toString() : date);
		return rtn.toString();
	}

	protected String determineHeaderName(Map<String, Object> count, String suffix) {
		String mySuffix = suffix==null ?"" :suffix;
		if (null == count) {
			return HEADER_TOTAL + HEADER_DELIMITER + mySuffix;
		} else {
			Object provider = count.get(MybatisConstants.DATA_SOURCE);
			
			if (provider==null) {
				provider = HEADER_TOTAL + HEADER_DELIMITER + mySuffix;
			} else {
				provider = provider + HEADER_DELIMITER + mySuffix;
			}
			LOG.trace("determine count header : {}", provider.toString());
	
			return provider.toString();
		}
	}
	
	protected String determineHeaderValue(Map<String, Object> count, String key) {
		if (null == count || count.isEmpty() || null == key || !count.containsKey(key)) {
			return "0";
		} else {
			Object results = count.get(key);
			results = results==null ?"0" :results;
			LOG.trace("determine count value : {}", results.toString());
			return results.toString();
		}
	}
	
	public void addCountHeaders(HttpServletResponse response, List<Map<String, Object>> rawCounts,
			String totalHeader, String valueHeader, String valueField) {
		LOG.trace("{} counts : {}", valueHeader, rawCounts);
		Map<String, String> counts = null==getCounts() ? new HashMap<>() : getCounts();
		
		response.addHeader(totalHeader, "0");
		counts.put(totalHeader, "0");
		
		for (Map<String, Object> count : rawCounts) {
			LOG.trace("result count : {}", count);
			String name = determineHeaderName(count, valueHeader);
			String value = determineHeaderValue(count, valueField);
			response.setHeader(name, value);
			counts.put(name, value);
		}
		
		setCounts(counts);
	}

	public void addSiteHeaders(HttpServletResponse response, List<Map<String, Object>> counts) {
		addCountHeaders(response, counts, HEADER_TOTAL_SITE_COUNT, HEADER_SITE_COUNT, MybatisConstants.STATION_COUNT);
	}

}
