package com.example.resourceserver.controller;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class FooController {

    @RequestMapping("/get")
    public String get(Principal principal){

        OAuth2Authentication authentication = (OAuth2Authentication) principal;

        authentication.getOAuth2Request().getScope(); //可资源校验

        authentication.getOAuth2Request().getResourceIds();//可资源校验


        return "foo";
    }

    @GetMapping("/userInfo")
    @ResponseBody
    public Principal userInfo(Principal principal) {
        return principal;
    }
}
