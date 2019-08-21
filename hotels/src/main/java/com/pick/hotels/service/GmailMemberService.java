package com.pick.hotels.service;

import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.pick.hotels.entity.CertDto;
import com.pick.hotels.entity.EmailCertDto;
import com.pick.hotels.entity.MemberDto;
import com.pick.hotels.entity.SellerDto;
import com.pick.hotels.repository.CertDao;
import com.pick.hotels.repository.EmailCertDao;

@Service
public class GmailMemberService implements EmailService{
	
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	private RandomStringService randomStringService;
	
	@Autowired
	private CertDao certDao;
	
	@Autowired
	private EmailCertDao emailcertDao;

	@Override
	public void find_pw(MemberDto mdto) throws MessagingException {
//		인증번호 생성
		String no = randomStringService.generate(128);
		String email = mdto.getMember_email1()+"@"+mdto.getMember_email2();
		
		System.out.println(email);
		System.out.println(no);
		CertDto cdto = CertDto.builder().cert_who(mdto.getMember_no()).cert_no(no).build();
		System.out.println(cdto);
		certDao.insert(cdto);
		
		MimeMessage mail = sender.createMimeMessage();
		MimeMessageHelper helper = 
				new MimeMessageHelper(mail, false, "UTF-8");
		
		helper.setFrom("HOTEL");
		helper.setTo(email);
		helper.setSubject("[] 비밀번호 변경 메일 입니다");
		String address = ServletUriComponentsBuilder
													.fromCurrentContextPath()
													.port(8080)
													.path("/member/new_pw")
													.queryParam("member_no", mdto.getMember_no())
													.queryParam("no", no)
													.toUriString();
		helper.setText("<h3><a href = '"+address+"'>이곳을 눌러 인증을 완료하세요</a></h3>", true);
		sender.send(mail);
	}

	@Override
	public void sendCertNo(SellerDto sdto) throws MessagingException {
		Random r = new Random();
		int emailcert = r.nextInt(6)+1;
		emailcertDao.insert(String.valueOf(emailcert));
		String email = sdto.getSeller_email_id()+"@"+sdto.getSeller_email_addr();
		MimeMessage mail = sender.createMimeMessage();
		MimeMessageHelper helper = 
				new MimeMessageHelper(mail, false, "UTF-8");
		
		helper.setFrom("HOTEL");
		helper.setTo(email);
		helper.setSubject("인증번호 메일 입니다.");
		helper.setText("귀하의 인증번호는 "+emailcert+" 입니다");
	}
	
	
	
}
