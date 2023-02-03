package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerV3HandlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV3);
    }

    @Override
    public ModelView handle(HttpServletRequest req, HttpServletResponse res, Object handler) throws ServletException, IOException {
        ControllerV3 controller = (ControllerV3) handler;

        Map<String, String> paraMap = createParaMap(req);
        ModelView mv = controller.process(paraMap);

        return mv;
    }

    private Map<String, String> createParaMap(HttpServletRequest req){
        Map<String, String> paraMap = new HashMap<>();

        req.getParameterNames().asIterator()
                .forEachRemaining(paraName -> paraMap.put(paraName, req.getParameter(paraName)));
        return paraMap;
    }
}
