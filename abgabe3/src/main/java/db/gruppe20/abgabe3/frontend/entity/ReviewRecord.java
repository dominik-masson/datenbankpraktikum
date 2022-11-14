package db.gruppe20.abgabe3.frontend.entity;

public record ReviewRecord(String id, CustomerRecord customerRecord, ProductRecord productRecord,
                           String description, String summary, Integer points, Integer helpful) {

}