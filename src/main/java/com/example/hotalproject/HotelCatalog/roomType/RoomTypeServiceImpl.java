package com.example.hotalproject.HotelCatalog.roomType;
import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.hotel.Hotel;
import com.example.hotalproject.HotelCatalog.hotel.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomTypeServiceImpl {

    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeMapper roomTypeMapper;

    @Transactional
    public RoomTypeResponseDto createRoomType(RoomTypeRequestDto request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getHotelId()));
        RoomType roomType = roomTypeMapper.toEntity(request, hotel);
        roomType = roomTypeRepository.save(roomType);
        return roomTypeMapper.toResponse(roomType);
    }

    @Transactional
    public RoomTypeResponseDto updateRoomType(Long id, RoomTypeRequestDto request) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));
        roomTypeMapper.updateEntity(roomType, request);
        roomType = roomTypeRepository.save(roomType);
        return roomTypeMapper.toResponse(roomType);
    }

    public RoomTypeResponseDto getRoomType(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));
        return roomTypeMapper.toResponse(roomType);
    }

    public List<RoomTypeResponseDto> getRoomTypesByHotel(Long hotelId) {
        return roomTypeRepository.findByHotelId(hotelId)
                .stream()
                .map(roomTypeMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteRoomType(Long id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("RoomType", id);
        }
        roomTypeRepository.deleteById(id);
    }
}

