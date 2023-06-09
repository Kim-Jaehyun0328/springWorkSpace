package naverAPI.demo.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import naverAPI.demo.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;

@Controller
public class LoginController {

    private String clientId = "MUM8jVsHBJm8YkYlJns9";
    private String clientSecret = "FgwoEU90MP";

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loggedIn", loginUser);
        System.out.println("loginUser.toString() = " + loginUser);
        return "home";}


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
    public String getNaverLogin(@RequestParam("code") String code, @RequestParam("state") String state, Model model, HttpSession session) {
        System.out.println("code = " + code);
        System.out.println("state = " + state);

        User user = new User(code, state);
        session.setAttribute("loginUser", user);
        model.addAttribute("loggedIn", user);
        return "home";
    }


    @GetMapping("/logout")
    public void getLogout(Model model, HttpServletResponse response, HttpSession session) throws IOException {
        User loginUser = (User) session.getAttribute("loginUser");
        session.invalidate();
        model.addAttribute("loggedIn", loginUser);
        response.sendRedirect("/");
    }


}
