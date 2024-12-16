package com.example.betaforall.model;
public class DeliveryLocation {
    private String id; // Уникальный ключ записи
    private String addressin;
    private String addressfor;
    private String statusdost;
    private String deliveryDuration;

    // Конструктор по умолчанию для Firebase
    public DeliveryLocation() {}

    public DeliveryLocation(String id, String addressin, String addressfor, String statusdost, String deliveryDuration) {
        this.id = id;
        this.addressin = addressin;
        this.addressfor = addressfor;
        this.statusdost = statusdost;
        this.deliveryDuration = deliveryDuration;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Остальные геттеры и сеттеры

    public String getAddressin() {
        return addressin;
    }

    public void setAddressin(String addressin) {
        this.addressin = addressin;
    }

    public String getAddressfor() {
        return addressfor;
    }

    public void setAddressfor(String addressfor) {
        this.addressfor = addressfor;
    }

    public String getStatusdost() {
        return statusdost;
    }

    public void setStatusdost(String statusdost) {
        this.statusdost = statusdost;
    }

    public String getDeliveryDuration() {
        return deliveryDuration;
    }

    public void setDeliveryDuration(String deliveryDuration) {
        this.deliveryDuration = deliveryDuration;
    }

    @Override
    public String toString() {
        return "DeliveryLocation{" +
                "addressin='" + addressin + '\'' +
                ", addressfor='" + addressfor + '\'' +
                ", statusdost='" + statusdost + '\'' +
                ", deliveryDuration='" + deliveryDuration + '\'' +
                '}';
    }
}
