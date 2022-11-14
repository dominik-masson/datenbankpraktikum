package db.gruppe20.abgabe3.frontend.entity;


public record ProductRecord(String id, String title, Double rating, Integer salesrank, String image) {

    public String toString(){

        return "[id:%s] %s (rating %s)".formatted(this.id, this.title, this.rating);

    }

}