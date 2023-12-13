package naverAPI.demo.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import naverAPI.demo.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    private String clientId = "MUM8jVsHBJm8YkYlJns9";
    private String clientSecret = "FgwoEU90MP";


    @GetMapping("/search") //10개만 출력되게 함
    public String getSearch(HttpSession session, HttpServletResponse response,
                            @RequestParam(value = "query", required = false, defaultValue = "") String query,
                            @RequestParam(value = "searchResults", required = false) List<Object> searchResults,
                            Model model) throws IOException {
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null) {
            response.sendRedirect("/");
        }
        String text = null;
        System.out.println("query = " + query);
        try {
            text = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text;
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});

            List<Map<String, Object>> items = (List<Map<String, Object>>) jsonMap.get("items");
            List<String> titles = new ArrayList<>();
            List<String> links = new ArrayList<>();

            for (Map<String, Object> item : items) {
                String title = (String) item.get("title");
                String link = (String) item.get("link");

                titles.add(title);
                links.add(link);
            }

            System.out.println("Titles:");
            for (String title : titles) {
                System.out.println(title);
            }

            System.out.println("\nLinks:");
            for (String link : links) {
                System.out.println(link);
            }
            model.addAttribute("searchResults", items);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "search";
    }


    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}
