package hello.login.web.login;


import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.login.LoginForm;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm){
        return "login/loginForm";
    }

    //@PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult result, HttpServletResponse res){

        if(result.hasErrors()){
            return "login/loginForm";
        }

        Member member = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        log.info("login? {}", member);

        if(member == null){
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        Cookie idCookie = new Cookie("memberId", String.valueOf(member.getId()));
        res.addCookie(idCookie);

        return "redirect:/";
    }

   // @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm loginForm, BindingResult result, HttpServletResponse res){

        if(result.hasErrors()){
            return "login/loginForm";
        }

        Member member = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        log.info("login? {}", member);

        if(member == null){
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        sessionManager.createSession(member, res);

        return "redirect:/";
    }

    //@PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm loginForm, BindingResult result, HttpServletRequest req){

        if(result.hasErrors()){
            return "login/loginForm";
        }

        Member member = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        log.info("login? {}", member);

        if(member == null){
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        HttpSession session = req.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);


        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV4(@Valid @ModelAttribute LoginForm loginForm, BindingResult result
             ,@RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest req){

        if(result.hasErrors()){
            return "login/loginForm";
        }

        Member member = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        log.info("login? {}", member);

        if(member == null){
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        HttpSession session = req.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);


        return "redirect:" + redirectURL;
    }

    //@PostMapping("/logout")
    public String logout(HttpServletResponse res){
        expireCookie(res, "memberId");
        return "redirect:/";
    }

    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest req){
        sessionManager.expire(req);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest req){
        HttpSession session = req.getSession(false);
        if(session != null){
            session.invalidate();
        }
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse res, String cookieName){
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        res.addCookie(cookie);
    }

}
