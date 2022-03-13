package ro.unibuc.hello;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ro.unibuc.hello.data.ArtworkRepository;
import ro.unibuc.hello.data.OrderRepository;

@SpringBootTest
class HelloApplicationTests {

	@MockBean
	ArtworkRepository mockRepository;

	@MockBean
	OrderRepository mockOrderRepository;

	@Test
	void contextLoads() {
	}

}
