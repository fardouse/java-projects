package learn.house.domain;

import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class Result<T> {
    private final List<String> messages = new ArrayList<>();
    private T payload;

    public void addErrorMessage(String message) {
        messages.add(message);
    }

    public boolean isSuccess() {
        return messages.isEmpty();
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
