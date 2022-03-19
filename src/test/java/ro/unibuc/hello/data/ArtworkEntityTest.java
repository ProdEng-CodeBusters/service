package ro.unibuc.hello.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ArtworkEntityTest {

    ArtworkEntity artwork = new ArtworkEntity("1", "The Scream",
            "Edvard Munch",
            "Munch's The Scream is an icon of modern art, " +
                    "the Mona Lisa for our time. As Leonardo da Vinci " +
                    "evoked a Renaissance ideal of serenity and " +
                    "self-control, Munch defined how we see our " +
                    "own age - wracked with anxiety and uncertainty.",
            "https://www.edvardmunch.org/images/paintings/the-scream.jpg",
            "PAINTING");

    @Test
    void getId() {
        Assertions.assertEquals("1", artwork.getId());
    }

    @Test
    void setId() {
        artwork.setId("2");

        Assertions.assertEquals("2", artwork.getId());
    }

    @Test
    void getArtist() {
        Assertions.assertEquals("Edvard Munch", artwork.getArtist());
    }

    @Test
    void setArtist() {
        artwork.setArtist("Michelangelo");

        Assertions.assertEquals("Michelangelo", artwork.getArtist());
    }

    @Test
    void getDescription() {
    }

    @Test
    void setDescription() {
    }

    @Test
    void getImage() {
    }

    @Test
    void setImage() {
    }

    @Test
    void getTitle() {
    }

    @Test
    void setTitle() {
    }

    @Test
    void getType() {
    }

    @Test
    void setType() {
    }

    @Test
    void testToString() {
    }
}