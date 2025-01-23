package org.zerock.mallapi.service;

import java.util.LinkedHashMap;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.domain.MemberRole;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.dto.MemberModifyDTO;
import org.zerock.mallapi.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
 
 @Service
 @RequiredArgsConstructor
 @Log4j2
 @SuppressWarnings("unchecked")
 public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;

  @Override
  public MemberDTO getKakaoMember(String accessToken) {

    String email = getEmailFromKakaoAccessToken(accessToken);
    
    log.info("email: " + email );
    
    Optional<Member> result = memberRepository.findById(email);
    
    if(result.isPresent()){
      MemberDTO memberDTO = entityToDTO(result.get());
    return memberDTO;
    }

    //닉네임은 '소셜회원'으로
    //패스워드는 임의로 생성  
    Member socialMember = makeSocialMember(email);
    
    memberRepository.save(socialMember);
    
    MemberDTO memberDTO = entityToDTO(socialMember);
    
    return memberDTO;
  
  }

  @Override
  public void modifyMember(MemberModifyDTO memberModifyDTO) {

    Optional<Member> result = memberRepository.findById(memberModifyDTO.getEmail());
    Member member = result.orElseThrow();
  
    member.changePw(passwordEncoder.encode(memberModifyDTO.getPw()));
    member.changeSocial(false);
    member.changeNickname(memberModifyDTO.getNickname());
    memberRepository.save(member);
  }

  private Member makeSocialMember(String email) {
  
    String tempPassword = makeTempPassword();
    log.info("tempPassword: " + tempPassword);
    String nickname = "소셜회원";
    Member member = Member.builder()
    .email(email)
    .pw(passwordEncoder.encode(tempPassword))
    .nickname(nickname)
    .social(true)
    .build();
    member.addRole(MemberRole.USER);
    return member;
  }

  private String makeTempPassword() {
    StringBuffer buffer = new StringBuffer();
  
    for(int i = 0;  i < 10; i++){
      buffer.append(  (char) ( (int)(Math.random()*55) + 65  ));
    }
    return buffer.toString();
  }


  private String getEmailFromKakaoAccessToken(String accessToken) {

      String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

      if (accessToken == null) {
          throw new RuntimeException("Access Token is null");
      }

      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer " + accessToken);
      headers.add("Content-Type", "application/x-www-form-urlencoded");
      HttpEntity<String> entity = new HttpEntity<>(headers);
      UriComponents uriBuilder = UriComponentsBuilder.
              fromUriString(kakaoGetUserURL).build();

      ResponseEntity<LinkedHashMap> response = restTemplate.exchange(
              uriBuilder.toString(),
              HttpMethod.GET,
              entity,
              LinkedHashMap.class);
      log.info(response);

      LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();
      // 1. bodyMap에서 kakao_account를 가져옴
      //
      LinkedHashMap<String, Object> kakaoAccount = bodyMap.get("kakao_account");

      // 2. kakaoAccount에서 profile의 데이터를 가져옴
      LinkedHashMap<String, Object> profile = (LinkedHashMap<String, Object>) kakaoAccount.get("profile");

      // 3. profile에서 nickname을 가져옴
      String nickname = (String) profile.get("nickname");

      log.info("nickname" + nickname);
    return nickname;
  }
  }