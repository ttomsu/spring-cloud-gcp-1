package org.springframework.cloud.gcp.data.datastore.it;

import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.it.GqlRepositoryIntegrationTests.Customer;
import org.springframework.cloud.gcp.data.datastore.it.GqlRepositoryIntegrationTests.CustomerProjection;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { DatastoreIntegrationTestConfiguration.class })
public class GqlRepositoryIntegrationTests {

	@Autowired
	private CustomerRepository customerRepository;

	@BeforeClass
	public static void checkToRun() {
		// assumeThat(
		// 		"Datastore integration tests are disabled. Please use '-Dit.datastore=true' "
		// 				+ "to enable them. ",
		// 		System.getProperty("it.datastore"), is("true"));
	}

	@Before
	public void clearDatastoreEntities() {
		customerRepository.deleteAll();
	}

	@Test
	public void selectGqlQueryWithProjection() {
		Customer customer = new Customer(1, "Smith", true);
		customerRepository.save(customer);

		List<String> names =
				customerRepository.findByIsActive(true, PageRequest.of(0, 100)).get()
						.map(projection -> projection.getLastName())
						.collect(Collectors.toList());

		System.out.println("Hello ! ! ! !");
		System.out.println(names);
	}

	@Entity(name = "Customer")
	static class Customer {

		@Id
		long id;

		String lastName;

		boolean isActive;

		public Customer(long id, String lastName, boolean isActive) {
			this.id = id;
			this.lastName = lastName;
			this.isActive = isActive;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public boolean isActive() {
			return isActive;
		}

		public void setActive(boolean active) {
			isActive = active;
		}

		@Override
		public String toString() {
			return "Customer{" +
					"lastName='" + lastName + '\'' +
					", isActive=" + isActive +
					'}';
		}
	}

	public interface CustomerProjection {
		String getLastName();
	}

}

interface CustomerRepository extends DatastoreRepository<Customer, Long> {
	@Query("SELECT * from Customer WHERE isActive = @is_active")
	Page<CustomerProjection> findByIsActive(@Param("is_active") boolean isActive, Pageable pageable);
}
