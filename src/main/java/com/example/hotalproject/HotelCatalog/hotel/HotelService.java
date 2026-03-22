package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface HotelService {

	HotelResponseDto createHotel(HotelRequestDto request);
	HotelResponseDto updateHotel(Long id, HotelRequestDto request);
	HotelResponseDto getHotel(Long id);
	void deleteHotel(Long id);
	PagedResponse<HotelResponseDto> listHotels(Pageable pageable, String nameContains, String city, String description, LocalDate before, LocalDate after);

}
