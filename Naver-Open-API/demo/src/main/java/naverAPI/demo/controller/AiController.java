package naverAPI.demo.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import naverAPI.demo.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AiController {

    private String clientId = "MUM8jVsHBJm8YkYlJns9";
    private String clientSecret = "FgwoEU90MP";
    String apiURL = "https://openapi.naver.com/v1/vision/celebrity"; // 유명인 얼굴 인식
    private static final String UPLOAD_DIR = "/Users/gimjaehyeon/naver-api";



    @GetMapping("/ai")
    public String getAi(HttpSession session, HttpServletResponse response, Model model,
                        @RequestParam(value = "value", required = false) String value,
                        @RequestParam(value = "confidence", required = false) String confidence,
                        @RequestParam(value = "imagePath", required = false) String imagePath) throws IOException {
        User loginUser = (User) session.getAttribute("loginUser");

        if(loginUser == null){
            response.sendRedirect("/");
        }
//        model.addAttribute("value", value);
//        model.addAttribute("confidence", confidence);
//        model.addAttribute("image", imagePath);

        System.out.println("image = " + imagePath);
        return "ai";
    }

    @PostMapping("/ai")
    public String postAi(Model model, @RequestParam("imageFile") MultipartFile file, HttpServletResponse response) throws IOException {

        String filename = file.getOriginalFilename();
        String imgFile = UPLOAD_DIR + File.separator + filename; //파일 경로
        file.transferTo(new File(imgFile));  //파일을 지정된 경로에 저장

        File uploadFile = new File(imgFile);

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders,uploadFile);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode facesArray = rootNode.get("faces");
        JsonNode firstFace = facesArray.get(0);
        JsonNode celebrityObject = firstFace.get("celebrity");
        String value = celebrityObject.get("value").asText();
        String confidence = celebrityObject.get("confidence").asText();
        model.addAttribute("value", value);
        model.addAttribute("confidence", confidence);
        model.addAttribute("imagePath", imgFile);

        return "ai";
    }



    private static String get(String apiUrl, Map<String, String> requestHeaders, File uploadFile){
        String boundary = "---" + System.currentTimeMillis() + "---";
        requestHeaders.put("Content-Type", "multipart/form-data; boundary=" + boundary);

        HttpURLConnection con = connect(apiUrl);
        con.setUseCaches(false);
        con.setDoOutput(true);
        con.setDoInput(true);

        try {
            con.setRequestMethod("POST");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            upload(con.getOutputStream(), uploadFile, boundary);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
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
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private static void upload(OutputStream outputStream, File uploadFile, String boundary){
        try(PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
            FileInputStream inputStream = new FileInputStream(uploadFile)){
            String LINE_FEED = "\r\n";

            String fileName = uploadFile.getName();
            String paramName = "image"; // 파라미터명은 image로 지정
            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"").append(paramName).append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
            writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            writer.append(LINE_FEED).flush();
            writer.append("--").append(boundary).append("--").append(LINE_FEED);
        } catch (IOException e){
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
