package hello.login.web.session;

import hello.login.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SessionManagerTest {

    SessionManager sessionManager = new SessionManager();

    @Test
    void sessionTest(){
        MockHttpServletResponse res = new MockHttpServletResponse();
        Member member = new Member();
        sessionManager.createSession(member, res);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(res.getCookies());

        Object result = sessionManager.getSession(req);
        Assertions.assertThat(result).isEqualTo(member);

        sessionManager.expire(req);
        Object expired = sessionManager.getSession(req);
        Assertions.assertThat(expired).isNull();


    }
}
