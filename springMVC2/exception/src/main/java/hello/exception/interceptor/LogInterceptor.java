package hello.exception.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException{
        String requestURI = req.getRequestURI();
        String uuid = UUID.randomUUID().toString();
        req.setAttribute(LOG_ID, uuid);

        log.info("REQUEST [{}] [{}] [{}] [{}]", uuid, req.getDispatcherType(), requestURI, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView) throws Exception{
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception{
        String requestURI = req.getRequestURI();
        String logId = (String) req.getAttribute(LOG_ID);
        log.info("RESPONSE [{}] [{}] [{}]", logId, req.getDispatcherType(), requestURI);
        if(ex != null){
            log.error("afterCompletion error!!", ex);
        }
    }
}
