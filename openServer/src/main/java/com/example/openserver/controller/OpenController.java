package com.example.openserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.Collections;

@RestController
public class OpenController {

    private String accessToken;


    private final RestTemplate restTemplate;

    public OpenController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/auth/code")
    public String login(@RequestParam String code) {
        String url = "http://localhost:8080/oauth/token";

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Authorization", "Basic b3Blbl9jbGllbnQ6MTIzNDU2");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", Collections.singletonList("authorization_code"));
        map.put("redirect_uri", Collections.singletonList("http://localhost:8083/auth/code"));
        map.put("code", Collections.singletonList(code));

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, httpHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        System.out.println(response.getBody());

        JSONObject obj = JSON.parseObject(response.getBody());
        accessToken = obj.getString("access_token");

        return response.getBody();
    }

    @RequestMapping("/get")
    public String get(){
        String url = "http://localhost:8082/get";

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Authorization", "Bearer "+accessToken);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, httpHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        return response.getBody();
    }
}
