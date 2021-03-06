package com.pick.hotels.controller;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pick.hotels.entity.AttractionDto;
import com.pick.hotels.entity.AttractionFileDto;
import com.pick.hotels.entity.AttractionListVO;
import com.pick.hotels.entity.CouponDto;
import com.pick.hotels.entity.EmailCertDto;
import com.pick.hotels.entity.MemberCountVO;
import com.pick.hotels.entity.MemberDto;
import com.pick.hotels.entity.PartnerDto;
import com.pick.hotels.entity.PartnerListVO;
import com.pick.hotels.entity.ReserveTotalVO;
import com.pick.hotels.entity.RestaurantDto;
import com.pick.hotels.entity.RestaurantFileDto;
import com.pick.hotels.entity.RestaurantListVO;
import com.pick.hotels.entity.ReviewDto;
import com.pick.hotels.entity.ReviewVO;
import com.pick.hotels.entity.SellerCountVO;
import com.pick.hotels.entity.SellerDto;
import com.pick.hotels.repository.AttractionDao;
import com.pick.hotels.repository.AttractionFileDao;
import com.pick.hotels.repository.CouponDao;
import com.pick.hotels.repository.EmailCertDao;
import com.pick.hotels.repository.MemberDao;
import com.pick.hotels.repository.PartnerDao;
import com.pick.hotels.repository.PartnerFileDao;
import com.pick.hotels.repository.ReserveDao;
import com.pick.hotels.repository.RestaurantDao;
import com.pick.hotels.repository.RestaurantFileDao;
import com.pick.hotels.repository.ReviewDao;
import com.pick.hotels.repository.SellerDao;
import com.pick.hotels.service.EmailService;
import com.pick.hotels.service.FileService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AttractionDao attractionDao;
	
	@Autowired
	private AttractionFileDao attractionFileDao;
	
	@Autowired
	private RestaurantDao restaurantDao;
	
	@Autowired
	private RestaurantFileDao restaurantFileDao;
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private SellerDao sellerDao;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private EmailCertDao emailcertDao;
	
	@Autowired
	private CouponDao couponDao;

	@Autowired
	private PartnerDao partnerDao;
	
	@Autowired
	private PartnerFileDao partnerFileDao;
	
	@Autowired
	private ReserveDao reserveDao;

	@Autowired
	private ReviewDao reviewDao;
	
//------------------------------------------------------------------------------------
//	관리자 메인 페이지
//------------------------------------------------------------------------------------
	
//	전체 관리 페이지("main")
	@GetMapping("/main")
	public String main(Model model, 
						@ModelAttribute MemberDto memberDto,
						@ModelAttribute SellerDto sellerDto,
						@ModelAttribute PartnerDto partnerDto) {
		
		int member_total_count = memberDao.total_count();
		int recent_member_count = memberDao.recent_count();
		
		List<MemberCountVO> member_monthly_count = memberDao.monthly_count();

		int seller_total_count = sellerDao.total_count();
		int recent_seller_count = sellerDao.recent_count();
		
		List<SellerCountVO> seller_monthly_count = sellerDao.monthly_count();
		
		List<ReserveTotalVO> reserve_total = reserveDao.getTotal();
		
		int waiting_partner_count = partnerDao.waiting_count();
		int recent_complete_partner_count = partnerDao.recent_complete_count();
		int recent_refuse_partner_count = partnerDao.recent_refuse_count();
		
		int available_coupon_count = couponDao.available_coupont_count();
		int recent_take_coupon_count = couponDao.recent_take_coupon_count();
		int recent_used_coupon_count = couponDao.recent_used_coupon_count();
		
		model.addAttribute("member_total_count", member_total_count);
		model.addAttribute("recent_member_count", recent_member_count);
		model.addAttribute("member_monthly_count", member_monthly_count);
		
		model.addAttribute("seller_total_count", seller_total_count);
		model.addAttribute("recent_seller_count", recent_seller_count);
		model.addAttribute("seller_monthly_count", seller_monthly_count);
		
		model.addAttribute("reserve_total", reserve_total);

		model.addAttribute("waiting_partner_count", waiting_partner_count);
		model.addAttribute("recent_complete_partner_count", recent_complete_partner_count);
		model.addAttribute("recent_refuse_partner_count", recent_refuse_partner_count);
		
		model.addAttribute("available_coupon_count", available_coupon_count);
		model.addAttribute("recent_take_coupon_count", recent_take_coupon_count);
		model.addAttribute("recent_used_coupon_count", recent_used_coupon_count);
		
		return "admin/main";
	}
	
	
//------------------------------------------------------------------------------------
//	관광지
//------------------------------------------------------------------------------------	
	
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
						
			AttractionFileDto afdto = AttractionFileDto.builder()
														.attraction_no(no)
														.build();
			
			afdto =  fileService.attraction_save(file3, afdto);
			
			attractionFileDao.regist(afdto);
		}
		
		model.addAttribute("no", no);
		
		return "redirect:detail";
	}
	
	
	
//	관광지 삭제 처리시 비밀번호 확인
	@PostMapping("/check_pw_attraction")
	public void check_pw_attraction(HttpSession session,@RequestParam String pw, HttpServletResponse resp) throws IOException {

		String member_id = (String) session.getAttribute("ok");
		
		MemberDto memberDto = memberDao.get(member_id);
		
		//DB에 있는 비밀번호
		String member_pw = memberDto.getMember_pw();
		
		if(BCrypt.checkpw(pw, member_pw)) {
			resp.getWriter().print("Y");
		}
		else {
			resp.getWriter().print("N");
		}
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

		model.addAttribute("adto", adto);
		model.addAttribute("afdtolist", attractionFileDao.getlist(no));
		model.addAttribute("no", no);

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
		
		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 5;		//한 페이지에 보여줄 게시글 갯수
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 5;		//페이지 갯수
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
		
		List<AttractionListVO> list = attractionDao.listVO(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/attraction/list";
	}
	

//------------------------------------------------------------------------------------
//	레스토랑
//------------------------------------------------------------------------------------

//	레스토랑 추가("/restaurant/regist")
	@GetMapping("/restaurant/regist")
	public String regist_rt() {
		return "admin/restaurant/regist";
	}
	
	
	@PostMapping("/restaurant/regist")
	public String regist(
			@RequestParam MultipartFile file1,
			@RequestParam MultipartFile file2,
			@RequestParam MultipartFile file3,
			@ModelAttribute RestaurantDto restaurantDto,
			Model model
			) throws IllegalStateException, IOException {
		
//		[1] DB에서 attraction seq 에서 번호를 뽑아옴
		int no = restaurantDao.getSequenceNumber();
		restaurantDto.setRestaurant_no(no);
		
//		[2] 먼저 Attraction table에 DB 추가
		restaurantDao.regist(restaurantDto);
		
//		[3] 파일이 있는지 확인하고 파일 업로드 후 restaurant file DB에 추가
		if(!file1.isEmpty()) {
			RestaurantFileDto rfdto = RestaurantFileDto.builder()
														.restaurant_no(no)
														.build();
			
			rfdto =  fileService.restaurant_save(file1, rfdto);
			
			restaurantFileDao.regist(rfdto);
		}
		
		if(!file2.isEmpty()) {
			RestaurantFileDto rfdto = RestaurantFileDto.builder()
														.restaurant_no(no)
														.build();
			
			rfdto =  fileService.restaurant_save(file2, rfdto);
			
			restaurantFileDao.regist(rfdto);
		}
		
		if(!file3.isEmpty()) {
			RestaurantFileDto rfdto = RestaurantFileDto.builder()
														.restaurant_no(no)
														.build();
									
			rfdto =  fileService.restaurant_save(file3, rfdto);
			
			restaurantFileDao.regist(rfdto);
		}
		
		model.addAttribute("no", no);
		
		return "redirect:detail";
	}
	
	
//	레스토랑 수정("/restaurant/edit")
	@GetMapping("/restaurant/edit")
	public String edit_rt(@RequestParam int no, Model model) {
		
		model.addAttribute("rdto", restaurantDao.get(no));
		model.addAttribute("rfdtolist", restaurantFileDao.getlist(no));
		
		return "admin/restaurant/edit";
	}
	
	
	@PostMapping("/restaurant/edit")
	public String edit(@ModelAttribute RestaurantDto rdto,
						@RequestParam(required = false) MultipartFile file1,
						@RequestParam(required = false) MultipartFile file2,
						@RequestParam(required = false) MultipartFile file3,
						@RequestParam(required = false, defaultValue = "0") int restaurant_file_no1,
						@RequestParam(required = false, defaultValue = "0") int restaurant_file_no2,
						@RequestParam(required = false, defaultValue = "0") int restaurant_file_no3,
						Model model
						) throws IllegalStateException, IOException {
		
		int no = rdto.getRestaurant_no();
		
		restaurantDao.edit(rdto);
		
		if(!file1.isEmpty()) {
		
			if(restaurant_file_no1 > 0) {
				fileService.restaurant_delete(restaurant_file_no1);
			}
			
			RestaurantFileDto rfdto = RestaurantFileDto.builder()
														.restaurant_no(no)
														.build();
			
			rfdto =  fileService.restaurant_save(file1, rfdto);
			
			restaurantFileDao.regist(rfdto);
		}
		
		if(!file2.isEmpty()) {
			
			if(restaurant_file_no2 > 0) {
				fileService.restaurant_delete(restaurant_file_no2);
			}
			
			RestaurantFileDto rfdto = RestaurantFileDto.builder()
														.restaurant_no(no)
														.build();
			
			rfdto =  fileService.restaurant_save(file2, rfdto);
			
			restaurantFileDao.regist(rfdto);
		}
		
		if(!file3.isEmpty()) {
			
			if(restaurant_file_no3 > 0) {
				fileService.restaurant_delete(restaurant_file_no3);
			}
			
			RestaurantFileDto rfdto = RestaurantFileDto.builder()
														.restaurant_no(no)
														.build();
			
			rfdto =  fileService.restaurant_save(file3, rfdto);
			
			restaurantFileDao.regist(rfdto);
		}
		
		model.addAttribute("no", no);
		
		return "redirect:detail";
	}
	
	
//	레스토랑 삭제 처리시 비밀번호 확인
	@PostMapping("/check_pw_restaurant")
	public void check_pw_restaurant(HttpSession session,@RequestParam String pw, HttpServletResponse resp) throws IOException {

		String member_id = (String) session.getAttribute("ok");
		
		MemberDto memberDto = memberDao.get(member_id);
		
		//DB에 있는 비밀번호
		String member_pw = memberDto.getMember_pw();
		
		if(BCrypt.checkpw(pw, member_pw)) {
			resp.getWriter().print("Y");
		}
		else {
			resp.getWriter().print("N");
		}
	}
	
	
	
//	레스토랑 삭제("/restaurant/exit")
	@GetMapping("/restaurant/exit")
	public String exit_rt(@RequestParam int no) {
		
		restaurantDao.exit(no);
		restaurantFileDao.exit(no);
		
		return "redirect:list";
	}
	
	
//	레스토랑 상세보기("restaurant/detail")
	@GetMapping("/restaurant/detail")
	public String content_rt(@RequestParam int no, Model model) {
		
		RestaurantDto rdto = restaurantDao.get(no);
		
		model.addAttribute("rdto", rdto);
		model.addAttribute("rfdtolist", restaurantFileDao.getlist(no));

		return "admin/restaurant/detail";
	}
	
	
//	레스토랑 전체 리스트 + 검색("/restaurant/list")
	@GetMapping("/restaurant/list")
	public String list_rt(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 5;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 5;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = restaurantDao.count(type, keyword);
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
		
		List<RestaurantListVO> list = restaurantDao.listVO(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/restaurant/list";
	}
	

//------------------------------------------------------------------------------------
//	쿠폰
//------------------------------------------------------------------------------------
	
//	쿠폰 추가("/coupon/regist")
	@GetMapping("/coupon/regist")
	public String regist_couon() {
		return "admin/coupon/regist";
	}
	
	@PostMapping("/coupon/regist")
	public String regist(
			@ModelAttribute CouponDto couponDto,
			Model model
			) throws IllegalStateException, IOException {
		
		int no = couponDao.getSequenceNumber();
		couponDto.setCoupon_no(no);
		
		couponDao.regist(couponDto);
		
		model.addAttribute("no", no);
		
		return "redirect:list";
	}
	
	
//	쿠폰 상세보기("coupon/detail")
	@GetMapping("/coupon/detail")
	public String detail_coupon(@RequestParam int no, Model model) {
		
		CouponDto cdto = couponDao.get(no);
		
		model.addAttribute("cdto", cdto);

		return "admin/coupon/detail";
	}
	
	
//	쿠폰 수정(사용불가 상태)("/coupon/edit")
	@GetMapping("/coupon/edit")
	public String edit_coupon(@RequestParam int no, Model model) {
		
		model.addAttribute("cdto", couponDao.get(no));
		
		return "admin/coupon/edit";
	}
	
	@PostMapping("/coupon/edit")
	public String edit_coupon(@ModelAttribute CouponDto cdto,
						Model model
						) throws IllegalStateException, IOException {
		
		int no = cdto.getCoupon_no();
		
		couponDao.edit(cdto);
		
		model.addAttribute("no", no);
		
		return "redirect:list";
	}
	
	
//	쿠폰 전체 리스트 + 검색("/coupon/list")
	@GetMapping("/coupon/list")
	public String list_coupon(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 10;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 5;
		
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = couponDao.count(type, keyword);
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
		
		List<CouponDto> list = couponDao.list(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/coupon/list";
	}
	
	
//	사용만료 쿠폰 리스트 + 검색("/coupon/blacklist")
	@GetMapping("/coupon/blacklist")
	public String blacklist_coupon(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 10;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 5;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = couponDao.count(type, keyword);
		int pageCount = (count -1) / pagesize + 1;
		
		if(endBlock > pageCount) {
			endBlock = pageCount;
		}
		if(page > end) {
			page = 1;
		}
		
		model.addAttribute("page", page);
		model.addAttribute("startBlock", startBlock);
		model.addAttribute("endBlock", endBlock);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		
		List<CouponDto> blacklist = couponDao.blacklist(type, keyword, start, end);
		
		model.addAttribute("list", blacklist);
		
		return "admin/coupon/blacklist";
	}
	
	
//------------------------------------------------------------------------------------
//	회원 관리
//------------------------------------------------------------------------------------	
	
//	회원 정보 상세보기("/member/detail")
	@GetMapping("/member/detail")
	public String detail_member(@RequestParam int no, Model model) {
		
		MemberDto memberDto = memberDao.get(no);
		
		model.addAttribute("mdto", memberDto);
		
		return "admin/member/detail";
	}
	
	
//	회원 비밀번호 변경 이메일 발송
	@GetMapping("/member/new_pw")
	@ResponseBody
	public String new_pw_member(@RequestParam int no, HttpServletRequest request, Model model) throws MessagingException {
		
		MemberDto memberDto = memberDao.get(no);
		
		model.addAttribute("mdto", memberDto);
		String url = request.getRequestURL().toString().replace(request.getRequestURI(), "");
		emailService.find_pw(memberDto, url);
		
		return "Y";
	}
	
	
//	회원 정보 수정("/member/edit")
	@GetMapping("/member/edit")
	public String edit_member(@RequestParam int no, Model model) {
		
		MemberDto memberDto = memberDao.get(no);
		
		model.addAttribute("mdto", memberDto);
		
		return "admin/member/edit";
	}
	
	@PostMapping("/member/edit")
	public String edit_member(@ModelAttribute MemberDto memberDto, Model model) {
		
		memberDao.edit_member(memberDto);
		
		model.addAttribute("no", memberDto.getMember_no());
		
		return "redirect:detail";
	}
	
	
//	회원 탈퇴 처리시 비밀번호 확인
	@PostMapping("/check_pw_member")
	public void check_pw_member(HttpSession session,@RequestParam String pw, HttpServletResponse resp) throws IOException {

		String member_id = (String) session.getAttribute("ok");
		
		MemberDto memberDto = memberDao.get(member_id);
		
		//DB에 있는 비밀번호
		String member_pw = memberDto.getMember_pw();
		
		if(BCrypt.checkpw(pw, member_pw)) {
			resp.getWriter().print("Y");
		}
		else {
			resp.getWriter().print("N");
		}
	}
	
	
//	회원 탈퇴("/member/exit")
	@GetMapping("/member/exit")
	public String exit_member(@RequestParam int no, Model model) {
		
		MemberDto memberDto = memberDao.get(no);
		
		String member_id = memberDto.getMember_id();
		
		memberDao.delete(member_id);
		
		return "redirect:list";
	}
	
	
//	전체 회원 리스트  + 검색("/member/list")
	@GetMapping("/member/list")
	public String list_member(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 10;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 10;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = memberDao.count(type, keyword);
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
		
		List<MemberDto> list = memberDao.list(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/member/list";
	}

	
//	비밀번호 변경 메일 발송
	@GetMapping("/emailcert")
	public void emailcert(@RequestParam String member_email1, @RequestParam String member_email2, HttpServletResponse resp) throws IOException, MessagingException {
			boolean result = emailService.sendCertNo_member(member_email1,member_email2);
			if(result) {
				resp.getWriter().print("Y");
			}
			else {
				resp.getWriter().print("N");
			}
		}
	
	
//	비밀번호 변경 메일 인증 여부 체크
	@GetMapping("/email_cert_check")
	public void email_cert_check(@RequestParam String member_email_cert, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		EmailCertDto ecdto = emailcertDao.get_m(member_email_cert);
		if(ecdto==null) {
			resp.getWriter().print("N");
		}
		else {
			resp.getWriter().print("Y");
			emailcertDao.delete_m(member_email_cert);
		}
	}
	
	
//------------------------------------------------------------------------------------
//	판매자 관리
//------------------------------------------------------------------------------------
	
//	판매자 정보 상세보기("/seller/detail")
	@GetMapping("/seller/detail")
	public String detail_seller(@RequestParam int no, Model model) {
		
		SellerDto sellerDto = sellerDao.get(no);
		
		model.addAttribute("sdto", sellerDto);
		
		return "admin/seller/detail";
	}
	
	
//	판매자 비밀번호 변경 이메일 발송
	@GetMapping("/seller/new_pw")
	@ResponseBody
	public String new_pw_seller(@RequestParam int no, HttpServletRequest request,Model model) throws MessagingException {
		
		SellerDto sellerDto = sellerDao.get(no);
		model.addAttribute("sdto", sellerDto);
		String url = request.getRequestURL().toString().replace(request.getRequestURI(), "");
		emailService.find_pw(sellerDto, url);
		
		return "Y";
	}
	
	
//	판매자 정보 수정("/seller/edit")
	@GetMapping("/seller/edit")
	public String edit_seller(@RequestParam int no, Model model) {
		
		SellerDto sellerDto = sellerDao.get(no);
		
		model.addAttribute("sdto", sellerDto);
		
		return "admin/seller/edit";
	}
	
	@PostMapping("/seller/edit")
	public String edit_seller(@ModelAttribute SellerDto sellerDto, Model model) {
		
		sellerDao.edit_seller(sellerDto);
		
		model.addAttribute("no", sellerDto.getSeller_no());
		
		return "redirect:detail";
	}
	
	
//	판매자 탈퇴 처리시 비밀번호 확인
	@PostMapping("/check_pw_seller")
	public void check_pw_seller(HttpSession session,@RequestParam String pw, HttpServletResponse resp) throws IOException {

		String member_id = (String) session.getAttribute("ok");
		
		MemberDto memberDto = memberDao.get(member_id);
		
		//DB에 있는 비밀번호
		String member_pw = memberDto.getMember_pw();
		
		if(BCrypt.checkpw(pw, member_pw)) {
			resp.getWriter().print("Y");
		}
		else {
			resp.getWriter().print("N");
		}
	}
	
	
//	판매자 탈퇴("/seller/exit")
	@GetMapping("/seller/exit")
	public String exit_seller(@RequestParam int no, Model model) {
		
		SellerDto sellerDto = sellerDao.get(no);
		
		String seller_id = sellerDto.getSeller_id();
		
		sellerDao.delete(seller_id);
		
		return "redirect:list";
	}
	
	
//	일반 판매자 리스트 + 검색("/seller/list")
	@GetMapping("/seller/list")
	public String list_seller(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 10;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 10;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		
		int count = sellerDao.count(type, keyword);
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
		
		List<SellerDto> list = sellerDao.list(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/seller/list";
	}
	
	
//	블랙리스트 판매자 리스트 + 검색("/seller/blacklist")
	@GetMapping("/seller/blacklist")
	public String blacklist_seller(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 10;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 10;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = sellerDao.count_black(type, keyword);
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
		
		List<SellerDto> list = sellerDao.blacklist(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/seller/blacklist";
	}
	
	
//	비밀번호 변경 메일 발송
	@GetMapping("/emailcert_seller")
	public void emailcert_seller(@RequestParam String seller_email_id, @RequestParam String seller_email_addr, HttpServletResponse resp) throws IOException, MessagingException {
			boolean result = emailService.sendCertNo(seller_email_id, seller_email_addr);
			if(result) {
				resp.getWriter().print("Y");
			}
			else {
				resp.getWriter().print("N");
			}
		}
	
	
//	비밀번호 변경 메일 인증 여부 체크
	@GetMapping("/email_cert_check_seller")
	public void email_cert_seller_check(@RequestParam String seller_email_cert, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		EmailCertDto ecdto = emailcertDao.get(seller_email_cert);
		if(ecdto==null) {
			resp.getWriter().print("N");
		}
		else {
			resp.getWriter().print("Y");
			emailcertDao.delete(seller_email_cert);
		}
	}
	
//------------------------------------------------------------------------------------
//	제휴 관리
//------------------------------------------------------------------------------------

//	제휴 정보 수정("/partner/edit")
	@GetMapping("/partner/edit")
	public String edit_partner(@RequestParam int no, Model model) {
		
		PartnerDto partnerDto = partnerDao.get(no);
		
		model.addAttribute("pdto", partnerDto);
		
		return "admin/partner/edit";
	}
	
	@PostMapping("/partner/edit")
	public String edit_partner(@ModelAttribute PartnerDto partnerDto, Model model) {
		
		partnerDao.edit_partner(partnerDto);
		
		model.addAttribute("no", partnerDto.getPartner_no());
		
		return "redirect:detail";
	}
	
	
//	제휴 정보 상세보기("/partner/detail")
	@GetMapping("/partner/detail")
	public String detail_partner(@RequestParam int no, Model model) {
		
		PartnerDto partnerDto = partnerDao.get(no);

		String type = partnerDto.getPartner_type().equals("승인대기")?"waiting_list":partnerDto.getPartner_type().equals("승인완료")?"complete_list":"refuse_list";
		
		model.addAttribute("pdto", partnerDto);
		model.addAttribute("pfdtolist", partnerFileDao.getlist(no));
		model.addAttribute("no", no);
		model.addAttribute("link", type);
		
		return "admin/partner/detail";
	}
	
	
//	제휴 승인 대기 리스트("/partner/waiting_list")
	@GetMapping("/partner/waiting_list")
	public String waiting_list(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 5;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 5;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = partnerDao.count_waiting(type, keyword);
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
		
		List<PartnerListVO> list = partnerDao.waiting_list(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/partner/waiting_list";
	}
	
	
//	제휴 승인 완료 리스트("/partner/complete_list")
	@GetMapping("/partner/complete_list")
	public String complete_list(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 5;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 5;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = partnerDao.count_complete(type, keyword);
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
		
		List<PartnerListVO> list = partnerDao.complete_list(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/partner/complete_list";
	}
	
	
//	제휴 승인 거절 리스트("/partner/refuse_list")
	@GetMapping("/partner/refuse_list")
	public String refuse_list(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 5;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 5;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = partnerDao.count_refuse(type, keyword);
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
		
		List<PartnerListVO> list = partnerDao.refuse_list(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/partner/refuse_list";
	}
	
	
//------------------------------------------------------------------------------------
// 리뷰
//------------------------------------------------------------------------------------	

//	리뷰 리스트("/review/list")
	@GetMapping("/review/list")
	public String review_list(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 10;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 5;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = reviewDao.count_review_list(type, keyword);
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
		
		List<ReviewVO> list = reviewDao.admin_review_list(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/review/list";
	}
	
	
//	리뷰 블랙리스트("/review/list")
	@GetMapping("/review/blacklist")
	public String review_blacklist(
						@RequestParam(required = false) String type,
						@RequestParam(required = false) String keyword,
						@RequestParam(required = false, defaultValue="1") int page,
						Model model
			) {

		if(page <= 0) {
			page = 1;
		}
		
		int pagesize = 10;
		int start = pagesize * page - (pagesize -1);
		int end = pagesize * page;
		
		int blocksize = 5;
		int startBlock = (page - 1 ) / blocksize * blocksize + 1;
		int endBlock = startBlock + (blocksize -1);
		
		int count = reviewDao.count_review_blacklist(type, keyword);
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
		
		List<ReviewVO> list = reviewDao.admin_review_blacklist(type, keyword, start, end);
		
		model.addAttribute("list", list);
		
		return "admin/review/blacklist";
	}
	
	
//	리뷰 블랙리스트 처리("/review/edit")
	@GetMapping("/review/edit")
	public String review_edit(@RequestParam int no, Model model) {

		ReviewDto reviewDto = reviewDao.get(no);
		
		model.addAttribute("rdto", reviewDto);
		
		return "admin/review/edit";
	}
	
	@PostMapping("/review/edit")
	public String review_edit(@ModelAttribute ReviewDto reviewDto) {
		
		reviewDao.edit(reviewDto);
		
		return "redirect:list";
	}


}



