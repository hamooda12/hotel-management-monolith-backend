package com.example.hotalproject.HotelCatalog.roomType;
import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.hotel.*;
import com.example.hotalproject.media.FileStorageService;
import com.example.hotalproject.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final FileStorageService fileStorageService;

    public RoomTypeResponseDto createRoomType(Long hotelId, RoomTypeRequestDto request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

        RoomType roomType = RoomType.builder()
                .hotel(hotel)
                .name(request.getName())
                .capacity(request.getCapacity())
                .basePrice(request.getBasePrice())
                .amenities(request.getAmenities())
                .totalRooms(request.getTotalRooms())
                .build();

        roomTypeRepository.save(roomType);
        return RoomTypeMapper.toResponse(roomType);
    }
    @Override
    @Transactional
    public RoomTypeResponseDto updateRoomType(Long id, RoomTypeRequestDto request) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));
        RoomTypeMapper.updateEntity(roomType, request);
        roomType = roomTypeRepository.save(roomType);
        return RoomTypeMapper.toResponse(roomType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> getRoomTypesByHotel(Long hotelId) {
        return roomTypeRepository.findByHotelId(hotelId)
                .stream()
                .map(RoomTypeMapper::toResponse)
                .toList();
    }
    @Override
    @Transactional
    public void deleteRoomType(Long id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("RoomType", id);
        }
        roomTypeRepository.deleteById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RoomTypeResponseDto> listRoomType(Pageable pageable, String nameContains, String amenities, Integer mincapacity, Integer maxcapacity, Integer mintotalrooms, Integer maxtotalrooms, BigDecimal minsalary, BigDecimal maxsalary) {
        Specification<RoomType> spec = null;

        if (nameContains != null && !nameContains.isBlank()) {
            spec= RoomTypeSpecifications.nameContains(nameContains);
        }
        if(amenities!=null && !amenities.isBlank()) {
            Specification<RoomType>spc=RoomTypeSpecifications.amenitiesContains(amenities);
            spec=spec==null?spc:spec.and(spc);
        }
        if(mincapacity!=null || maxcapacity!=null) {
            Specification<RoomType> capacityBetweenSpec = RoomTypeSpecifications.capacityBetween(mincapacity,maxcapacity);
            spec = (spec == null) ? capacityBetweenSpec : spec.and(capacityBetweenSpec);
        }
        if(mintotalrooms!=null || maxtotalrooms!=null) {
            Specification<RoomType> totalroomBetweenSpec = RoomTypeSpecifications.totalroomsBetween(mintotalrooms,maxtotalrooms);
            spec = (spec == null) ? totalroomBetweenSpec : spec.and(totalroomBetweenSpec);
        }
        if(minsalary!=null || maxsalary!=null) {
            Specification<RoomType> salaryBetweenSpec = RoomTypeSpecifications.SalaryBetween(minsalary,maxsalary);
            spec = (spec == null) ? salaryBetweenSpec : spec.and(salaryBetweenSpec);
        }

        Page<RoomType> page = roomTypeRepository.findAll(spec, pageable);
        List<RoomTypeResponseDto> content = page.getContent()
                .stream()
                .map(RoomTypeMapper::toResponse)
                .toList();
        return PagedResponse.from(page, content);


    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomType> getRoomType(Long id) {
        return roomTypeRepository.findById(id);
    }

    @Transactional
    public RoomTypeResponseDto uploadRoomTypeImage(Long roomTypeId, MultipartFile file) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", roomTypeId));
        String imagePath = fileStorageService.storeRoomTypeImage(roomTypeId, file, roomType.getImageUrl());
        roomType.setImageUrl(imagePath);
        roomType = roomTypeRepository.save(roomType);
        return RoomTypeMapper.toResponse(roomType);
    }
}

