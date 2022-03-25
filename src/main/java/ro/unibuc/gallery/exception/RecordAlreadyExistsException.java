package ro.unibuc.gallery.exception;

import ro.unibuc.gallery.data.ArtworkEntity;
import ro.unibuc.gallery.data.OrderEntity;

public class RecordAlreadyExistsException extends RuntimeException {
    private static final String recordAlreadyExistsTemplate = "%s with given id or name already exists.";

    public RecordAlreadyExistsException(OrderEntity entity) {
        super(String.format(recordAlreadyExistsTemplate, OrderEntity.class));
    }

    public RecordAlreadyExistsException(ArtworkEntity entity) {
        super(String.format(recordAlreadyExistsTemplate, ArtworkEntity.class));
    }
}
