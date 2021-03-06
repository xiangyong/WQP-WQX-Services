package gov.usgs.cida.wqp.mapping;

import java.util.List;
import java.util.Map;

public interface IXmlMapping {

	String getRoot();

	String getHeader();

	Map<String, List<String>> getStructure();

	Map<String, String> getHardBreak();
	
	Map<String, List<String>> getGrouping();

	String getEntryNodeName();

}
