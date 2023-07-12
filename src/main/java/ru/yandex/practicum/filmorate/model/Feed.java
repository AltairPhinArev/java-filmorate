package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.model.feedTypes.Event;
import ru.yandex.practicum.filmorate.model.feedTypes.Operation;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Feed {

    Long timestamp;

    Long userId;

    Event eventType;

    Operation operation;

    Long eventId;

    Long entityId;
}
