package org.modelmapper.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.modelmapper.spi.ValueReader;

/**
 * SQL ResultSet Value Reader Implementation 
 * 
 * @author Serge Matsevilo
 *
 */
public class ResultSetValueReader implements ValueReader<ResultSet> {

	public Object get(ResultSet source, String memberName) {
		try {
			return source.getObject(memberName);
		} catch (SQLException e) {
		      throw new IllegalArgumentException("Cannot get value for " + memberName, e);
		}
	}

	public Class<?> getType(ResultSet source, String memberName) {
		try {
			ResultSetMetaData meta = source.getMetaData();
			
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				if (meta.getColumnName(i).equals(memberName)) {
					try {
						return Class.forName(meta.getColumnClassName(i));
					} catch (ClassNotFoundException e) {
					      throw new IllegalArgumentException("Cannot get class for " + memberName, e);
					}
				}
			}
		
		} catch (SQLException e) {
		      throw new IllegalArgumentException("Cannot get metadata for the specified result set.", e);
		}

		return Object.class;
	}

	public Collection<String> memberNames(ResultSet source) {
		try {
			ResultSetMetaData meta = source.getMetaData();
			ArrayList<String> names = new ArrayList<String>(meta.getColumnCount());			
			
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				names.add(meta.getColumnName(i));
			}
			
			return names;
			
		} catch (SQLException e) {
		      throw new IllegalArgumentException("Cannot get columns from the spepecified result set.", e);
		}
	}
	
	  @Override
	  public String toString() {
	    return "SQL";
	  }
}
