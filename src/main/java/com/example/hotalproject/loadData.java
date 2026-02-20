package com.example.hotalproject;

import com.example.hotalproject.HotelCatalog.hotel.Hotel;
import com.example.hotalproject.HotelCatalog.hotel.HotelRepository;
import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
@Configuration
public class loadData {
    private static final Logger log = LoggerFactory.getLogger(loadData.class);
    @Bean
    CommandLineRunner seedData(HotelRepository hotelRepository,
                               RoomTypeRepository roomTypeRepository) {
        return args -> {

            if (hotelRepository.count() > 0) return;

            Random random = new Random();

            String[] cities = {"Jerusalem", "Hebron", "Ramallah", "Bethlehem", "Nablus"};
            String[] hotelTypes = {"Grand", "Royal", "Plaza", "Resort", "Boutique"};
            String[] roomNames = {"Single", "Double", "Suite", "Deluxe", "Family"};

            for (int i = 1; i <= 10; i++) {

                String city = cities[random.nextInt(cities.length)];
                String type = hotelTypes[random.nextInt(hotelTypes.length)];

                // إنشاء Hotel
                Hotel hotel = Hotel.builder()
                        .name(city + " " + type + " Hotel")
                        .city(city)
                        .address("Street " + i + ", " + city)
                        .description("Comfortable stay in " + city)
                        .managerEmail("manager" + i + "@gmail.com")
                        .build();

                hotelRepository.save(hotel);

                // إنشاء RoomTypes لكل فندق
                for (int j = 0; j < 3; j++) {

                    String roomName = roomNames[random.nextInt(roomNames.length)];

                    RoomType roomType = RoomType.builder()
                            .hotel(hotel) // الربط المهم 🔥
                            .name(roomName)
                            .capacity(1 + random.nextInt(4))
                            .basePrice(BigDecimal.valueOf(50 + random.nextInt(200)))
                            .amenities("WiFi, TV, AC")
                            .totalRooms(5 + random.nextInt(20))
                            .build();

                    roomTypeRepository.save(roomType);
                }
            }
        };
    }
}
