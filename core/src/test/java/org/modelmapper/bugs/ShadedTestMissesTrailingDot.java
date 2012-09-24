package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/?
 * 
 * Reported by Kay Schubert.
 */
@Test
public class ShadedTestMissesTrailingDot extends AbstractTest {

	public static class Source {
		private String item;
		private String itemX;
		
		public String getItem() {
			return item;
		}
		
		public void setItem(String item) {
			this.item = item;
		}
		
		public String getItemX() {
			return itemX;
		}
		
		public void setItemX(String itemX) {
			this.itemX = itemX;
		}
	}

	public static class Destination {
		private String item;
		private String itemX;
		
		public String getItem() {
			return item;
		}
		
		public void setItem(String item) {
			this.item = item;
		}
		
		public String getItemX() {
			return itemX;
		}
		
		public void setItemX(String itemX) {
			this.itemX = itemX;
		}
	}

	public void test() {
		String expectedNameXValue = "someValue";
		Source source = new Source();
		source.setItemX(expectedNameXValue);

		Destination result = modelMapper.map(source, Destination.class);

		assertEquals(result.getItemX(), expectedNameXValue);
	}
}
