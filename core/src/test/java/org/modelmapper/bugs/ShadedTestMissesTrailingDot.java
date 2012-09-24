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
	public static class SubItem {
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class Source {
		private String item;
		private SubItem itemX;
		
		public String getItem() {
			return item;
		}
		
		public void setItem(String item) {
			this.item = item;
		}
		
		public SubItem getItemX() {
			return itemX;
		}
		
		public void setItemX(SubItem itemX) {
			this.itemX = itemX;
		}
	}

	public static class Destination {
		private String item;
		private SubItem itemX;
		
		public String getItem() {
			return item;
		}
		
		public void setItem(String item) {
			this.item = item;
		}
		
		public SubItem getItemX() {
			return itemX;
		}
		
		public void setItemX(SubItem itemX) {
			this.itemX = itemX;
		}
	}

	public void test() {
		String expectedNameXValue = "someValue";
		Source source = new Source();
		SubItem subItemX = new SubItem();
		subItemX.setValue(expectedNameXValue);
		source.setItemX(subItemX);

		Destination result = modelMapper.map(source, Destination.class);

		assertEquals(expectedNameXValue, result.getItemX().getValue());
	}
}
