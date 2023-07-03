package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.storage.feed.Event;
import ru.yandex.practicum.filmorate.storage.feed.Operation;

@Data
@AllArgsConstructor
public class Feed {

    Long timestamp;

    Long userId;

    Event eventType;

    Operation operation;

    Long eventId;

    Long entityId;
}
