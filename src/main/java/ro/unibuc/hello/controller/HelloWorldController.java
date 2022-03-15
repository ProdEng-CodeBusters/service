package ro.unibuc.hello.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.ArtworkEntity;
import ro.unibuc.hello.data.ArtworkRepository;
import ro.unibuc.hello.data.OrderEntity;
import ro.unibuc.hello.data.OrderRepository;
import ro.unibuc.hello.dto.Greeting;

@Controller
public class HelloWorldController {

    @Autowired
    private ArtworkRepository artworkRepository;
    @Autowired
    private OrderRepository orderRepository;

    private static final String helloTemplate = "Hello, %s!";
    private static final String informationTemplate = "%s : %s %s!";
    private static final String artworkTemplate = "%s by %s \n %s \n Link to image: ";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/hello-world")
    @ResponseBody
    public Greeting sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(helloTemplate, name));
    }

    @GetMapping("/info")
    @ResponseBody
    public Greeting listAll(@RequestParam(name="title", required=false, defaultValue="Overview") String title) {
        Optional<ArtworkEntity> entity = artworkRepository.findByTitle(title);
        return new Greeting(counter.incrementAndGet(), String.format(informationTemplate, entity.get().getTitle(),
                entity.get().getDescription(), entity.get().getImage()));
    }

    @GetMapping("/gallery")
    @ResponseBody
    public ResponseEntity<List> showAll(@RequestParam(name="title", required=false) String title,
                                        @RequestParam(name="artist", required=false) String artist) {

        try
        {
            List listOfArtworks = new ArrayList();
            if((title == null || title.isEmpty()) && (artist == null || artist.isEmpty()))
            {
                artworkRepository.findAll().forEach(listOfArtworks::add);
            }
            else
            {
                if(artist == null || artist.isEmpty()){
                    artworkRepository.findByTitleContaining(title).forEach(listOfArtworks::add);
                }
                else {
                    artworkRepository.findByArtist(artist).forEach(listOfArtworks::add);
                }
            }

            if(listOfArtworks.isEmpty())
            {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(listOfArtworks, HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/gallery/{id}")
    public ResponseEntity getArtById(@PathVariable("id") String id)
    {
        try
        {
            Optional artworkOptional = artworkRepository.findById(id);
            return new ResponseEntity<>(artworkOptional.get(), HttpStatus.OK);

        }
        catch (Exception e)
        {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/gallery")
    @ResponseBody
    public ResponseEntity addArtToGallery(@RequestBody ArtworkEntity Artwork)
    {
        try
        {
            ArtworkEntity createdArt = artworkRepository.save(new ArtworkEntity(Artwork.getId(),Artwork.getTitle(), Artwork.getArtist(),
                    Artwork.getDescription(),Artwork.getImage()));
            System.out.println(createdArt);
            return new ResponseEntity<>(createdArt, HttpStatus.CREATED);

        }
        catch (Exception e)
        {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PutMapping("/gallery/{id}")
    @ResponseBody
    public ResponseEntity updateAnArtwork(@PathVariable("id") String id, @RequestBody ArtworkEntity Artwork)
    {
        Optional artworkOptional = artworkRepository.findById(id);
        deleteAnArtwork(id);
        if(artworkOptional.isPresent())
        {
            ArtworkEntity updatedArtwork = (ArtworkEntity) artworkOptional.get();
            updatedArtwork.setId(Artwork.getId());
            updatedArtwork.setTitle(Artwork.getTitle());
            updatedArtwork.setArtist(Artwork.getArtist());
            updatedArtwork.setDescription(Artwork.getDescription());
            return new ResponseEntity<>(artworkRepository.save(updatedArtwork), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @DeleteMapping("/gallery/{id}")
    @Transactional
    public ResponseEntity deleteAnArtwork(@PathVariable("id") String id)
    {
        try
        {
            artworkRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/order")
    @ResponseBody
    public ResponseEntity<List> getOrders(@RequestParam(name="clientName", required=false) String clientName,
                                          @RequestParam(name="artworkName", required=false) String artworkName) {
        try
        {
            List listOfOrders= new ArrayList();
            if((clientName == null || clientName.isEmpty()) && (artworkName == null || artworkName.isEmpty()))
            {
                orderRepository.findAll().forEach(listOfOrders::add);
            }
            else
            {
                if( artworkName == null || artworkName.isEmpty()) {
                    orderRepository.findByClientName(clientName).forEach(listOfOrders::add);
                } else {
                    if( clientName == null || clientName.isEmpty()){
                        orderRepository.findByArtworkName(artworkName).forEach(listOfOrders::add);
                    }
                }
            }

            if(listOfOrders.isEmpty())
            {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(listOfOrders, HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/order/{id}")
    @ResponseBody
    public ResponseEntity getOrderById(@PathVariable("id") String id)
    {
        try
        {
            Optional orderOptional = orderRepository.findById(id);
            return new ResponseEntity<>(orderOptional.get(), HttpStatus.OK);

        }
        catch (Exception e)
        {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity placeOrder(@RequestBody OrderEntity order)
    {
        try
        {
            Optional artworkEntity = artworkRepository.findByTitle(order.getArtworkName());
            if(artworkEntity.isEmpty()){
                throw new NoSuchElementException();
            }

            OrderEntity newOrder = orderRepository.save(new OrderEntity(order.getId(), order.getClientName(),
                    order.getArtworkName(),
                    order.getOffer(),
                    order.getEmail(),
                    order.getPhoneNumber()));
            System.out.println(newOrder);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);

        }
        catch (NoSuchElementException e)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>( null, HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PutMapping("/order/{id}")
    @ResponseBody
    public ResponseEntity updateAnOrder(@PathVariable("id") String id, @RequestBody OrderEntity Order)
    {
        Optional orderOptional = orderRepository.findById(id);
        deleteAnOrder(id);
        if(orderOptional.isPresent())
        {
            OrderEntity updatedOrder = (OrderEntity) orderOptional.get();
            updatedOrder.setId(Order.getId());
            updatedOrder.setArtworkName(Order.getArtworkName());
            updatedOrder.setClientName(Order.getClientName());
            updatedOrder.setOffer(Order.getOffer());
            updatedOrder.setEmail(Order.getEmail());
            updatedOrder.setPhoneNumber(Order.getPhoneNumber());
            return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @DeleteMapping("/order/{id}")
    @Transactional
    public ResponseEntity deleteAnOrder(@PathVariable("id") String id)
    {
        try
        {
            orderRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
