package ro.unibuc.gallery.exception;

import ro.unibuc.gallery.data.OrderEntity;

public class OfferTooLowException extends RuntimeException {
    private static final String offerTooLowTemplate = "Your offer of %s was too low.";

    public OfferTooLowException(OrderEntity entity) {
        super(String.format(offerTooLowTemplate, entity.getOffer()));
    }
}
