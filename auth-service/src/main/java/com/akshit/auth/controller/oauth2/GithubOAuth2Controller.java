package com.akshit.auth.controller.oauth2;

import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.GithubAccessTokenRequestResponse;
import com.akshit.auth.model.GithubGetUserRequestResponse;
import com.akshit.auth.service.JwtService;
import com.akshit.auth.service.UserService;
import com.akshit.auth.service.oauth2.GithubOAuth2Service;
import com.akshit.auth.utils.Cookies;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/login/oauth2/github")
public class GithubOAuth2Controller {

    @Value("${oauth2.client.github.client-id}")
    private String githubClientId;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Value("${oauth2.client.github.redirect-uri}")
    private String githubRedirectUri;

    @Autowired
    private GithubOAuth2Service githubOAuth2Service;

    @GetMapping("")
    public ResponseEntity<Void> authorizationEndpoint(HttpServletRequest request){
        String referer = request.getHeader(HttpHeaders.REFERER);
        String state = generateState(referer);

        String location = UriComponentsBuilder
                .fromHttpUrl("https://github.com/login/oauth/authorize")
                .queryParam("client_id", githubClientId)
                .queryParam("redirect_uri", githubRedirectUri)
                .queryParam("state", state)
                .encode()
                .build()
                .toString();
        ResponseCookie stateCookie = ResponseCookie
                .from("state", state)
                .path("/")
                .build();
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, location)
                .header(HttpHeaders.SET_COOKIE, stateCookie.toString())
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callbackEndpoint(
            @RequestParam(required = false) String code,
            @RequestParam String state,
            @RequestParam(required = false) String error,
            HttpServletRequest request
    ) throws Exception
    {
        String stateCookie = Cookies.readServletCookie(request, "state");
        if(stateCookie == null || !stateCookie.equals(state))
            throw new Exception("Malformed state");

        String referer = jwtService.extractClaim(state, Claims::getSubject);
        ResponseCookie removeStateCookie = ResponseCookie
                .from("state", "")
                .path("/")
                .maxAge(0)
                .build();

        if(error != null)
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, (!referer.equals("")) ? referer : "http://localhost:8080")
                    .header(HttpHeaders.SET_COOKIE, removeStateCookie.toString())
                    .build();

        GithubAccessTokenRequestResponse response = githubOAuth2Service.getGithubAccessTokenRequestResponse(code);
        GithubGetUserRequestResponse githubUser = githubOAuth2Service.getGithubGetUserRequestResponse(response.getAccess_token());

        UserEntity user = userService.findUserByEmail(githubUser.getEmail());
        if(user == null){
            user = UserEntity.fromGithubGetUserRequestResponse(githubUser);
            userService.save(user);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, (!referer.equals("")) ? referer : "http://localhost:8080")
                .header(HttpHeaders.SET_COOKIE, Cookies.getAccessTokenCookie(accessToken).toString())
                .header(HttpHeaders.SET_COOKIE, Cookies.getRefreshTokenCookie(refreshToken).toString())
                .header(HttpHeaders.SET_COOKIE, removeStateCookie.toString())
                .build();
    }

    private String generateState(String referer){
        if(referer == null)
            referer = "";
        HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("salt", UUID.randomUUID().toString());
        return jwtService.generateToken(referer, extraClaims);
    }
}
