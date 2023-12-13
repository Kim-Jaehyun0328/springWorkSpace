package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String requestURI = req.getRequestURI();

        String uuid = UUID.randomUUID().toString();
        req.setAttribute(LOG_ID, uuid);

        if(handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler;
        }
        log.info("REQUEST [{}] [{}] [{}]", uuid, requestURI, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView){
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception{
        String requestURI = req.getRequestURI();
        String logId = (String) req.getAttribute(LOG_ID);
        log.info("RESPONSE [{}] [{}]", logId, requestURI);
        if(ex != null){
            log.info("afterCompletion error!!", ex);
        }
    }
}
