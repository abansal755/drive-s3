package com.akshit.auth.service.oauth2;

import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.exception.ApiException;
import com.akshit.auth.model.GithubAccessTokenRequestResponse;
import com.akshit.auth.model.GithubGetUserRequestResponse;
import com.akshit.auth.model.Token;
import com.akshit.auth.service.CookiesService;
import com.akshit.auth.service.JwtService;
import com.akshit.auth.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.UUID;

@Service
public class GithubOAuth2Service {

    @Value("${oauth2.client.github.client-id}")
    private String githubClientId;

    @Value("${oauth2.client.github.client-secret}")
    private String githubClientSecret;

    @Value("${oauth2.client.github.redirect-uri}")
    private String githubRedirectUri;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private CookiesService cookiesService;

    public ResponseEntity<Void> authorizationEndpointHandler(HttpServletRequest request){
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

    public ResponseEntity<Void> callbackEndpointHandler(
            String code,
            String state,
            String error,
            HttpServletRequest request
    )
    {
        String stateCookie = cookiesService.readServletCookie(request, "state");
        if(stateCookie == null || !stateCookie.equals(state))
            throw new ApiException("Malformed state", HttpStatus.BAD_REQUEST);

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

        GithubAccessTokenRequestResponse response = getGithubAccessTokenRequestResponse(code);
        GithubGetUserRequestResponse githubUser = getGithubGetUserRequestResponse(response.getAccess_token());

        UserEntity user = userService.findUserByEmail(githubUser.getEmail());
        if(user == null){
            user = UserEntity.fromGithubGetUserRequestResponse(githubUser);
            userService.save(user);
        }

        Token accessToken = jwtService.generateAccessToken(user);
        Token refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, (!referer.equals("")) ? referer : "http://localhost:8080")
                .header(HttpHeaders.SET_COOKIE, cookiesService.getAccessTokenCookie(accessToken.getValue()).toString())
                .header(HttpHeaders.SET_COOKIE, cookiesService.getRefreshTokenCookie(refreshToken.getValue()).toString())
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

    private GithubAccessTokenRequestResponse getGithubAccessTokenRequestResponse(String code){
        String uri = UriComponentsBuilder
                .fromHttpUrl("https://github.com/login/oauth/access_token")
                .queryParam("client_id", githubClientId)
                .queryParam("client_secret", githubClientSecret)
                .queryParam("code", code)
                .encode()
                .build()
                .toString();
        return RestClient
                .create()
                .post()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, "application/json")
                .retrieve()
                .body(GithubAccessTokenRequestResponse.class);
    }

    private GithubGetUserRequestResponse getGithubGetUserRequestResponse(String accessToken){
        return RestClient
                .create()
                .get()
                .uri("https://api.github.com/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(GithubGetUserRequestResponse.class);
    }
}
