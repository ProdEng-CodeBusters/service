package ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Objects;

public class ArtworkEntity {

    @Id
    @Indexed(unique = true)
    public String id;

    public String title;
    public String artist;
    public String description;
    public String image;
    public String type;

    public ArtworkEntity(String id,String title, String artist, String description, String image, String type) {
        this.id=id;
        this.title = title;
        this.artist = artist;
        this.description = description;
        this.image = image;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format(
                "Information[title='%s', description='%s', image='%s']",
                id, title, description, image);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtworkEntity that = (ArtworkEntity) o;
        return id.equals(that.id) && title.equals(that.title) && artist.equals(that.artist) && Objects.equals(description, that.description) && Objects.equals(image, that.image) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, artist);
    }
}