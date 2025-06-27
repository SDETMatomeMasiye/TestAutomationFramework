package testauto.com.api.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import testauto.com.common.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ApiUtil {

    private final HTTPMethod method;
    private final String baseURI;
    private final String endPoint;
    private final String body;
    private Map<String, Object> requestSpecDetails;

    public ApiUtil(Builder builder){
        this.method = builder.method;
        this.baseURI = builder.baseURI;
        this.endPoint = builder.endPoint;
        this.body = builder.body;
        this.requestSpecDetails = builder.requestSpecDetails;
    }

    public void callApi(String description, Consumer<Response> runnable){
        LogUtil.info("Starting api call - " + description, this.getClass());
        RestAssured.baseURI = baseURI;
        RequestSpecification requestSpec = getRequestSpec();
        Response response = switch (method) {
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
    }

    private RequestSpecification getRequestSpec(){
        RequestSpecification requestSpec = RestAssured.given();

        if(requestSpecDetails == null){
            LogUtil.warn("A null collection of request specification details provided. Defaulting to an empty hashmap 'requestSpecDetails = new HashMap<>()'.", this.getClass());
            requestSpecDetails = new HashMap<>();
        }

        for(Map.Entry<String, Object> entry: requestSpecDetails.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            if(key.startsWith("path_")){
                key = key.replace("path_","");
                requestSpec.pathParam(key, value);
            } else if (key.startsWith("query_")) {
                key = key.replace("query_","");
                requestSpec.queryParam(key, value);
            } else if(key.startsWith("header_")){
                key = key.replace("header_","");
                requestSpec.header(key, value);
            }
        }
        return requestSpec;
    }

    public static class Builder{
        private HTTPMethod method;
        private String baseURI;
        private String endPoint;
        private String body;
        private Map<String, Object> requestSpecDetails;

        public Builder method(HTTPMethod method){
            this.method = method;
            return this;
        }

        public Builder baseURI(String baseURI){
            this.baseURI = baseURI;
            return this;
        }

        public Builder endPoint(String endPoint){
            this.endPoint = endPoint;
            return this;
        }

        public Builder body(String body){
            this.body = body;
            return this;
        }

        public Builder requestSpecDetails(Map<String, Object> requestSpecDetails){
            this.requestSpecDetails = requestSpecDetails;
            return this;
        }

        public ApiUtil build(){
            Objects.requireNonNull(method, "HTTP method must be set.");
            Objects.requireNonNull(baseURI, "BaseURI must be set.");
            Objects.requireNonNull(endPoint, "Endpoint must be set.");
            return new ApiUtil(this);
        }
    }
}
