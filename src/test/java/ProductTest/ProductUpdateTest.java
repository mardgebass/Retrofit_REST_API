package ProductTest;

import com.github.javafaker.Faker;
import dto.Product;
import enums.CategoryType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.annachemic.db.dao.ProductsMapper;
import service.ProductService;
import utils.DbUtils;
import utils.PrettyLogger;
import utils.RetrofitUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ProductUpdateTest {
    static ProductsMapper productsMapper;
    static Retrofit client;
    static ProductService productService;
    private static Integer productId;
    static Faker faker = new Faker();
    static Product product;
    private Product product1;

    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        productsMapper = DbUtils.getProductsMapper();
    }

    @BeforeEach
    void setUp() throws IOException {

        product = new Product().withTitle(faker.food().dish())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryType.FOOD.getTitle());

        Response<Product> response = productService.createProduct(product).execute();

        PrettyLogger.DEFAULT.log(response.body().toString());

        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));

        productId = response.body().getId();

    }

    @Test
    void putProductsTest() throws IOException {

        product1 = new Product().withId(productId)
                .withTitle(faker.food().dish())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryType.FOOD.getTitle());

        Integer countProductsBefore = DbUtils.countProducts(productsMapper);

        Response<Product> response = productService.updateProduct(product1).execute();

        Integer countProductsAfter = DbUtils.countProducts(productsMapper);

        assertThat(countProductsAfter, equalTo(countProductsBefore));

        PrettyLogger.DEFAULT.log(response.body().toString());

        assertThat(response.isSuccessful(), is(true));
    }


    @AfterEach
    void getProductTest() throws IOException {
        Response<Product> response = productService.getProduct(productId).execute();

        PrettyLogger.DEFAULT.log(response.body().toString());

        assertThat(response.body().getTitle(), equalTo(product1.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product1.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product1.getCategoryTitle()));
    }


    @AfterAll
    static void tearDown() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(productId).execute();
        assertThat(response.isSuccessful(), is(true));
    }

}