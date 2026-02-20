package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeMapper;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceImpl {

    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final HotelMapper hotelMapper;
    private final RoomTypeMapper roomTypeMapper;

    @Transactional
    public HotelResponseDto createHotel(HotelRequestDto request) {
        Hotel hotel = hotelMapper.toEntity(request);
        hotel = hotelRepository.save(hotel);
        return hotelMapper.toResponse(hotel);
    }

    @Transactional
    public HotelResponseDto updateHotel(Long id, HotelRequestDto request) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
        hotelMapper.updateEntity(hotel, request);
        hotel = hotelRepository.save(hotel);
        return hotelMapper.toResponse(hotel);
    }

    public HotelResponseDto getHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
        List<RoomTypeResponseDto> roomTypes = roomTypeRepository.findByHotelId(id)
                .stream()
                .map(roomTypeMapper::toResponse)
                .toList();
        return hotelMapper.toResponse(hotel, roomTypes);
    }

    @Transactional
    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hotel", id);
        }
        hotelRepository.deleteById(id);
    }

    public Page<HotelResponseDto> browseHotels(String city, String nameContains,
                                            Integer minCapacity,
                                            BigDecimal minPrice, BigDecimal maxPrice,
                                            Pageable pageable) {
        return hotelRepository.findWithFilters(city, nameContains, minCapacity,
                        minPrice, maxPrice, pageable)
                .map(hotelMapper::toResponse);
    }
}

