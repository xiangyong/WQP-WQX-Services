package gov.usgs.cida.wqp.dao;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.usgs.cida.wqp.transform.intfc.ITransformer;

import org.apache.ibatis.session.ResultContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StreamingResultHandlerTest {

	private StreamingResultHandler h;
	@Mock
	private ITransformer t;
	@Mock
	private ResultContext<Object> context;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		h = new StreamingResultHandler(t);
	}

	@Test
	public void testHandleResult() {
		when(context.getResultObject()).thenReturn("Hello");
		h.handleResult(context);
		verify(t).write(anyObject());
	}

	@Test
	public void testHandleNullResult() {
		h.handleResult(null);
		verify(t, never()).write(anyObject());
	}

}
