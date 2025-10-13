package org.project.weatherforecastservice.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class TimeConverterTest {

    @Test
    void testToCheckUTCCompatibility() {

        LocalDateTime response = TimeConverter.time(Integer.toUnsignedLong(1760368184), 0);
        assertEquals(15, response.getHour());
    }

    @Test
    void testToCheckUTCPlusOneCompatibility() {

        LocalDateTime response = TimeConverter.time(Integer.toUnsignedLong(1760368184), 3600);
        assertEquals(16, response.getHour());
    }

    @Test
    void testShouldThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> TimeConverter.time(Integer.toUnsignedLong(1760368184), -70000));
    }
}