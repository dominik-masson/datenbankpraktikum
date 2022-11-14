package db.gruppe20.abgabe3.frontend.entity;

public record OfferRecord(String id, String Store, ProductRecord productRecord, Double price,
                          String currency, Boolean availability) {
}
