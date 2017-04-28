package org.modelmapper.sql;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.modelmapper.sql.ResultSetValueReader;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class ResultSetValueReaderTest {
	
    static class Sample {
        private String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
    }

    public void shouldCreateMapForNull() throws SQLException {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().addValueReader(new ResultSetValueReader());

        ResultSetMetaData meta = Mockito.mock(ResultSetMetaData.class); 
        Mockito.when(meta.getColumnCount()).thenReturn(1);
        Mockito.when(meta.getColumnName(1)).thenReturn("text");
        Mockito.when(meta.getColumnClassName(1)).thenReturn(String.class.getName());

        ResultSet set1 = Mockito.mock(ResultSet.class);
        Mockito.when(set1.getMetaData()).thenReturn(meta);
        Mockito.when(set1.getObject("text")).thenReturn(null);
        
        Sample obj1 = modelMapper.map(set1, Sample.class);
        Assert.assertNull(obj1.getText());

        ResultSet set2 = Mockito.mock(ResultSet.class);
        Mockito.when(set2.getMetaData()).thenReturn(meta);
        Mockito.when(set2.getObject("text")).thenReturn("abc");

        Sample obj2 = modelMapper.map(set2, Sample.class);
        Assert.assertEquals(obj2.getText(), "abc");
    }
}
