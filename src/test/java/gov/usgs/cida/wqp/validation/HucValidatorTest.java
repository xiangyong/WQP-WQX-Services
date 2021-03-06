package gov.usgs.cida.wqp.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;

import gov.usgs.cida.wqp.BaseSpringTest;
import gov.usgs.cida.wqp.validation.AbstractValidator;
import gov.usgs.cida.wqp.validation.ValidationConstants;
import gov.usgs.cida.wqp.validation.ValidationResult;

public class HucValidatorTest extends BaseSpringTest implements ValidationConstants {

	@Resource(name = "hucValidator")
	AbstractValidator<String[]> validator;
	
	@Test
	public void testHuc_valids() {
		ValidationResult<String[]> vr = validator.validate("01;01*;0102;0102*;010203;010203*;01020304");
		assertTrue(vr.isValid());
		String[] t = vr.getTransformedValue();
		assertEquals(7, t.length);
		assertEquals("01", t[0]);
		assertEquals("01", t[1]);
		assertEquals("0102", t[2]);
		assertEquals("0102", t[3]);
		assertEquals("010203", t[4]);
		assertEquals("010203", t[5]);
		assertEquals("01020304", t[6]);
	}
	
	@Test
	public void testHuc_invalids() {
		ValidationResult<String[]> vr = validator.validate("0*1;0*;01020304*");
		assertFalse(vr.isValid());
		assertEquals(1, vr.getValidationMessages().size());
		assertEquals("The value of huc=0*1;0*;01020304* must match the format (?:[0-9]{8})|(?:(?:[0-9]{2}){1,3}\\*?)",
				vr.getValidationMessages().get(0));
		
		vr = validator.validate("0*1");
		assertFalse(vr.isValid());
		assertEquals(1, vr.getValidationMessages().size());
		assertEquals("The value of huc=0*1 must match the format (?:[0-9]{8})|(?:(?:[0-9]{2}){1,3}\\*?)",
				vr.getValidationMessages().get(0));
		
		vr = validator.validate("0*");
		assertFalse(vr.isValid());
		assertEquals(1, vr.getValidationMessages().size());
		assertEquals("The value of huc=0* must match the format (?:[0-9]{8})|(?:(?:[0-9]{2}){1,3}\\*?)",
				vr.getValidationMessages().get(0));
		
		vr = validator.validate("01020304*");
		assertFalse(vr.isValid());
		assertEquals(1, vr.getValidationMessages().size());
		assertEquals("The value of huc=01020304* must match the format (?:[0-9]{8})|(?:(?:[0-9]{2}){1,3}\\*?)",
				vr.getValidationMessages().get(0));
	}

}
