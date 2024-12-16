package com.example.betaforall.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MapLibreGeocoderResponse {
    @SerializedName("type")
    private String type;

    @SerializedName("query")
    private List<String> query;

    @SerializedName("features")
    private List<Feature> features;

    public String getType() {
        return type;
    }

    public List<String> getQuery() {
        return query;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public static class Feature {
        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;

        @SerializedName("place_type")
        private List<String> placeType;

        @SerializedName("text")
        private String text;

        @SerializedName("place_name")
        private String placeName;

        @SerializedName("geometry")
        private Geometry geometry;

        @SerializedName("context")
        private List<Context> context;

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public List<String> getPlaceType() {
            return placeType;
        }

        public String getText() {
            return text;
        }

        public String getPlaceName() {
            return placeName;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public List<Context> getContext() {
            return context;
        }
    }

    public static class Geometry {
        @SerializedName("type")
        private String type;

        @SerializedName("coordinates")
        private List<Double> coordinates;

        public String getType() {
            return type;
        }

        public List<Double> getCoordinates() {
            return coordinates;
        }
    }

    public static class Context {
        @SerializedName("id")
        private String id;

        @SerializedName("text")
        private String text;

        public String getId() {
            return id;
        }

        public String getText() {
            return text;
        }
    }
}
