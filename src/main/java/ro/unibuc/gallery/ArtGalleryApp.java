package ro.unibuc.gallery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import ro.unibuc.gallery.data.*;
import ro.unibuc.gallery.data.ArtworkRepository;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories
public class ArtGalleryApp {

	@Autowired
	private ArtworkRepository artworkRepository;

	@Autowired
	private OrderRepository orderRepository;

	public static void main(String[] args) {
		SpringApplication.run(ArtGalleryApp.class, args);
	}

	@PostConstruct
	public void runAfterObjectCreated() {
		artworkRepository.deleteAll();
		orderRepository.deleteAll();
		artworkRepository.save(new ArtworkEntity("1","The Scream",
				"Edvard Munch",
				"Munch's The Scream is an icon of modern art, " +
						"the Mona Lisa for our time. As Leonardo da Vinci " +
						"evoked a Renaissance ideal of serenity and " +
						"self-control, Munch defined how we see our " +
						"own age - wracked with anxiety and uncertainty.",
				"https://www.edvardmunch.org/images/paintings/the-scream.jpg",
				"PAINTING"));
		orderRepository.save(new OrderEntity("1", "Robert Johnson",
				"The Scream", 100000, "robjohn@gmail.co.uk", "+44067245344"));
	}

}
