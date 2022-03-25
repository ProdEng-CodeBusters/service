package ro.unibuc.gallery.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * No need to implement this interface.
 * Spring Data MongoDB automatically creates a class it implementing the interface when you run the application.
 */
@Repository
public interface ArtworkRepository extends MongoRepository<ArtworkEntity, String> {

    public List<ArtworkEntity> findByTitleContaining(String title);
    public Optional<ArtworkEntity> findByTitle(String title);
    public List<ArtworkEntity> findByType(String type);
    public List<ArtworkEntity> findByArtist(String artist);
    public Optional<ArtworkEntity> findById(String id);

}