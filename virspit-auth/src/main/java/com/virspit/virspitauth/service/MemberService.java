package com.virspit.virspitauth.service;


import com.virspit.virspitauth.dto.model.Member;
import com.virspit.virspitauth.dto.request.InitPwdRequestDto;
import com.virspit.virspitauth.dto.request.MemberChangePwdRequestDto;
import com.virspit.virspitauth.dto.request.MemberSignInRequestDto;
import com.virspit.virspitauth.dto.request.MemberSignUpRequestDto;
import com.virspit.virspitauth.dto.response.MemberSignInResponseDto;
import com.virspit.virspitauth.dto.response.MemberInfoResponseDto;
import com.virspit.virspitauth.error.ErrorCode;
import com.virspit.virspitauth.error.exception.InvalidValueException;
import com.virspit.virspitauth.feign.MemberServiceFeignClient;
import com.virspit.virspitauth.jwt.JwtGenerator;
import feign.FeignException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final JwtUserDetailsService jwtUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final RedisTemplate<String, Integer> verifyRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final JavaMailSenderImpl javaMailSender;
    private final MemberServiceFeignClient memberServiceFeignClient;

    @Value("${my.ip}")
    private String myIp;

    public MemberInfoResponseDto register(MemberSignUpRequestDto memberSignUpRequestDto) {
        String pwd = memberSignUpRequestDto.getPassword();
        memberSignUpRequestDto.setPassword(passwordEncoder.encode(pwd));


        return memberServiceFeignClient.save(memberSignUpRequestDto);
    }


    public MemberSignInResponseDto login(MemberSignInRequestDto memberSignInRequestDto) throws Exception {
        log.info("login ?????? email : " + memberSignInRequestDto.getEmail());
        final String userEmail = memberSignInRequestDto.getEmail();

        //??????????????? ??????
        if (stringRedisTemplate.opsForValue().get("email-" + userEmail) != null) {
            throw new InvalidValueException(userEmail, ErrorCode.BLACKLIST_MEMBER);
        }


        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userEmail, memberSignInRequestDto.getPassword()));
        log.info("?????? ??????");

        Member member = memberServiceFeignClient.findByEmail(userEmail);
        log.info("login member : " + member.toString());

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userEmail);
        final String accessToken = jwtGenerator.generateAccessToken(userDetails);
        final String refreshToken = jwtGenerator.generateRefreshToken(userEmail);


        //generate Token and save in redis
        stringRedisTemplate.opsForValue().set("refresh-" + userEmail, refreshToken);

        log.info("login ?????? email : " + userEmail);
        return new MemberSignInResponseDto(MemberInfoResponseDto.of(member), accessToken, refreshToken);
    }

    public String verifyUserEmail(String userEmail) throws Exception {

        if (memberServiceFeignClient.checkByEmail(userEmail) == false) {
            log.warn("?????? ????????? ????????? ?????????.");
            throw new InvalidValueException(ErrorCode.EMAIL_ALREADY_EXIST);
        }

        log.info("verify email ??????");
        int rand = new Random().nextInt(999999);
        verifyRedisTemplate.opsForValue().set("verify-" + userEmail, rand);

        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
        message.setSubject("[????????????] VIRSPIT ????????? ??????");

        String content =
                "VIRSPIT??? ?????????????????? ???????????????." +
                        "<br><br>" +
                        "?????? ????????? " + rand + "?????????." +
                        "<br>" +
                        "?????? ??????????????? ???????????? ???????????? ???????????? ?????????.";

        message.setText(content, "UTF-8", "html");
        javaMailSender.send(message);

        verifyRedisTemplate.expire("verify-" + userEmail, 10 * 24 * 1000, TimeUnit.MILLISECONDS);
        // expire ??????

        return "????????? ?????? ?????? ??????";
    }


    public Boolean verifyNumber(String userEmail, Integer number) throws Exception {
        if (verifyRedisTemplate.opsForValue().get("verify-" + userEmail) != null) {
            int verifiedNumber = verifyRedisTemplate.opsForValue().get("verify-" + userEmail);
            if (verifiedNumber != number) {
                throw new Exception("??????????????? ???????????????.");
            }
            verifyRedisTemplate.delete("verify-" + userEmail);
            return true;
        } else {
            throw new Exception("????????? ????????? ???????????? ????????????.");
        }

    }

    public Boolean findPasssword(String userEmail) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
        message.setSubject("[????????????] VIRSPIT ???????????? ?????????");

        int rand = new Random().nextInt(999999);
        String formatted = String.format("%06d", rand);
        String hash = getSHA512Token(userEmail, formatted);
        String content =
                "VIRSPIT ???????????? ?????????" +
                        "<br><br>" +
                        "????????? ??????????????? virspit!23$ ?????????." +
                        "<br>" +
                        "???????????? ???????????? ?????? ????????? ???????????????." +
                        "<br>" +
                        "<a href='http://" + myIp + ":8083" + "/auth/findpwd/res?useremail=" + userEmail + "&key=" + hash + "'>" +
                        "???????????? ????????????</a></p>" +
                        "<br>" +
                        "????????? ????????? ??? ??????????????? ??????????????????.!";

        stringRedisTemplate.opsForValue().set("changepw-" + userEmail, hash);
        message.setText(content, "UTF-8", "html");
        javaMailSender.send(message);

        return true;
    }

    public String getSHA512Token(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public Boolean initPassword(String userEmail, String hash) throws Exception {
        log.info("changePassword ??????");
        if (stringRedisTemplate.opsForValue().get("changepw-" + userEmail).equals(hash)) {
            stringRedisTemplate.delete("changepw-" + userEmail);

            String initPwd = passwordEncoder.encode("virspit!23$");
            InitPwdRequestDto initPwdRequestDto = new InitPwdRequestDto(userEmail, initPwd);

            memberServiceFeignClient.initPwd(initPwdRequestDto);

            return true;
        } else {
            throw new Exception("??????");
        }
    }

    public Boolean changePassword(MemberChangePwdRequestDto memberChangePwdRequestDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberChangePwdRequestDto.getEmail(), memberChangePwdRequestDto.getBeforePwd()));

        String password = passwordEncoder.encode(memberChangePwdRequestDto.getAfterPwd());
        InitPwdRequestDto initPwdRequestDto = new InitPwdRequestDto(memberChangePwdRequestDto.getEmail(), password);
        memberServiceFeignClient.initPwd(initPwdRequestDto);
        return true;
    }

    public String logout(String accessToken) {
        String memberName = null;

        try {
            memberName = jwtGenerator.getUsernameFromToken(accessToken);
        } catch (ExpiredJwtException e) {
            memberName = e.getClaims().getSubject();
            log.info("already logout : " + memberName);
        }

        stringRedisTemplate.delete("refresh-" + memberName);
        stringRedisTemplate.opsForValue().set(accessToken, "true");
        stringRedisTemplate.expire(accessToken, 10 * 6 * 1000, TimeUnit.MILLISECONDS);

        return "logout success";
    }

}
