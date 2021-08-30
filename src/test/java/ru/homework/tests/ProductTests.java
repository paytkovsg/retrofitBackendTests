package ru.homework.tests;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.homework.db.dao.CategoriesMapper;
import ru.homework.db.dao.ProductsMapper;
import ru.homework.db.model.Products;
import ru.homework.dto.Product;
import ru.homework.enums.CategoryType;
import ru.homework.service.CategoryService;
import ru.homework.service.ProductService;
import ru.homework.utils.DbUtils;
import ru.homework.utils.RetrofitUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class ProductTests {
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    Faker faker = new Faker();
    Product product;
    int productId;
    static ProductsMapper productsMapper;
    static CategoriesMapper categoriesMapper;



    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);
        productsMapper = DbUtils.getProductsMapper();
        categoriesMapper = DbUtils.getCategoriesMapper();
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().dish())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryType.FOOD.getTitle());
    }

    @SneakyThrows
    @Test
    void postProductTest()  {

        Response<Product> response = productService.createProduct(product).execute();

        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
        productId = response.body().getId();

        Products dbProduct = DbUtils.selectProductBayKey(productsMapper, productId);

        assertThat(product.getTitle(), equalTo(dbProduct.getTitle()));
        assertThat(product.getPrice(), equalTo(dbProduct.getPrice()));

    }
    @SneakyThrows
    @Test
    void countProductAfterPostTest() {
        Integer countProductsBefore = DbUtils.countProducts(productsMapper);

        productService.createProduct(product).execute();

        Integer countProductsAfter = DbUtils.countProducts((productsMapper));

        assertThat(countProductsAfter, equalTo(countProductsBefore+1));

    }

    @SneakyThrows
    @Test
    void updateProductTest(){
        Response<Product> response = productService.createProduct(product).execute();
        productId = response.body().getId();
        Products updateProduct = DbUtils.updateProduct(productsMapper, "productUpdate", productId);
        assertThat(updateProduct.getTitle(), equalTo(DbUtils.selectProductBayKey(productsMapper, productId).getTitle()));
        assertThat(response.body().getTitle(), not(updateProduct.getTitle()));//дополнительная проверка

    }

    @SneakyThrows
    @Test
    void deleteProductTest(){
        Response<Product> response = productService.createProduct(product).execute();
        productId = response.body().getId();
        Integer countProductsBefore = DbUtils.countProducts(productsMapper);
        DbUtils.deleteProduct(productsMapper, productId);
        Integer countProductsAfter = DbUtils.countProducts((productsMapper));
        assertThat(countProductsAfter, equalTo(countProductsBefore-1));

    }



}
