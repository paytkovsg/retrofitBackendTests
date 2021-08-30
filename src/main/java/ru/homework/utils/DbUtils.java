package ru.homework.utils;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import ru.homework.db.dao.CategoriesMapper;
import ru.homework.db.dao.ProductsMapper;
import ru.homework.db.model.Categories;
import ru.homework.db.model.CategoriesExample;
import ru.homework.db.model.Products;
import ru.homework.db.model.ProductsExample;

import java.io.IOException;

@UtilityClass
public class DbUtils {
    private static  String resource = "mybatisConfig.xml";
    static Faker faker = new Faker();
    private static SqlSession getSqlSession() throws IOException {
        SqlSessionFactory sqlSessionFactory;
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(resource));
        return sqlSessionFactory.openSession(true);
    }
    @SneakyThrows
    public static CategoriesMapper getCategoriesMapper(){
        return getSqlSession().getMapper(CategoriesMapper.class);
    }
    @SneakyThrows
    public static ProductsMapper getProductsMapper() {
        return getSqlSession().getMapper(ProductsMapper.class);
    }
    private static void createNewCategory(CategoriesMapper categoriesMapper) {
        Categories newCategory = new Categories();
        newCategory.setTitle(faker.animal().name());

        categoriesMapper.insert(newCategory);
    }

    public static Integer countCategories(CategoriesMapper categoriesMapper) {
        long categoriesCount = categoriesMapper.countByExample(new CategoriesExample());
        return Math.toIntExact(categoriesCount);
    }

    public static Integer countProducts(ProductsMapper productsMapper) {
        long products = productsMapper.countByExample(new ProductsExample());
        return Math.toIntExact(products);
    }
    public static Products selectProductBayKey (ProductsMapper productsMapper, long key){
        return productsMapper.selectByPrimaryKey(key);
    }
    public static Products updateProduct(ProductsMapper productsMapper, String name, long key){
        Products product = new Products();
        product.setTitle(name);
        product.setId(key);
        productsMapper.updateByPrimaryKey(product);
        return product;
    }
    public static void  deleteProduct(ProductsMapper productsMapper, long key){
        Products product = new Products();
        product.setId(key);
        productsMapper.deleteByPrimaryKey(key);

    }
}
