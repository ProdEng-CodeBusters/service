package ro.unibuc.gallery.controller;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;

import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.gallery.data.ArtworkEntity;
import ro.unibuc.gallery.data.ArtworkRepository;
import ro.unibuc.gallery.data.OrderEntity;
import ro.unibuc.gallery.data.OrderRepository;
import ro.unibuc.gallery.exception.OfferTooLowException;
import ro.unibuc.gallery.exception.RecordAlreadyExistsException;

@Controller
public class AppController {

    @Autowired
    private ArtworkRepository artworkRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    MeterRegistry metricsRegistry;

    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/gallery")
    @ResponseBody
    @Timed(value = "gallery.get.time", description = "Time taken to return entire gallery")
    @Counted(value = "gallery.get.count", description = "Times the gallery was returned")
    public ResponseEntity<List> showAll(@RequestParam(name="title", required=false) String title,
                                        @RequestParam(name="artist", required=false) String artist,
                                        @RequestParam(name="type", required=false) String type) {
        metricsRegistry.counter("gallery_show_all_query_count", "endpoint", "artworks").increment(counter.incrementAndGet());

        long start = System.currentTimeMillis();

        try
        {
            List listOfArtworks = new ArrayList();
            if((title == null || title.isEmpty()) && (artist == null || artist.isEmpty()) && (type == null || type.isEmpty()))
            {
                artworkRepository.findAll().forEach(listOfArtworks::add);
            }
            else
            {
                if(artist == null || artist.isEmpty()){
                    if(title == null || title.isEmpty()){
                        artworkRepository.findByType(type).forEach(listOfArtworks::add);
                    } else {
                        artworkRepository.findByTitleContaining(title).forEach(listOfArtworks::add);
                    }
                }
                else {
                    if(title == null || title.isEmpty()){
                        if(type == null || type.isEmpty()){
                            artworkRepository.findByArtist(artist).forEach(listOfArtworks::add);
                        } else {
                            artworkRepository.findByArtist(artist).stream().filter((ArtworkEntity a) -> {
                                return Objects.equals(a.getType(), type);
                            }).forEach(listOfArtworks::add);
                        }
                    } else {
                        artworkRepository.findByTitleContaining(title).forEach(listOfArtworks::add);
                    }
                }
            }

            if(listOfArtworks.isEmpty())
            {
                throw new NoSuchElementException();
            }

            metricsRegistry.timer("gallery_show_all_query_time", "endpoint", "artworks").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return new ResponseEntity<>(listOfArtworks, HttpStatus.OK);
        }
        catch (NullPointerException e){
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/gallery/{id}")
    @Timed(value = "gallery.getById.time", description = "Time taken to return an artwork")
    @Counted(value = "gallery.getById.count", description = "Times an artwork was returned")
    public ResponseEntity getArtById(@PathVariable("id") String id)
    {
        try
        {
            Optional<ArtworkEntity> artworkOptional = artworkRepository.findById(id);
            return new ResponseEntity<>(artworkOptional.get(), HttpStatus.OK);

        }
        catch (Exception e)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/gallery")
    @ResponseBody
    public ResponseEntity addArtToGallery(@RequestBody ArtworkEntity Artwork)
    {
        try
        {
            Optional<ArtworkEntity> artworkEntity = artworkRepository.findByTitle(Artwork.getTitle());

            if(artworkEntity.isPresent()){
                throw new RecordAlreadyExistsException(artworkEntity.get());
            }

            artworkEntity = artworkRepository.findById(Artwork.getId());
            if(artworkEntity.isPresent()){
                throw new RecordAlreadyExistsException(artworkEntity.get());
            }

            ArtworkEntity createdArt = artworkRepository.save(new ArtworkEntity(Artwork.getId(),Artwork.getTitle(), Artwork.getArtist(),
                    Artwork.getDescription(), Artwork.getImage(), Artwork.getType()));
            return new ResponseEntity<>(createdArt, HttpStatus.CREATED);

        }
        catch( RecordAlreadyExistsException e){
            return new ResponseEntity<>(Artwork, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PutMapping("/gallery/{id}")
    @ResponseBody
    public ResponseEntity updateAnArtwork(@PathVariable("id") String id, @RequestBody ArtworkEntity Artwork)
    {
        try {
            Optional artworkOptional = artworkRepository.findById(id);
            deleteAnArtwork(id);
            if (artworkOptional.isPresent()) {
                ArtworkEntity updatedArtwork = (ArtworkEntity) artworkOptional.get();
                updatedArtwork.setId(Artwork.getId());
                updatedArtwork.setTitle(Artwork.getTitle());
                updatedArtwork.setArtist(Artwork.getArtist());
                updatedArtwork.setImage(Artwork.getImage());
                updatedArtwork.setDescription(Artwork.getDescription());
                updatedArtwork.setType(Artwork.getType());
                return new ResponseEntity<>(artworkRepository.save(updatedArtwork), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
            }
        } catch ( NullPointerException e ){
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
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
    @Timed(value = "order.get.time", description = "Time taken to return all orders")
    @Counted(value = "order.get.count", description = "Times all orders were returned")
    public ResponseEntity<List> getOrders(@RequestParam(name="clientName", required=false) String clientName,
                                          @RequestParam(name="artworkName", required=false) String artworkName) {
        try
        {
            List listOfOrders= new ArrayList();
            if((clientName == null || clientName.isEmpty()) && (artworkName == null || artworkName.isEmpty()))
            {
                orderRepository.findAll().stream().sorted(Comparator.comparingInt(OrderEntity::getOffer).reversed())
                        .forEach(listOfOrders::add);
            }
            else
            {
                if( artworkName == null || artworkName.isEmpty()) {
                    orderRepository.findByClientName(clientName).stream()
                            .sorted(Comparator.comparingInt(OrderEntity::getOffer).reversed())
                            .forEach(listOfOrders::add);
                } else {
                    if( clientName == null || clientName.isEmpty()){
                        orderRepository.findByArtworkName(artworkName).stream()
                                .sorted(Comparator.comparingInt(OrderEntity::getOffer).reversed())
                                .forEach(listOfOrders::add);
                    } else {
                        orderRepository.findByArtworkName(artworkName)
                                .stream().filter((OrderEntity order) -> {
                                    return Objects.equals(order.getClientName(), clientName);
                        }).sorted(Comparator.comparingInt(OrderEntity::getOffer).reversed()).forEach(listOfOrders::add);
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
    @Timed(value = "order.getById.time", description = "Time taken to return an order")
    @Counted(value = "order.getById.count", description = "Times an order was returned")
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
            Optional<OrderEntity> orderEntity = orderRepository.findById(order.getId());
            if( orderEntity.isPresent()){
                throw new RecordAlreadyExistsException(order);
            }
            orderEntity = orderRepository.findByArtworkName(order.getArtworkName()).stream()
                    .max(Comparator.comparingInt(OrderEntity::getOffer));
            if(orderEntity.isPresent() && orderEntity.get().getOffer() >= order.getOffer()){
                throw new OfferTooLowException(order);
            }
            OrderEntity newOrder = orderRepository.save(new OrderEntity(order.getId(), order.getClientName(),
                    order.getArtworkName(),
                    order.getOffer(),
                    order.getEmail(),
                    order.getPhoneNumber()));

            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);

        }
        catch (OfferTooLowException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
        catch (NoSuchElementException e)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        catch (RecordAlreadyExistsException e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.IM_USED);
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
        try {
            Optional orderOptional = orderRepository.findById(id);
            deleteAnOrder(id);
            if (orderOptional.isPresent()) {
                OrderEntity updatedOrder = (OrderEntity) orderOptional.get();
                updatedOrder.setId(Order.getId());
                updatedOrder.setArtworkName(Order.getArtworkName());
                updatedOrder.setClientName(Order.getClientName());
                updatedOrder.setOffer(Order.getOffer());
                updatedOrder.setEmail(Order.getEmail());
                updatedOrder.setPhoneNumber(Order.getPhoneNumber());
                return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
