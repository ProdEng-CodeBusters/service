package ro.unibuc.hello;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ro.unibuc.hello.controller.HelloWorldController;
import ro.unibuc.hello.data.ArtworkEntity;
import ro.unibuc.hello.data.ArtworkRepository;
import ro.unibuc.hello.data.OrderEntity;
import ro.unibuc.hello.data.OrderRepository;
import ro.unibuc.hello.exception.OfferTooLowException;
import ro.unibuc.hello.exception.RecordAlreadyExistsException;

@SpringBootTest
class HelloApplicationTestsIT {

	@Autowired
	ArtworkRepository mockArtworkRepository;

	@Autowired
	OrderRepository mockOrderRepository;

	@Autowired
	HelloWorldController mockController;

	@Test
	void test_AddArtwork_Success() {
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



	@Test
	void test_AddOrder_AlreadyExistsException() {
		//Arrange
		OrderEntity newOrder = new OrderEntity("1", "Robert Johnson",
				"The Scream", 100000, "robjohn@gmail.co.uk", "+44067245344");

		//Act
		ResponseEntity response = mockController.placeOrder(newOrder);
		RecordAlreadyExistsException e = new RecordAlreadyExistsException(newOrder);

		//Assert
		Assertions.assertEquals(HttpStatus.IM_USED, response.getStatusCode());
		Assertions.assertEquals(e.getMessage(), response.getBody());
	}

	@Test
	void test_AddOrder_OfferToLowException() {
		//Arrange
		OrderEntity newOrder = new OrderEntity("2", "Clyde Riversdale",
				"The Scream", 10000, "criversdale@gmail.co.uk", "+4406741100");

		//Act
		ResponseEntity response = mockController.placeOrder(newOrder);
		OfferTooLowException e = new OfferTooLowException(newOrder);

		//Assert
		Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
		Assertions.assertEquals(e.getMessage(), response.getBody());
	}

	@Test
	void test_ModifyArtwork_Success() {
		//Arrange
		ArtworkEntity artworkEntity = new ArtworkEntity("1","The Scream",
				"Edvard Munch",
				"Munch's The Scream is an icon of modern art, " +
						"the Mona Lisa for our time." ,
				"https://www.edvardmunch.org/images/paintings/the-scream.jpg",
				"PAINTING");
		//Act
		ResponseEntity response = mockController.updateAnArtwork("1", artworkEntity);

		//Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(artworkEntity, (response.getBody()));
	}

	@Test
	void test_DeleteArtwork_NotFoundException() {

		//Act
		ResponseEntity response = mockController.deleteAnArtwork("3");

		//Assert
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

}
