package ro.unibuc.hello.exception;

import ro.unibuc.hello.data.OrderEntity;

public class OfferTooLowException extends RuntimeException {
    private static final String offerTooLowTemplate = "Your offer of %s was too low.";

    public OfferTooLowException(OrderEntity entity) {
        super(String.format(offerTooLowTemplate, entity.getOffer()));
    }
}
