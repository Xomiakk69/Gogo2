package com.example.betaforall.api;

import com.google.gson.annotations.SerializedName;

public class NominatimResponse {

    @SerializedName("place_id")
    private long placeId;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lon")
    private String longitude;

    @SerializedName("address")
    private Address address;

    // Геттеры и сеттеры
    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "NominatimResponse{" +
                "placeId=" + placeId +
                ", displayName='" + displayName + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", address=" + address +
                '}';
    }

    public static class Address {
        @SerializedName("city")
        private String city;

        @SerializedName("road")
        private String road;

        @SerializedName("house_number")
        private String houseNumber;

        // Геттеры и сеттеры
        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRoad() {
            return road;
        }

        public void setRoad(String road) {
            this.road = road;
        }

        public String getHouseNumber() {
            return houseNumber;
        }

        public void setHouseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "city='" + city + '\'' +
                    ", road='" + road + '\'' +
                    ", houseNumber='" + houseNumber + '\'' +
                    '}';
        }
    }
}
