package com.pick.hotels.repository;

import java.util.List;

import com.pick.hotels.entity.H_search_vo;
import com.pick.hotels.entity.HotelDto;
import com.pick.hotels.entity.HotelListVo;
import com.pick.hotels.entity.HotelSalesVO;
import com.pick.hotels.entity.ReserverListVO;

public interface HotelDao {

	public void regist(HotelDto hotelDto);

	List<HotelDto> list(int seller_no);

	List<HotelListVo> get_h_list(H_search_vo s_vo);

	public int getSequenceNumber();

	public boolean delete(int hotel_no);

	HotelDto get(int hotel_no);
	
	HotelDto getNo(int seller_no);

	public void edit(HotelDto hotelDto);

	List<HotelDto> getNoList(int seller_no);
	
	List<HotelSalesVO> sales(int hotel_no);

	List<HotelSalesVO> monthSales(int hotel_no);

	List<HotelSalesVO> monthSalesPrice(int hotel_no);

	List<HotelSalesVO> salesPrice(List<HotelDto> list);

	public String getMonth();

	public List<ReserverListVO> getReserver(int hotel_no);

	public List<HotelSalesVO> hotelsalesPrice(int hotel_no);

}
