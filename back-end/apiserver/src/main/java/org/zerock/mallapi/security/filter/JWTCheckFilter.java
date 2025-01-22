package org.zerock.mallapi.security.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.util.JWTUtil;

import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
@SuppressWarnings("unchecked")
public class JWTCheckFilter extends OncePerRequestFilter {

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

    // Preflight요청은 체크하지 않음 
    if(request.getMethod().equals("OPTIONS")){
      return true;
    }

    String path = request.getRequestURI();

    log.info("check uri.............." + path);

    //api/member/ 경로의 호출은 체크하지 않음 
    if(path.startsWith("/api/member/")) {
      return true;
    }

    //이미지 조회 경로는 체크하지 않는다면 
    if(path.startsWith("/api/products/view/")) {
      return true;
    }

    return false;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    log.info("------------------------JWTCheckFilter.......................");

    // Authorization 헤더 추출
    String authHeaderStr = request.getHeader("Authorization");

    try {
      // Authorization 헤더 유효성 체크
      if (authHeaderStr == null || !authHeaderStr.startsWith("Bearer ")) {
        throw new IllegalArgumentException("Invalid Authorization header");
      }

      // "Bearer " 이후의 토큰 추출
      String accessToken = authHeaderStr.substring(7);

      // 토큰 유효성 검증
      Map<String, Object> claims = JWTUtil.validateToken(accessToken);

      log.info("JWT claims: " + claims);

      // 클레임에서 사용자 정보 추출
      String email = (String) claims.get("email");
      String pw = (String) claims.get("pw");
      String nickname = (String) claims.get("nickname");
      Boolean social = (Boolean) claims.get("social");
      List<String> roleNames = (List<String>) claims.get("roleNames");

      // MemberDTO 객체 생성
      MemberDTO memberDTO = new MemberDTO(email, pw, nickname, social.booleanValue(), roleNames);

      log.info("-----------------------------------");
      log.info(memberDTO);
      log.info(memberDTO.getAuthorities());

      // 인증 정보 생성 및 SecurityContextHolder 설정
      UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(memberDTO, pw, memberDTO.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(authenticationToken);

      // 다음 필터로 요청 전달
      filterChain.doFilter(request, response);

    } catch (Exception e) {
      log.error("JWT Check Error..............");
      log.error(e.getMessage());

      // 에러 응답 작성
      Gson gson = new Gson();
      String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

      response.setContentType("application/json");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
      PrintWriter printWriter = response.getWriter();
      printWriter.println(msg);
      printWriter.close();
    }
  }


}
