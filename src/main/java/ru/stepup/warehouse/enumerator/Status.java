package ru.stepup.warehouse.enumerator;

public enum Status {
    CLOSE(0), OPEN(1), RESERVED(2), DELETED(3);
    int statusId;
    private Status(int statusId) {
        this.statusId = statusId;
    }
    public int getStatusId() {
        return statusId;
    }
}
