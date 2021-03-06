package com.pick.hotels.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PartnerFileDto {
	private int		partner_no, p_file_no;
	private String	p_file_name, p_file_type;
}
