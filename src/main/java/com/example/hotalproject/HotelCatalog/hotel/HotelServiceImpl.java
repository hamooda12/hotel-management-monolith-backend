package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeMapper;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeResponseDto;
import com.example.hotalproject.media.FileStorageService;
import com.example.hotalproject.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public HotelResponseDto createHotel(HotelRequestDto request) {
        Hotel hotel = HotelMapper.toEntity(request);
        hotel = hotelRepository.save(hotel);
        return HotelMapper.toResponse(hotel);
    }
    @Override
    @Transactional
    public HotelResponseDto updateHotel(Long id, HotelRequestDto request) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
        HotelMapper.updateEntity(hotel, request);
        hotel = hotelRepository.save(hotel);
        return HotelMapper.toResponse(hotel);
    }
    @Override
    public HotelResponseDto getHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
        List<RoomTypeResponseDto> roomTypes = roomTypeRepository.findByHotelId(id)
                .stream()
                .map(RoomTypeMapper::toResponse)
                .toList();
        return HotelMapper.toResponse(hotel, roomTypes);
    }
    @Override
    @Transactional
    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hotel", id);
        }
        hotelRepository.deleteById(id);
    }
    @Override
    public PagedResponse<HotelResponseDto> listHotels(Pageable pageable,String nameContains,String city,String description,LocalDate before,LocalDate after) {
        Specification<Hotel> spec = null;

        if (nameContains != null && !nameContains.isBlank()) {
            spec=HotelSpecifications.nameContains(nameContains);
        }
        if(city!=null && !city.isBlank()) {
            Specification<Hotel>spc=HotelSpecifications.cityContains(city);
            spec=spec==null?spc:spec.and(spc);
        }
        if(description!=null && !description.isBlank()) {
            Specification<Hotel>spc=HotelSpecifications.descriptionContains(description);
            spec=spec==null?spc:spec.and(spc);
        }
        if(after!=null||before!=null) {
            Specification<Hotel> hiredate =HotelSpecifications.CriatedDateBetween(after,before);
            spec=(spec==null)?hiredate:spec.and(hiredate);
        }
        Page<Hotel> page = hotelRepository.findAll(spec, pageable);
        List<HotelResponseDto> content = page.getContent()
                .stream()
                .map(e ->HotelMapper.toResponse(e,e.getRooms().stream().map(RoomTypeMapper::toResponse).toList()))
                .toList();
        return PagedResponse.from(page, content);


    }

    @Transactional
    public HotelResponseDto uploadHotelImage(Long hotelId, MultipartFile file) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", hotelId));
        String imagePath = fileStorageService.storeHotelImage(hotelId, file, hotel.getImageUrl());
        hotel.setImageUrl(imagePath);
        hotel = hotelRepository.save(hotel);
        return HotelMapper.toResponse(hotel);
    }
    public Optional<Hotel> getHotelByHotelId(Long hotelId) {
        return hotelRepository.findById(hotelId);
    }
}

