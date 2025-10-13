package org.project.weatherforecastservice.util;

import lombok.extern.slf4j.Slf4j;

import java.time.*;

@Slf4j
public class TimeConverter {

    public static LocalDateTime time(Long dt, int tZ) {
        try {
            Instant instant = Instant.ofEpochSecond(dt);
            ZoneOffset offset = ZoneOffset.ofTotalSeconds(tZ);
            
            log.info("Converting {} into LocalDateTime", dt);
            return LocalDateTime.ofInstant(instant, offset);
        } catch (DateTimeException e) {
            throw new RuntimeException("Unable to convert " + dt + " into LocalDateTime", e);
        }

    }
}
