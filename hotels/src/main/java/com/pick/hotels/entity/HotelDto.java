package com.pick.hotels.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HotelDto {
	private String hotel_name, hotel_zip_code, hotel_basic_addr, hotel_detail_addr, 
	hotel_content, hotel_parking, hotel_ballet, hotel_fitness, hotel_Storage, 
	hotel_pool, hotel_sauna, hotel_lounge, hotel_bbq, hotel_cafe, hotel_tel,
	hotel_convenience_store, hotel_karaoke, hotel_internet, seller_id;
	private int hotel_no, hotel_longitude, hotel_latitude, hotel_star, 
	region_no;
}
