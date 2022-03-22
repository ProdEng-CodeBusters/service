package ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Objects;

public class OrderEntity {

    @Id
    @Indexed(unique = true)
    public String id;

    public String clientName;
    public String artworkName;
    public Integer offer;
    public String email;
    public String phoneNumber;

    public OrderEntity(String id, String clientName, String artworkName, Integer offer, String email, String phoneNumber){
        this.id = id;
        this.clientName = clientName;
        this.artworkName = artworkName;
        this.offer = offer;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtworkName() {
        return artworkName;
    }

    public void setArtworkName(String artworkName) {
        this.artworkName = artworkName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getOffer() {
        return offer;
    }

    public void setOffer(Integer offer) {
        this.offer = offer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return id.equals(that.id) && clientName.equals(that.clientName) && artworkName.equals(that.artworkName) && offer.equals(that.offer) && email.equals(that.email) && phoneNumber.equals(that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientName, artworkName, offer, phoneNumber);
    }
}
