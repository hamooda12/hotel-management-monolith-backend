package com.example.hotalproject;

import com.example.hotalproject.HotelCatalog.hotel.Hotel;
import com.example.hotalproject.HotelCatalog.hotel.HotelRepository;
import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import com.example.hotalproject.security.AppUser;
import com.example.hotalproject.security.AppUserRepository;
import com.example.hotalproject.security.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Random;

@Configuration
public class LoadData {

    @Bean
    CommandLineRunner seedData(
            HotelRepository hotelRepository,
            RoomTypeRepository roomTypeRepository,
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder
    ) {
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
            }

            if (hotelRepository.count() == 0) {
                Random random = new Random();

                String[] roomNames = {"Single", "Double", "Suite", "Deluxe", "Family"};

                String[][] hotels = {
                        {"Ramallah Grand Hotel", "Ramallah", "Al-Irsal Street, Ramallah", "Modern luxury stay in the heart of Ramallah.", "https://images.unsplash.com/photo-1566073771259-6a8506099945"},
                        {"Jerusalem Royal Hotel", "Jerusalem", "Salah Al-Din Street, Jerusalem", "Elegant hotel close to historic Jerusalem landmarks.", "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa"},
                        {"Hebron Plaza Hotel", "Hebron", "Ein Sarah Street, Hebron", "Comfortable stay with warm local hospitality.", "https://images.unsplash.com/photo-1564501049412-61c2a3083791"},
                        {"Bethlehem Boutique Hotel", "Bethlehem", "Manger Street, Bethlehem", "Boutique experience near Bethlehem city center.", "https://images.unsplash.com/photo-1571896349842-33c89424de2d"},
                        {"Nablus Resort Hotel", "Nablus", "Rafidia Street, Nablus", "Relaxing hotel surrounded by Nablus mountain views.", "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4"},

                        {"Jericho Oasis Hotel", "Jericho", "City Center, Jericho", "Sunny resort stay near Jericho attractions.", "https://images.unsplash.com/photo-1582719508461-905c673771fd"},
                        {"Tulkarm Garden Hotel", "Tulkarm", "Main Street, Tulkarm", "Peaceful hotel with garden-style comfort.", "https://images.unsplash.com/photo-1561501878-aabd62634533"},
                        {"Jenin Palace Hotel", "Jenin", "Cinema Street, Jenin", "Simple and elegant stay for business and travel.", "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb"},
                        {"Qalqilya View Hotel", "Qalqilya", "Downtown, Qalqilya", "City hotel with clean rooms and friendly service.", "https://images.unsplash.com/photo-1559599238-308793637427"},
                        {"Gaza Sea Hotel", "Gaza", "Beach Road, Gaza", "Seaside-inspired hotel experience.", "https://images.unsplash.com/photo-1455587734955-081b22074882"},

                        {"Ramallah City Inn", "Ramallah", "Al-Masyoun, Ramallah", "Premium city stay for short and long visits.", "https://images.unsplash.com/photo-1445019980597-93fa8acb246c"},
                        {"Jerusalem Heritage Hotel", "Jerusalem", "Old City Road, Jerusalem", "Classic hotel with heritage-inspired design.", "https://images.unsplash.com/photo-1563911302283-d2bc129e7570"},
                        {"Hebron Hills Hotel", "Hebron", "University Street, Hebron", "Quiet stay with spacious rooms.", "https://images.unsplash.com/photo-1535827841776-24afc1e255ac"},
                        {"Bethlehem Star Hotel", "Bethlehem", "Star Street, Bethlehem", "Warm stay with beautiful city atmosphere.", "https://images.unsplash.com/photo-1590490360182-c33d57733427"},
                        {"Nablus Mountain Hotel", "Nablus", "Mountain Road, Nablus", "Comfortable rooms with mountain scenery.", "https://images.unsplash.com/photo-1540541338287-41700207dee6"},

                        {"Jericho Palm Resort", "Jericho", "Palm Street, Jericho", "Relaxing resort with sunny outdoor vibes.", "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9"},
                        {"Tulkarm Elite Hotel", "Tulkarm", "Al-Quds Street, Tulkarm", "Clean modern stay with premium service.", "https://images.unsplash.com/photo-1551632436-cbf8dd35adfa"},
                        {"Jenin Sky Hotel", "Jenin", "Downtown, Jenin", "Urban hotel for family and business stays.", "https://images.unsplash.com/photo-1568084680786-a84f91d1153c"},
                        {"Qalqilya Comfort Hotel", "Qalqilya", "Main Circle, Qalqilya", "Comfortable rooms at affordable prices.", "https://images.unsplash.com/photo-1602002418082-a4443e081dd1"},
                        {"Gaza Palm Hotel", "Gaza", "Al-Rimal, Gaza", "Elegant stay with relaxing atmosphere.", "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"}
                };

                for (int i = 0; i < hotels.length; i++) {
                    Hotel hotel = Hotel.builder()
                            .name(hotels[i][0])
                            .city(hotels[i][1])
                            .address(hotels[i][2])
                            .description(hotels[i][3])
                            .imageUrl(hotels[i][4])
                            .managerEmail("manager1@gmail.com")
                            .build();

                    hotelRepository.save(hotel);
                    seedRoomTypes(roomTypeRepository, random, roomNames, hotel);
                }
            }
        };
    }

    private void seedRoomTypes(
            RoomTypeRepository roomTypeRepository,
            Random random,
            String[] roomNames,
            Hotel hotel
    ) {
        String[] roomImages = {
                "https://images.unsplash.com/photo-1631049307264-da0ec9d70304",
                "https://images.unsplash.com/photo-1611892440504-42a792e24d32",
                "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf",
                "https://images.unsplash.com/photo-1586023492125-27b2c045efd7",
                "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2"
        };

        for (int j = 0; j < 3; j++) {
            String roomName = roomNames[random.nextInt(roomNames.length)];

            RoomType roomType = RoomType.builder()
                    .hotel(hotel)
                    .name(roomName)
                    .capacity(1 + random.nextInt(4))
                    .basePrice(BigDecimal.valueOf(50 + random.nextInt(200)))
                    .amenities("WiFi, TV, AC")
                    .totalRooms(5 + random.nextInt(20))
                    .imageUrl(roomImages[random.nextInt(roomImages.length)])
                    .build();

            roomTypeRepository.save(roomType);
        }
    }
}