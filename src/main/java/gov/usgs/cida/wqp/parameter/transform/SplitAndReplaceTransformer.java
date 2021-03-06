package gov.usgs.cida.wqp.parameter.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tkunicki
 */
public class SplitAndReplaceTransformer extends SplitTransformer {
	private static final Logger LOG = LoggerFactory.getLogger(SplitAndReplaceTransformer.class);
	
	private final String regex;
	private final String replace;
	
	public SplitAndReplaceTransformer(String delimiter, String regex, String replace) {
		super(delimiter);
		LOG.trace(getClass().getName());
		this.regex = regex;
		this.replace = replace;
	}
	
	@Override
	public String[] transform(String value) {
		String[] strings = split(value);
		for (int i = 0; i < strings.length; ++i) {
			strings[i] = strings[i].replaceAll(regex, replace);
		}
		return strings;
	}
}