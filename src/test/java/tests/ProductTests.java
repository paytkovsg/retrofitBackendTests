package tests;

import com.github.javafaker.Faker;
import dto.Category;
import dto.Product;
import enums.CategoryType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import service.CategoryService;
import service.ProductService;
import utils.RetrofitUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProductTests {
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    Faker faker = new Faker();
    Product product;
    static int id;
    static String title;


    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().dish())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryType.FOOD.getTitle());
    }

   @Test
    void addProductTest() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();
        id = response.body().getId();
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
    }

    @Test
    void createProductTest() throws IOException {
        Response<Product> response = productService.getProduct(id).execute();
        assertThat(response.code(), equalTo(200));

    }

    void deleteCreatedProductTest() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(id).execute();
        assertThat(response.code(), equalTo(200));
    }

    @Test
    void createAndDeletedTest() throws IOException {
        addProductTest();
        createProductTest();
        deleteCreatedProductTest();
        deletedProductTest();

    }

    void deletedProductTest() throws IOException {
        Response<Product> response = productService.getProduct(id).execute();
        assertThat(response.code(), equalTo(404));
    }

    @Test
    void getCategoryByIdTest() throws IOException {
        Integer id = CategoryType.FOOD.getId();
        Response<Category> response = categoryService
                .getCategory(id)
                .execute();
        assertThat(response.body().getTitle(), equalTo(CategoryType.FOOD.getTitle()));
        assertThat(response.body().getId(), equalTo(id));
    }

    @Test
    void updateProduct() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();
        id = response.body().getId();
        title = response.body().getTitle();
        Response<Product> responseUpdate = productService.updateProduct(id, title+1).execute();
        assertThat(responseUpdate.code(), equalTo(200));


    }

}
