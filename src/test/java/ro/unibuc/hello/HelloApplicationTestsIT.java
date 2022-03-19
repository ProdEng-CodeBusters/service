package ro.unibuc.hello;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ro.unibuc.hello.controller.HelloWorldController;
import ro.unibuc.hello.data.ArtworkEntity;
import ro.unibuc.hello.data.ArtworkRepository;
import ro.unibuc.hello.data.OrderRepository;

@SpringBootTest
class HelloApplicationTests {

	@Autowired
	ArtworkRepository mockArtworkRepository;

	@Autowired
	HelloWorldController mockController;

	@Test
	void test_AddArtwork() {
		//Arrange
		ArtworkEntity artworkEntity = new ArtworkEntity("3",
				"David",
				"Michelangelo",
				"David is a masterpiece of Renaissance sculpture, created in marble between 1501 and 1504 by the Italian artist Michelangelo. David is a 5.17-metre (17 ft 0 in) marble statue of the Biblical figure David, a favoured subject in the art of Florence.",
				"https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/Michelangelo%27s_David_-_right_view_2.jpg/409px-Michelangelo%27s_David_-_right_view_2.jpg",
				"SCULPTURE");

		//Act
		ResponseEntity response = mockController.addArtToGallery(artworkEntity);

		//Assert
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		Assertions.assertEquals(artworkEntity, response.getBody());
	}

}
