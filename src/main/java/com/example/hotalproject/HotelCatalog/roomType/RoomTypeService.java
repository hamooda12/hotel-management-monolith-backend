package com.example.hotalproject.HotelCatalog.roomType;

import com.example.hotalproject.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoomTypeService {
        RoomTypeResponseDto createRoomType(RoomTypeRequestDto request);
        RoomTypeResponseDto updateRoomType(Long id, RoomTypeRequestDto request);
        List<RoomTypeResponseDto> getRoomTypesByHotel(Long hotelId);
        void deleteRoomType(Long id);
        PagedResponse<RoomTypeResponseDto> listRoomType(Pageable pageable, String nameContains, String amenities, Integer mincapacity, Integer maxcapacity, Integer mintotalrooms, Integer maxtotalrooms, Integer minsalary, Integer maxsalary);
        Optional<RoomType> getRoomType(Long id);
}
