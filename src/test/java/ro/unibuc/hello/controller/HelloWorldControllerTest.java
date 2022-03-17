package ro.unibuc.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.hello.data.ArtworkEntity;
import ro.unibuc.hello.data.ArtworkRepository;
import ro.unibuc.hello.data.OrderEntity;
import ro.unibuc.hello.data.OrderRepository;
import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.exception.OfferTooLowException;
import ro.unibuc.hello.exception.RecordAlreadyExistsException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class hHelloWorldControllerTest {

    @Mock
    private ArtworkRepository artworkRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private  HelloWorldController mockController;

    @InjectMocks
    private HelloWorldController helloWorldController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(helloWorldController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void test_sayHello() throws Exception {
        // Arrange
        Greeting greeting = new Greeting(1, "Hello, there!");

        when(mockController.sayHello(any())).thenReturn(greeting);

        // Act
        MvcResult result = mockMvc.perform(get("/hello-world?name=there")
                        .content(objectMapper.writeValueAsString(greeting))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        Assertions.assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(greeting));
    }

    @Test
    void test_getArtById() {

        // Arrange
        String id = "1";
        Optional<ArtworkEntity> artworkEntity = Optional.of(new ArtworkEntity(id, "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING"));
        when(artworkRepository.findById(id)).thenReturn(artworkEntity);

        // Act
        ResponseEntity result = helloWorldController.getArtById("1");

        // Assert
        Assertions.assertEquals(result.getBody(), artworkEntity.get());
    }

    @Test
    void getArtById_NotFoundException() {
        // Arrange
        String id = "2";
        Optional<ArtworkEntity> artworkEntity = Optional.of(new ArtworkEntity(id, "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING"));
        when(artworkRepository.findById(id)).thenReturn(null);

        // Act
        ResponseEntity result = helloWorldController.getArtById(id);

        // Assert
        assertNull(result.getBody());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void test_showAll_byTitle() {
        // Arrange
        String title = "The Scream";
        ArtworkEntity artworkEntity = new ArtworkEntity("1", title,
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");
        List<ArtworkEntity> artworkEntityList = new ArrayList<>();
        artworkEntityList.add(artworkEntity);
        when(artworkRepository.findByTitleContaining(title)).thenReturn(artworkEntityList);

        // Act
        ResponseEntity result = helloWorldController.showAll(title, null, null);

        // Assert
        Assertions.assertEquals(result.getBody(), artworkEntityList);
    }

    @Test
    void test_showAll_byType() {
        // Arrange
        String type = "PAINTING";
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                type);
        List<ArtworkEntity> artworkEntityList = new ArrayList<>();
        artworkEntityList.add(artworkEntity);
        when(artworkRepository.findByType(type)).thenReturn(artworkEntityList);

        // Act
        ResponseEntity result = helloWorldController.showAll(null, null, type);

        // Assert
        Assertions.assertEquals(result.getBody(), artworkEntityList);
    }

    @Test
    void test_showAll_byArtist() {
        // Arrange
        String artist = "Edvard Munch";
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");
        List<ArtworkEntity> artworkEntityList = new ArrayList<>();
        artworkEntityList.add(artworkEntity);
        when(artworkRepository.findByArtist(artist)).thenReturn(artworkEntityList);

        // Act
        ResponseEntity result = helloWorldController.showAll(null, artist, null);

        // Assert
        Assertions.assertEquals(result.getBody(), artworkEntityList);
    }

    @Test
    void test_showAll_exception_not_found() {
        // Arrange
        String title = "The Creation of Adam";
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");
        List<ArtworkEntity> artworkEntityList = new ArrayList<>();
        artworkEntityList.add(artworkEntity);
        when(artworkRepository.findByTitleContaining(title)).thenReturn(null);

        // Act
        ResponseEntity<List> result = helloWorldController.showAll(title, null, null);

        // Assert
        assertNull(result.getBody());
        Assertions.assertEquals(result.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    void test_addArtToGallery() {
        // Arrange
        String title = "The Scream";
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");

        when(artworkRepository.findByTitle(title)).thenReturn(Optional.empty());
        when(artworkRepository.findById("1")).thenReturn(Optional.empty());
        when(artworkRepository.save(any())).thenReturn(artworkEntity);

        // Act
        ResponseEntity result = helloWorldController.addArtToGallery(artworkEntity);

        // Assert
        Assertions.assertEquals(artworkEntity, result.getBody());

        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void test_addArtToGallery_recordExistsException() {
        // Arrange
        String title = "The Scream";
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");
        List<ArtworkEntity> artworkEntityList = new ArrayList<>();
        artworkEntityList.add(artworkEntity);
        when(artworkRepository.findById("1")).thenReturn(Optional.of(artworkEntity));

        // Act
        ResponseEntity result = helloWorldController.addArtToGallery(artworkEntity);

        // Assert
        Assertions.assertEquals(artworkEntity, result.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void test_updateAnArtwork() {
        // Arrange
        String id = "1";
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");

        when(artworkRepository.findById(id)).thenReturn(Optional.of(artworkEntity));

        // Act
        ResponseEntity result = helloWorldController.updateAnArtwork(id, artworkEntity);

        // Assert
        Assertions.assertEquals(null, result.getBody());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void test_updateAnArtwork_NotFoundException() {
        // Arrange
        String id = "2";
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");

        when(artworkRepository.findById(id)).thenReturn(null);

        // Act
        ResponseEntity result = helloWorldController.updateAnArtwork(id, artworkEntity);

        // Assert
        Assertions.assertEquals(id, result.getBody());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void test_deleteAnArtwork() {
        // Arrange
        String id = "1";
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");

        //when(artworkRepository.deleteById(id).thenReturn(null);

        // Act
        ResponseEntity result = helloWorldController.deleteAnArtwork(id);

        // Assert
        //Assertions.assertEquals(artworkEntity, result.getBody());
        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void test_getOrders_byArtwork() {
        // Arrange
        String artwork = "The Scream";
        OrderEntity orderEntity = new OrderEntity("1",
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(orderEntity);
        when(orderRepository.findByArtworkName(artwork)).thenReturn(orderEntityList);

        // Act
        ResponseEntity result = helloWorldController.getOrders(null, artwork);

        // Assert
        Assertions.assertEquals(result.getBody(), orderEntityList);
    }

    @Test
    void test_getOrders_byClientName() {
        // Arrange
        String clientName = "Robert Johnson";
        OrderEntity orderEntity = new OrderEntity("1",
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(orderEntity);
        when(orderRepository.findByClientName(clientName)).thenReturn(orderEntityList);

        // Act
        ResponseEntity result = helloWorldController.getOrders(clientName, null);

        // Assert
        Assertions.assertEquals(result.getBody(), orderEntityList);
    }

    @Test
    void test_getOrders_noParams() {
        // Arrange
        OrderEntity orderEntity = new OrderEntity("1",
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(orderEntity);
        when(orderRepository.findAll()).thenReturn(orderEntityList);

        // Act
        ResponseEntity result = helloWorldController.getOrders(null, null);

        // Assert
        Assertions.assertEquals(result.getBody(), orderEntityList);
    }


    @Test
    void test_getOrderById() {
        // Arrange
        String id = "1";
        Optional<OrderEntity> orderEntity = Optional.of(new OrderEntity(id,
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344"));
        when(orderRepository.findById(id)).thenReturn(orderEntity);

        // Act
        ResponseEntity result = helloWorldController.getOrderById("1");

        // Assert
        Assertions.assertEquals(result.getBody(), orderEntity.get());
    }
    @Test
    void test_placeOrder() {

        // Arrange
        String old_order_id = "1";
        String new_order_id = "2";
        OrderEntity old_orderEntity = new OrderEntity(old_order_id,
                "Robert Johnson",
                "The Scream",
                10000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(old_orderEntity);
        OrderEntity new_orderEntity = new OrderEntity(new_order_id,
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");
        when(artworkRepository.findByTitle("The Scream")).thenReturn(Optional.of(artworkEntity));
        when(orderRepository.findById(old_order_id)).thenReturn(null);
        when(orderRepository.findByArtworkName("The Scream")).thenReturn(orderEntityList);
        when(orderRepository.save(any())).thenReturn(new_orderEntity);
        // Act

        ResponseEntity result = helloWorldController.placeOrder(new_orderEntity);

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertEquals(new_orderEntity, result.getBody());

    }

    @Test
    void test_placeOrder_OfferTooLowException() {

        // Arrange
        String old_order_id = "1";
        String new_order_id = "2";
        OrderEntity old_orderEntity = new OrderEntity(old_order_id,
                "Robert Johnson",
                "The Scream",
                1000000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(old_orderEntity);
        OrderEntity new_orderEntity = new OrderEntity(new_order_id,
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");
        OfferTooLowException exception = new OfferTooLowException(new_orderEntity);
        when(artworkRepository.findByTitle("The Scream")).thenReturn(Optional.of(artworkEntity));
        when(orderRepository.findById(old_order_id)).thenReturn(null);
        when(orderRepository.findByArtworkName("The Scream")).thenReturn(orderEntityList);
        // Act

        ResponseEntity result = helloWorldController.placeOrder(new_orderEntity);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, result.getStatusCode());
        Assertions.assertEquals(exception.getMessage(), result.getBody());
    }

    @Test
    void test_placeOrder_RecordAlreadyExistsException() {

        // Arrange
        String old_order_id = "1";
        String new_order_id = "1";
        OrderEntity old_orderEntity = new OrderEntity(old_order_id,
                "Robert Johnson",
                "The Scream",
                1000000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(old_orderEntity);
        OrderEntity new_orderEntity = new OrderEntity(new_order_id,
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");
        RecordAlreadyExistsException exception = new RecordAlreadyExistsException(new_orderEntity);
        when(artworkRepository.findByTitle("The Scream")).thenReturn(Optional.of(artworkEntity));
        when(orderRepository.findById(old_order_id)).thenReturn(Optional.of(old_orderEntity));
        // Act

        ResponseEntity result = helloWorldController.placeOrder(new_orderEntity);

        // Assert
        Assertions.assertEquals(HttpStatus.IM_USED, result.getStatusCode());
        Assertions.assertEquals(exception.getMessage(), result.getBody());
    }

    @Test
    void test_placeOrder_NoSuchElementException() {
        // Arrange
        String new_order_id = "2";

        OrderEntity new_orderEntity = new OrderEntity(new_order_id,
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        ArtworkEntity artworkEntity = new ArtworkEntity("1", "The Scream",
                "Edvard Munch",
                "Munch's The Scream is an icon of modern art, " +
                        "the Mona Lisa for our time. As Leonardo da Vinci " +
                        "evoked a Renaissance ideal of serenity and " +
                        "self-control, Munch defined how we see our " +
                        "own age - wracked with anxiety and uncertainty.",
                "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
                "PAINTING");
        when(artworkRepository.findByTitle("Not a tea pot")).thenReturn(Optional.of(artworkEntity));
        // Act

        ResponseEntity result = helloWorldController.placeOrder(new_orderEntity);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        Assertions.assertEquals(null, result.getBody());
    }

    @Test
    void test_updateAnOrder() {
        // Arrange
        String old_order_id = "1";
        String new_order_id = "1";
        OrderEntity old_orderEntity = new OrderEntity(old_order_id,
                "Robert Johnson",
                "The Scream",
                10000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(old_orderEntity);
        OrderEntity new_orderEntity = new OrderEntity(new_order_id,
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344");

        when(orderRepository.findById(old_order_id)).thenReturn(Optional.of(old_orderEntity));
        when(orderRepository.save(any())).thenReturn(new_orderEntity);

        // Act

        ResponseEntity result = helloWorldController.updateAnOrder(old_order_id,new_orderEntity);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(new_orderEntity, result.getBody());
    }
    @Test
    void test_updateAnOrder_Exception() {
        // Arrange
        String old_order_id = "1";
        String new_order_id = "1";
        OrderEntity old_orderEntity = new OrderEntity(old_order_id,
                "Robert Johnson",
                "The Scream",
                10000,
                "robjohn@gmail.co.uk",
                "+44067245344");
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(old_orderEntity);
        OrderEntity new_orderEntity = new OrderEntity(new_order_id,
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344");

        when(orderRepository.findById(old_order_id)).thenReturn(null);

        // Act

        ResponseEntity result = helloWorldController.updateAnOrder(old_order_id,new_orderEntity);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        Assertions.assertEquals(new_order_id, result.getBody());
    }

    @Test
    void test_deleteAnOrder() {
        // Arrange
        String id = "1";
        OrderEntity orderEntity = new OrderEntity("1",
                "Robert Johnson",
                "The Scream",
                100000,
                "robjohn@gmail.co.uk",
                "+44067245344");

        // Act
        ResponseEntity result = helloWorldController.deleteAnArtwork(orderEntity.getId());

        // Assert
        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}