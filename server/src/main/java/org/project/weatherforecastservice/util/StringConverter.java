package org.project.weatherforecastservice.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringConverter {

    public static String capitalizeString(String input) {
        
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);


    }
}
