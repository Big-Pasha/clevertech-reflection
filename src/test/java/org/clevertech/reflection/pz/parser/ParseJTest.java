package org.clevertech.reflection.pz.parser;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.clevertech.reflection.pz.model.Customer;
import org.clevertech.reflection.pz.model.Order;
import org.clevertech.reflection.pz.model.Product;
import org.clevertech.reflection.pz.util.ModelSupplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.text.SimpleDateFormat;

class ParseJTest {

    static ObjectMapper objectMapper;
    static ParseJ parseJ;

    @BeforeAll
    static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

        parseJ = new ParseJ();
    }

    @Test
    void parsingNullObjectShouldReturnNull() throws JsonProcessingException {
        String parseJResult = parseJ.parseToJsonInLine(null);
        String objMapResult = objectMapper.writeValueAsString(null);

        assertEquals(objMapResult, parseJResult, "ParseJ should return null as objectMapper");
    }

    @Test
    void shouldBeEqualProductJsonWithObjMapper() throws JsonProcessingException {
        Product product = ModelSupplier.getProduct();

        String parseJResult = parseJ.parseToJsonInLine(product);
        String objMapResult = objectMapper.writeValueAsString(product);

        assertEquals(objMapResult, parseJResult, "ParseJ should return the same json object as objectMapper");
    }

    @Test
    void shouldBeEqualProductJsonWithObjMapperWithNullFields() throws JsonProcessingException {
        Product product = ModelSupplier.getProduct();
        product.setId(null);

        String parseJResult = parseJ.parseToJsonInLine(product);
        String objMapResult = objectMapper.writeValueAsString(product);

        assertEquals(objMapResult, parseJResult, "ParseJ should return the same json object as objectMapper");
    }

    @Test
    void shouldBeEqualOrderJsonWithObjMapper() throws JsonProcessingException {
        Order order = ModelSupplier.getOrder();

        String parseJResult = parseJ.parseToJsonInLine(order);
        String objMapResult = objectMapper.writeValueAsString(order);

        assertEquals(objMapResult, parseJResult, "ParseJ should return the same json object as objectMapper");
    }

    @Test
    void shouldBeEqualCustomerJsonWithObjMapper() throws JsonProcessingException {
        Customer customer = ModelSupplier.getCustomer();

        String parseJResult = parseJ.parseToJsonInLine(customer);
        String objMapResult = objectMapper.writeValueAsString(customer);

        assertEquals(objMapResult, parseJResult, "ParseJ should return the same json object as objectMapper");
    }

    @Test
    void parseNullShouldThrowException() {
        assertThrows(ParseJException.class, () -> parseJ.parseFromJson(Product.class, null));
    }

    @Test
    void parseEmptyStringShouldThrowException() {
        assertThrows(ParseJException.class, () -> parseJ.parseFromJson(Product.class, ""));
    }

    @Test
    void parseEmpyProductShouldReturnEmptyProduct() throws Exception {
        Product product = new Product();
        String productJson = objectMapper.writeValueAsString(product);

        Product parsedProduct = parseJ.parseFromJson(Product.class, productJson);

        assertEquals(product, parsedProduct, "ParseJ should return the equal product");
    }

    @Test
    void parseProductShouldReturnEqualsProduct() throws Exception {
        Product product = ModelSupplier.getProduct();
        String productJson = objectMapper.writeValueAsString(product);

        Product parsedProduct = parseJ.parseFromJson(Product.class, productJson);

        assertEquals(product, parsedProduct, "ParseJ should return the equal product");
    }

    @Test
    void parseEmpyOrderShouldReturnEmptyOrder() throws Exception {
        Order order = new Order();
        String productJson = objectMapper.writeValueAsString(order);

        Order parsedOrder = parseJ.parseFromJson(Order.class, productJson);

        assertEquals(order, parsedOrder, "ParseJ should return equal order");
    }

    @Test
    void parseOrderShouldReturnEqualOrder() throws Exception {
        Order order = ModelSupplier.getOrder();
        String productJson = objectMapper.writeValueAsString(order);

        Order parsedOrder = parseJ.parseFromJson(Order.class, productJson);

        assertEquals(order, parsedOrder, "ParseJ should return equal order");
    }

    @Test
    void parseEmptyCustomerShouldReturnEmptyCustomer() throws Exception {
        Customer customer = new Customer();
        String productJson = objectMapper.writeValueAsString(customer);

        Customer parsedCustomer = parseJ.parseFromJson(Customer.class, productJson);

        assertEquals(customer, parsedCustomer, "ParseJ should return equal customer");
    }

    @Test
    void parseCustomerShouldReturnEqualCustomer() throws Exception {
        Customer customer = ModelSupplier.getCustomer();
        String productJson = objectMapper.writeValueAsString(customer);

        Customer parsedCustomer = parseJ.parseFromJson(Customer.class, productJson);

        assertEquals(customer, parsedCustomer, "ParseJ should return equal customer");
    }
}
