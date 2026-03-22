package com.example.hotalproject.HotelCatalog.roomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeSelectionDTO {

	private RoomType roomType;
	private int count;
}