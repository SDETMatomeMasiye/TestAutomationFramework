package testauto.com.api.utils;

import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import testauto.com.common.LogUtil;
import testauto.com.common.ReportUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class ApiUtil {

    private final HTTPMethod method;
    private final String baseURI;
    private final String endPoint;
    private final String body;
    private Map<String, Object> requestSpecDetails;
    private final ExtentTest test;
    private ExtentTest node;
    private Response response;
    private boolean isTestPassed = true;
    private final StringBuilder message = new StringBuilder();

    public ApiUtil(Builder builder) {
        this.method = builder.method;
        this.baseURI = builder.baseURI;
        this.endPoint = builder.endPoint;
        this.body = builder.body;
        this.requestSpecDetails = builder.requestSpecDetails;
        this.test = builder.test;
    }

    public ApiUtil callApi(String description, Consumer<Response> runnable) {
        LogUtil.info("Starting api call - " + description, this.getClass());
        node = test.createNode(description);
        RestAssured.baseURI = baseURI;
        RequestSpecification requestSpec = getRequestSpec();
        response = switch (method) {
            case GET -> requestSpec.when().get(endPoint).then().extract().response();
            case POST -> {
                Objects.requireNonNull(body, "body cannot be null for POST request.");
                yield requestSpec.body(body).when().post(endPoint).then().extract().response();
            }
            case PUT -> requestSpec.body(body).when().put(endPoint).then().extract().response();
            case DELETE -> requestSpec.when().delete(endPoint).then().extract().response();
            case PATCH -> requestSpec.body(body).when().patch(endPoint).then().extract().response();
        };
        Objects.requireNonNull(response, "Response cannot be null at this point.");
        runnable.accept(response);
        LogUtil.info("Done api call - " + description, this.getClass());
        return this;
    }

    public ApiUtil validateStatusCode(int statusCode) {
        Objects.requireNonNull(response, "Response cannot be null at this point.");
        if (response.statusCode() == statusCode) {
            message.append("Expected status code '").append(statusCode)
                    .append("' matches the actual status code '").append(response.statusCode()).append("'.\n");
        } else {
            isTestPassed = false;
            message.append("Expected status code '").append(statusCode)
                    .append("' matches the actual status code '").append(response.statusCode()).append("'.\n");
        }
        return this;
    }

    public ApiUtil validateResponseField(String pathToKey, Object expected){
        if(pathToKey == null || pathToKey.isBlank()) throw new IllegalArgumentException("path to key cannot be null or empty / blank.");
        Objects.requireNonNull(response, "Response cannot be null at this point.");
        JsonPath jsonPath = new JsonPath(response.asString());
        Object actual = jsonPath.get(pathToKey);
        assertEquals(expected, actual, "Expected value '" + expected + "' does not match actual value '" + actual+ "' for path: '" + pathToKey + "'.");
        return this;
    }

    public void report() {
        String messageString = this.message.toString();
        String statusToReport = isTestPassed ? "pass" : "fail";
        ReportUtil.reportAPI(statusToReport, messageString, node, response);
        assertNotEquals("fail", statusToReport, messageString);
    }

    private RequestSpecification getRequestSpec() {
        RequestSpecification requestSpec = RestAssured.given();

        if (requestSpecDetails == null) {
            LogUtil.warn("A null collection of request specification details provided. Defaulting to an empty hashmap 'requestSpecDetails = new HashMap<>()'.", this.getClass());
            requestSpecDetails = new HashMap<>();
        }

        for (Map.Entry<String, Object> entry : requestSpecDetails.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith("path_")) {
                key = key.replace("path_", "");
                requestSpec.pathParam(key, value);
            } else if (key.startsWith("query_")) {
                key = key.replace("query_", "");
                requestSpec.queryParam(key, value);
            } else if (key.startsWith("header_")) {
                key = key.replace("header_", "");
                requestSpec.header(key, value);
            }
        }
        return requestSpec;
    }

    public static class Builder {
        private HTTPMethod method;
        private String baseURI;
        private String endPoint;
        private String body;
        private Map<String, Object> requestSpecDetails;
        private ExtentTest test;

        public Builder method(HTTPMethod method) {
            this.method = method;
            return this;
        }

        public Builder baseURI(String baseURI) {
            this.baseURI = baseURI;
            return this;
        }

        public Builder endPoint(String endPoint) {
            this.endPoint = endPoint;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder requestSpecDetails(Map<String, Object> requestSpecDetails) {
            this.requestSpecDetails = requestSpecDetails;
            return this;
        }

        public Builder test(ExtentTest test) {
            this.test = test;
            return this;
        }

        public ApiUtil build() {
            Objects.requireNonNull(method, "HTTP method must be set.");
            Objects.requireNonNull(baseURI, "BaseURI must be set.");
            Objects.requireNonNull(endPoint, "Endpoint must be set.");
            return new ApiUtil(this);
        }
    }
}
