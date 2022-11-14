package db.gruppe20.abgabe3.frontend.entity;


public record CustomerRecord(Integer id, String username, String accountNumber, String address) {

    public String toString() {
        return "[id:%s] %s".formatted(this.id, this.username);
    }

}