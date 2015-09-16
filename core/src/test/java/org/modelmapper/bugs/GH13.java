package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

@Test
public class GH13 extends AbstractTest {
	static public interface EmployeeInf {

		String getEmployeeFName();

		void setEmployeeFName(String employeeFName);

		String getEmployeeLName();

		void setEmployeeLName(String employeeLName);

	}
	static class Client {
		private String clientFName;

		private String clientLName;

		public String getClientFName() {
			return clientFName;
		}

		public void setClientFName(String clientFName) {
			this.clientFName = clientFName;
		}

		public String getClientLName() {
			return clientLName;
		}

		public void setClientLName(String clientLName) {
			this.clientLName = clientLName;
		}
	}

	static class Employee implements EmployeeInf {
		private String employeeFName;
		private String employeeLName;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.modelmapper.bugs.EmployeeInf#getEmployeeFName()
		 */
		@Override
		public String getEmployeeFName() {
			return employeeFName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.modelmapper.bugs.EmployeeInf#setEmployeeFName(java.lang.String)
		 */
		@Override
		public void setEmployeeFName(String employeeFName) {
			this.employeeFName = employeeFName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.modelmapper.bugs.EmployeeInf#getEmployeeLName()
		 */
		@Override
		public String getEmployeeLName() {
			return employeeLName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.modelmapper.bugs.EmployeeInf#setEmployeeLName(java.lang.String)
		 */
		@Override
		public void setEmployeeLName(String employeeLName) {
			this.employeeLName = employeeLName;
		}
	}

	public void shouldMapTargetInterface() {
		PropertyMap<Client, EmployeeInf> personMap = new PropertyMap<Client, EmployeeInf>() {
			protected void configure() {
				map().setEmployeeFName(source.getClientFName());
				map().setEmployeeLName(source.getClientLName());
			}
		};

		modelMapper.addMappings(personMap);

		Client customer = new Client();
		customer.setClientFName("testFName");
		customer.setClientLName("testLName");

		Employee employee = new Employee();
		modelMapper.map(customer, employee, EmployeeInf.class);

		assert employee.getEmployeeFName().equals(customer.getClientFName());
		assert employee.getEmployeeLName().equals(customer.getClientLName());
	}

}
