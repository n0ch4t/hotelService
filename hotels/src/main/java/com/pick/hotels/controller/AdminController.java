package com.pick.hotels.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pick.hotels.entity.AttractionDto;
import com.pick.hotels.entity.AttractionFileDto;
import com.pick.hotels.repository.AttractionDao;
import com.pick.hotels.repository.AttractionFileDao;
import com.pick.hotels.service.FileService;


@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AttractionDao attractionDao;
	
	@Autowired
	private AttractionFileDao attractionFileDao;
	
	@Autowired
	private FileService fileService;

//	관광지 추가("/attraction/regist")
	@GetMapping("/attraction/regist")
	public String regist() {
		return "admin/attraction/regist";
	}
	
	@PostMapping("/attraction/regist")
	public String regist(
			@RequestParam MultipartFile file1,
			@RequestParam MultipartFile file2,
			@RequestParam MultipartFile file3,
			@ModelAttribute AttractionDto attractionDto,
			Model model
			) throws IllegalStateException, IOException {
		
//		[1] DB에서 attraction seq 에서 번호를 뽑아옴
		int no = attractionDao.getSequenceNumber();
		attractionDto.setAttraction_no(no);
		
//		[2] 먼저 Attraction table에 DB 추가
		attractionDao.regist(attractionDto);
		
//		[3] 파일이 있는지 확인하고 파일 업로드 후 attraction file DB에 추가
		if(!file1.isEmpty()) {
			AttractionFileDto afdto = AttractionFileDto.builder()
														.attraction_no(no)
														.build();
			
			afdto =  fileService.attraction_save(file1, afdto);
			
			attractionFileDao.regist(afdto);
		}
		
		if(!file2.isEmpty()) {
			AttractionFileDto afdto = AttractionFileDto.builder()
														.attraction_no(no)
														.build();
			
			afdto =  fileService.attraction_save(file2, afdto);
			
			attractionFileDao.regist(afdto);
		}
		
		if(!file3.isEmpty()) {
			AttractionFileDto afdto = AttractionFileDto.builder()
														.attraction_no(no)
														.build();
			
			afdto =  fileService.attraction_save(file3, afdto);
			
			attractionFileDao.regist(afdto);
		}
		
		model.addAttribute("no", no);
		
		return "redirect:detail";
	}
	
//	관광지 수정("/attraction/edit")
	@GetMapping("/attraction/edit")
	public String edit(@RequestParam int no, Model model) {
		
		model.addAttribute("adto", attractionDao.get(no));
		model.addAttribute("afdtolist", attractionFileDao.getlist(no));
		
		return "admin/attraction/edit";
	}
	
	@PostMapping("/attraction/edit")
	public String edit(@ModelAttribute AttractionDto adto,
						@RequestParam(required = false) MultipartFile file1,
						@RequestParam(required = false) MultipartFile file2,
						@RequestParam(required = false) MultipartFile file3,
						@RequestParam(required = false, defaultValue = "0") int attraction_file_no1,
						@RequestParam(required = false, defaultValue = "0") int attraction_file_no2,
						@RequestParam(required = false, defaultValue = "0") int attraction_file_no3,
						Model model
						) throws IllegalStateException, IOException {
		
		int no = adto.getAttraction_no();
		
		attractionDao.edit(adto);
		
//		수정을 하게되면 
//		1. 수정한 글 내용
//		2. 수정파일1, 2, 3
//		이 넘어오게 되는데 이것을 받아서 수정 처리를 한다.
//		-> 글 내용은 그냥 수정
//		->
		if(!file1.isEmpty()) {
		
			if(attraction_file_no1 > 0) {
				fileService.attraction_delete(attraction_file_no1);
			}
			
			AttractionFileDto afdto = AttractionFileDto.builder()
														.attraction_no(no)
														.build();
			
			afdto =  fileService.attraction_save(file1, afdto);
			
			attractionFileDao.regist(afdto);
		}
		
		if(!file2.isEmpty()) {
			
			if(attraction_file_no2 > 0) {
				fileService.attraction_delete(attraction_file_no2);
			}
			
			AttractionFileDto afdto = AttractionFileDto.builder()
														.attraction_no(no)
														.build();
			
			afdto =  fileService.attraction_save(file2, afdto);
			
			attractionFileDao.regist(afdto);
		}
		
		if(!file3.isEmpty()) {
			
			if(attraction_file_no3 > 0) {
				fileService.attraction_delete(attraction_file_no3);
			}
			
			fileService.attraction_delete(attraction_file_no3);
			
			AttractionFileDto afdto = AttractionFileDto.builder()
														.attraction_no(no)
														.build();
			
			afdto =  fileService.attraction_save(file3, afdto);
			
			attractionFileDao.regist(afdto);
		}
		
		model.addAttribute("no", no);
		
		return "redirect:detail";
	}
	
//	관광지 삭제("/attraction/exit")
	@GetMapping("/attraction/exit")
	public String exit(@RequestParam int no) {
		
		attractionDao.exit(no);
		attractionFileDao.exit(no);
		
		return "redirect:list";
	}
	
//	관광지 상세보기("/attraction/detail")
	@GetMapping("/attraction/detail")
	public String content(@RequestParam int no, Model model) {
		
		AttractionDto adto = attractionDao.get(no);
		AttractionFileDto afdto = attractionFileDao.get(no);
		
		model.addAttribute("adto", adto);
		model.addAttribute("afdto", afdto);
		model.addAttribute("afdtolist", attractionFileDao.getlist(no));

		return "admin/attraction/detail";
	}
	
//	관광지 전체리스트  + 검색("/attraction/list")
	@GetMapping("/attraction/list")
	public String list(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {
		int pagesize = 10;		//한 페이지에 보여줄 게시글 갯수
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 5;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = attractionDao.count(type, keyword);
		int pageCount = (count -1) / pagesize + 1;
		
		if(endBlock > pageCount) {
			endBlock = pageCount;
		}
		
		model.addAttribute("page", page);
		model.addAttribute("startBlock", startBlock);
		model.addAttribute("endBlock", endBlock);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("pageCount", pageCount);
		
		List<AttractionDto> list = attractionDao.list(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/attraction/list";
	}
	
//	레스토랑 추가("/restaurant/regist")
//	레스토랑 수정("/restaurant/edit")
//	레스토랑 삭제("/restaurant/exit")
//	레스토랑 상세보기("attraction/detail")
//	레스토랑 전체 리스트 +검색("/restaurant/list")
	
	
//	쿠폰 추가("/coupon/regist")
//	쿠폰 수정("/coupon/edit")
//	쿠폰 만료 처리(사용불가 상태)("/coupon/exit")
//	쿠폰 상세보기("/coupon/detail")
//	쿠폰 전체 리스트 + 검색("/coupon/list")
	
	
//	회원 정보 상세보기("/member/detail")
//	회원 정보 수정("/member/edit")
//	회원 탈퇴("/member/exit")
//	전체 회원 리스트  + 검색("/member/list")

	
//	판매자 정보 상세보기("/seller/detail")
//	판매자 정보 수정("/seller/edit")
//	판매자 탈퇴("/seller/exit")
//	전체 판매자 리스트 + 검색("/seller/list")
	
}