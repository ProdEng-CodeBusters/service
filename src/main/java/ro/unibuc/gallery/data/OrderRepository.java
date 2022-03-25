package ro.unibuc.gallery.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<OrderEntity, String>{

    public List<OrderEntity> findByClientName(String clientName);
    public List<OrderEntity> findByArtworkName(String artworkId);
    public Optional<OrderEntity> findById(String id);

}
