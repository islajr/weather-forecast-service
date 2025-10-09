package org.project.weatherforecastservice.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeConverter {

    public static LocalDateTime time(Long dt, int tZ) {
        Instant instant = Instant.ofEpochSecond(dt);
        ZoneId zoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC);

        return LocalDateTime.ofInstant(instant, zoneId);
    }
}
