package com.example.hotalproject.AI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class HotelInformationService {

    private static final Logger LOG =
        LoggerFactory.getLogger(HotelInformationService.class);

    public String getInformationFor(String hotelName) {

        try {

            var filename = String.format(
                "classpath:/hotelData/%s.txt",
                hotelName.toLowerCase().replace(" ", "_")
            );

            return new DefaultResourceLoader()
                .getResource(filename)
                .getContentAsString(Charset.defaultCharset());

        } catch (IOException e) {

            LOG.info("No information found for hotel: " + hotelName);

            return "";
        }
    }
}