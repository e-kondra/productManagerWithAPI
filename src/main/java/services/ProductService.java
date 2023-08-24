package services;

import com.google.gson.Gson;
import dto.Product;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductService {
    private String BASE_URL;
    private final String ENDPOINT = "/products";
    private final Gson gson = new Gson();
    private HttpClient httpClient = HttpClient.newHttpClient();

    public ProductService() {
        this.loadAPIProperties();
    }
    private void loadAPIProperties(){
        try {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
            propertiesConfiguration.load("application.properties");
            this.BASE_URL = propertiesConfiguration.getString("api.url");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void createProduct(Product product) throws Exception{
        String requestBody = gson.toJson(product);

        //create HTTP request using HttpRequest class
        HttpRequest request = HttpRequest.newBuilder()
                // configure the request using HttpRequest
                .uri(new URI(this.BASE_URL + this.ENDPOINT)) // specify the address to send the request
                .timeout(Duration.ofSeconds(30)) //how long should wait for response from API before in request fails
                .header("Content-Type","application/json")// the type of data we are sending in the request additional configuration could also be added here like cookies
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)) //the type of request we are making and the data to send if data is required
                .build(); // the final copy of the request is compiled based on configuration above and ready for sending

        // send request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()); // The request is sent and response body converted into string

        System.out.println(response.statusCode());
        System.out.println(response.body());

        //check the response and response code for success or failure
        if(response.statusCode() != 201){ // we can check the status code to see if it was successful
            throw new Exception("Request failed with status code of: " + response.statusCode());
        }

        //extract the todo item from the response
        Product createdProduct = gson.fromJson(response.body(), Product.class); // we try to extract json response body from json

        //check if the object was properly created
        if(createdProduct == null || createdProduct.get_id() == null){
            throw new Exception("Failed to create todo item with code " + response.statusCode());
        }
    }

    public List<Product> getAllProducts() throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(this.BASE_URL + this.ENDPOINT))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")// what type we are sending
                .header("Accept","application/json") // what type we are expecting
                .GET()
                .build();
        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200){
            throw new Exception("Request failed, API responded with code: " + response.statusCode());
        }

        List<Product> convertedListFromAPI = Arrays.asList(this.gson.fromJson(response.body(), Product[].class));
        return new ArrayList<Product> (convertedListFromAPI);
    }

    public Product updateProduct(Product product, String prod_id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(this.BASE_URL + this.ENDPOINT + "/" + prod_id))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type","application/json")
                .header("Accept","application/json") // what type we are expecting
                .PUT(HttpRequest.BodyPublishers.ofString(this.gson.toJson(product)))
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new Exception("Unable to update the product item with id " + prod_id +
                    ". Error code: " + response.statusCode() + response.body());

        return this.gson.fromJson(response.body(), Product.class);
    }

    public void deleteProduct(String productId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(this.BASE_URL + this.ENDPOINT + "/" + productId))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new Exception("Unable to delete product item with id" + productId + ", error code: " + response.statusCode());
    }
}
