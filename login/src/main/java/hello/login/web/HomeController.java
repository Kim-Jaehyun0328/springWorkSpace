package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentresolver.Login;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    //@GetMapping("/")
    public String homLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model){

        if(memberId == null){
            return "home";
        }

        Member loginMember = memberRepository.findById(memberId);
        if(loginMember == null){
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homLoginV2(HttpServletRequest req, Model model){

        Member member = (Member)sessionManager.getSession(req);

        if(member == null){
            return "home";
        }

        model.addAttribute("member", member);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homLoginV3(HttpServletRequest req, Model model){

        HttpSession session = req.getSession(false);
        if(session == null){
            return "home";
        }

        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);


        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homLoginV4(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model){

        if(loginMember == null){
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model){
        if(loginMember == null){
            return "home";
        }
        model.addAttribute("member", loginMember);
        return "loginHome";

    }
}