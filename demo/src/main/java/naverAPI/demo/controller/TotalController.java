package naverAPI.demo.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

@Controller
public class TotalController {

    private String clientId = "MUM8jVsHBJm8YkYlJns9";
    private String clientSecret = "FgwoEU90MP";


    private static Boolean loggedIn = Boolean.FALSE;

    @GetMapping("/")
    public String home() {return "home";}


    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @PostMapping("/login")
    public void postLogin(HttpServletResponse response) throws IOException {
        String basicUrl = "https://nid.naver.com/oauth2.0/authorize";
        String url = UriComponentsBuilder
                .fromUriString(basicUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", URLEncoder.encode("http://localhost:8080/naver-login", "UTF-8"))
                .queryParam("state", URLEncoder.encode("123", "UTF-8"))
                .build().toString();

        response.sendRedirect(url);
    }

    @GetMapping("/naver-login")
    public String getNaverLogin(@RequestParam("code") String code, @RequestParam("state") String state, Model model) {
        System.out.println("code = " + code);
        System.out.println("state = " + state);

        loggedIn = Boolean.TRUE;
        model.addAttribute("loggedIn", loggedIn);
        return "home";
    }


    @GetMapping("/logout")
    public void getLogout(Model model, HttpServletResponse response) throws IOException {
        loggedIn = Boolean.FALSE;
        model.addAttribute("loggedIn", loggedIn);
        response.sendRedirect("/");
    }


}
