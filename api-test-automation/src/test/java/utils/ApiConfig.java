package utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiConfig {

    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private static final Pattern STATUS_PATTERN = Pattern.compile("status code: (\\d+)");

    public static RequestSpecification petSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath("/pet")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    public static SafeResponse safeExecute(Supplier<Response> call) {
        try {
            Response resp = call.get();
            return new SafeResponse(resp.statusCode(), resp);
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            Matcher m = STATUS_PATTERN.matcher(msg);
            if (m.find()) {
                return new SafeResponse(Integer.parseInt(m.group(1)), null);
            }
            throw new RuntimeException("Beklenmeyen hata: " + msg, e);
        }
    }

    public static class SafeResponse {
        private final int statusCode;
        private final Response response;

        public SafeResponse(int statusCode, Response response) {
            this.statusCode = statusCode;
            this.response = response;
        }

        public int statusCode() {
            return statusCode;
        }

        public Response response() {
            return response;
        }

        public String jsonString(String path) {
            if (response == null) return null;
            return response.jsonPath().getString(path);
        }

        public long jsonLong(String path) {
            if (response == null) return -1;
            return response.jsonPath().getLong(path);
        }
    }
}
