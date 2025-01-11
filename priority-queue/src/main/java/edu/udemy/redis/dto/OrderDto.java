package edu.udemy.redis.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.udemy.redis.util.UserClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class OrderDto {

    private final String number;
    private final UserClass userClass;
    private final LocalDateTime createdAt;

    public OrderDto(String number, UserClass userClass) {
        createdAt = LocalDateTime.now();
        this.number = createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "_" + number;
        this.userClass = userClass;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public OrderDto(@JsonProperty("number") String number,
                    @JsonProperty("userClass") UserClass userClass,
                    @JsonProperty("createdAt") LocalDateTime createdAt) {
        this.number = number;
        this.userClass = userClass;
        this.createdAt = createdAt;
    }

    public String getNumber() {
        return number;
    }

    public UserClass getUserClass() {
        return userClass;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDto orderDto = (OrderDto) o;
        return Objects.equals(number, orderDto.number)
                && userClass == orderDto.userClass
                && createdAt.equals(orderDto.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, userClass, createdAt);
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "number='" + number + '\'' +
                ", userClass=" + userClass +
                ", createdAt=" + createdAt +
                '}';
    }
}