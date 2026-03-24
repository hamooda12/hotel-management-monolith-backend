package com.example.hotalproject;

import com.example.hotalproject.HotelCatalog.hotel.Hotel;
import com.example.hotalproject.HotelCatalog.hotel.HotelRepository;
import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import com.example.hotalproject.security.AppUser;
import com.example.hotalproject.security.AppUserRepository;
import com.example.hotalproject.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
@Configuration
public class loadData {
    private static final Logger log = LoggerFactory.getLogger(loadData.class);
    @Bean
    CommandLineRunner seedData(HotelRepository hotelRepository,
                               RoomTypeRepository roomTypeRepository,
                               AppUserRepository appUserRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {

            if (appUserRepository.count() == 0) {
                appUserRepository.save(AppUser.builder()
                        .email("admin@hotel.local")
                        .password(passwordEncoder.encode("Admin@123"))
                        .role(Role.ADMIN)
                        .build());
                appUserRepository.save(AppUser.builder()
                        .email("manager1@gmail.com")
                        .password(passwordEncoder.encode("Manager@123"))
                        .role(Role.MANAGER)
                        .build());
                appUserRepository.save(AppUser.builder()
                        .email("guest@hotel.local")
                        .password(passwordEncoder.encode("Guest@123"))
                        .role(Role.GUEST)
                        .build());
                log.info("Seeded demo users (admin/manager/guest).");
            }

            if (hotelRepository.count() > 0) return;

            Random random = new Random();

            String[] cities = {"Jerusalem", "Hebron", "Ramallah", "Bethlehem", "Nablus"};
            String[] hotelTypes = {"Grand", "Royal", "Plaza", "Resort", "Boutique"};
            String[] roomNames = {"Single", "Double", "Suite", "Deluxe", "Family"};

            for (int i = 1; i <= 10; i++) {

                String city = cities[random.nextInt(cities.length)];
                String type = hotelTypes[random.nextInt(hotelTypes.length)];


                Hotel hotel = Hotel.builder()
                        .name(city + " " + type + " Hotel")
                        .city(city)
                        .address("Street " + i + ", " + city)
                        .description("Comfortable stay in " + city)
                        .managerEmail("manager" + i + "@gmail.com")
                        .build();

                hotelRepository.save(hotel);


                for (int j = 0; j < 3; j++) {

                    String roomName = roomNames[random.nextInt(roomNames.length)];

                    RoomType roomType = RoomType.builder()
                            .hotel(hotel)
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
