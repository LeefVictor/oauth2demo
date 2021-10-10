package com.example.authserver.controller;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes({ "authorizationRequest" })
public class OAuthController {  
  
    @RequestMapping({ "/oauth/confirm" })
    public String getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        //可在这里要求必须输入scope参数
        if (request.getAttribute("_csrf") != null) {
            model.put("_csrf", request.getAttribute("_csrf"));
        }
        //
        return "AuthorityConfirm";
    }  
  
    @RequestMapping({ "/oauth/error" })
    public String handleError(Map<String, Object> model, HttpServletRequest request) {  
        Object error = request.getAttribute("error");  
        String errorSummary;  
        if (error instanceof OAuth2Exception) {
            OAuth2Exception oauthError = (OAuth2Exception) error;  
            errorSummary = HtmlUtils.htmlEscape(oauthError.getSummary());
        } else {  
            errorSummary = "Unknown error";  
        }  
        model.put("errorSummary", errorSummary);  
        return "Error";
    }

}  
