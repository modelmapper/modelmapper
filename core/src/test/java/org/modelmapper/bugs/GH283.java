package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * Test that demonstrates how explicitly mapped properties are overwritten by field name matching. 
 * 
 * @author boris.strandjev
 */
@Test
public class GH283 extends AbstractTest {

	public static class PersonDetails {
		private String firstName;
		private String lastName;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	}

	public static class Requester {
		private PersonDetails person;
		private PersonDetails unverifiedRequester;

		public PersonDetails getUnverifiedRequester() {
			return unverifiedRequester;
		}

		public void setUnverifiedRequester(PersonDetails unverifiedRequester) {
			this.unverifiedRequester = unverifiedRequester;
		}

		public PersonDetails getPerson() {
			return person;
		}

		public void setPerson(PersonDetails person) {
			this.person = person;
		}
	}

	public static class Order {
		private Requester requester;

		public Requester getRequester() {
			return requester;
		}

		public void setRequester(Requester requester) {
			this.requester = requester;
		}
	}

	public static class OrderView {
		private PersonDetails requester;

		public PersonDetails getRequester() {
			return requester;
		}

		public void setRequester(PersonDetails requester) {
			this.requester = requester;
		}
	}

	public void test() {
		modelMapper.addMappings(new PropertyMap<Requester, PersonDetails>() {
			@Override
			protected void configure() {
				map(source.getPerson().getFirstName()).setFirstName(null);
				map(source.getPerson().getLastName()).setLastName(null);
			}
		});
		modelMapper.addMappings(new PropertyMap<Order, OrderView>() {
			@Override
			protected void configure() {
				map(source.getRequester()).setRequester(null);
			}
		});

		String requesterFirstName = "John";
		String requesterLastName = "Doe";
		PersonDetails person = new PersonDetails();
		person.setFirstName(requesterFirstName);
		person.setLastName(requesterLastName);
		Requester requester = new Requester();
		requester.setPerson(person);
		Order order = new Order();
		order.setRequester(requester);

		OrderView orderView = modelMapper.map(order, OrderView.class);
		assertEquals(orderView.getRequester().getFirstName(), requesterFirstName,
				"Expected the correct requested first name");
		assertEquals(orderView.getRequester().getLastName(), requesterLastName,
				"Expected the correct requested last name");
	}
}