package com.client.ws.rasmooplus.integration;

import com.client.ws.rasmooplus.dto.wsraspay.CreditCardDto;
import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;
import com.client.ws.rasmooplus.integration.impl.WsRaspayIntegrationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para {@link WsRaspayIntegrationImpl}.
 *
 * <p><b>Estratégia:</b> a classe instancia o {@link RestTemplate} e os {@link org.springframework.http.HttpHeaders}
 * diretamente no construtor padrão (sem injeção pelo Spring), portanto usamos
 * {@link ReflectionTestUtils#setField} para substituir o campo {@code restTemplate}
 * pelo mock após a instanciação via {@code @InjectMocks}.
 *
 * <p>Cenários cobertos por método:
 * <ul>
 *   <li>✅ Sucesso       — API retorna 2xx com body preenchido</li>
 *   <li>⚠️ Body nulo    — API retorna resposta sem body (ex: 204 / bad request sem payload)</li>
 *   <li>❌ Exceção       — RestTemplate lança {@link HttpClientErrorException}</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class WsRaspayIntegrationImplTest {

    // ── Mock do RestTemplate ───────────────────────────────────────────────────
    // Declarado como @Mock para que o Mockito possa interceptar as chamadas.
    // Depois do setUp() ele é injetado via ReflectionTestUtils no campo privado da impl.
    @Mock
    private RestTemplate restTemplate;

    // @InjectMocks cria a instância real de WsRaspayIntegrationImpl.
    // O construtor padrão cria internamente um new RestTemplate() e um HttpHeaders —
    // por isso precisamos substituir o restTemplate após a construção.
    @InjectMocks
    private WsRaspayIntegrationImpl wsRaspayIntegration;

    // ── Constantes de URL (mesmos valores esperados pelo @Value no application.properties) ──
    private static final String RASPAY_HOST    = "http://localhost:8181";
    private static final String CUSTOMER_URL   = "/v1/customer";
    private static final String ORDER_URL      = "/v1/order";
    private static final String PAYMENT_URL    = "/v1/payment";

    /**
     * Executado antes de CADA teste.
     * <ol>
     *   <li>Injeta as URLs que viriam do application.properties via {@code @Value}.</li>
     *   <li>Substitui o {@code RestTemplate} criado no construtor pelo mock do Mockito.</li>
     * </ol>
     */
    @BeforeEach
    void setUp() {
        // Injeta @Value fields
        ReflectionTestUtils.setField(wsRaspayIntegration, "raspayHost",  RASPAY_HOST);
        ReflectionTestUtils.setField(wsRaspayIntegration, "customerUrl", CUSTOMER_URL);
        ReflectionTestUtils.setField(wsRaspayIntegration, "orderUrl",    ORDER_URL);
        ReflectionTestUtils.setField(wsRaspayIntegration, "paymentUrl",  PAYMENT_URL);

        // Substitui o restTemplate instanciado no construtor pelo mock
        ReflectionTestUtils.setField(wsRaspayIntegration, "restTemplate", restTemplate);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // createCustomer
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * ✅ Sucesso: POST /customer → API responde 201 com body CustomerDto.
     */
    @Test
    void givenCreateCustomer_whenApiReturns201WithBody_thenReturnCustomerDto() {
        // Arrange
        CustomerDto customerDto = CustomerDto.builder()
                .cpf("123.456.789-09")
                .email("joao@email.com")
                .firstName("Joao")
                .lastName("Silva")
                .build();

        ResponseEntity<CustomerDto> fakeResponse = ResponseEntity.of(Optional.of(customerDto));

        when(restTemplate.exchange(
                eq(RASPAY_HOST + CUSTOMER_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(CustomerDto.class)
        )).thenReturn(fakeResponse);

        // Act
        CustomerDto result = wsRaspayIntegration.createCustomer(customerDto);

        // Assert
        assertNotNull(result);
        assertEquals(customerDto.getCpf(),       result.getCpf());
        assertEquals(customerDto.getEmail(),      result.getEmail());
        assertEquals(customerDto.getFirstName(),  result.getFirstName());
        assertEquals(customerDto.getLastName(),   result.getLastName());

        verify(restTemplate, times(1)).exchange(
                eq(RASPAY_HOST + CUSTOMER_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(CustomerDto.class)
        );
    }

    /**
     * ⚠️ Body nulo: API responde sem body (ex: bad request sem payload JSON).
     */
    @Test
    void givenCreateCustomer_whenApiReturnsNoBody_thenReturnNull() {
        // Arrange
        CustomerDto customerDto = CustomerDto.builder().cpf("111.111.111-11").build();

        when(restTemplate.exchange(
                eq(RASPAY_HOST + CUSTOMER_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(CustomerDto.class)
        )).thenReturn(ResponseEntity.badRequest().build()); // body == null

        // Act
        CustomerDto result = wsRaspayIntegration.createCustomer(customerDto);

        // Assert
        assertNull(result, "Quando a API não retorna body, o resultado deve ser null");

        verify(restTemplate, times(1)).exchange(
                anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(CustomerDto.class));
    }

    /**
     * ❌ Exceção: RestTemplate lança HttpClientErrorException (4xx) → deve propagar.
     */
    @Test
    void givenCreateCustomer_whenApiThrowsHttpClientErrorException_thenRethrowException() {
        // Arrange
        CustomerDto customerDto = CustomerDto.builder().cpf("000.000.000-00").build();

        when(restTemplate.exchange(
                eq(RASPAY_HOST + CUSTOMER_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(CustomerDto.class)
        )).thenThrow(HttpClientErrorException.BadRequest.class);

        // Act & Assert
        assertThrows(HttpClientErrorException.class,
                () -> wsRaspayIntegration.createCustomer(customerDto),
                "Deve relançar a HttpClientErrorException recebida do RestTemplate");

        verify(restTemplate, times(1)).exchange(
                anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(CustomerDto.class));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // createOrder
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * ✅ Sucesso: POST /order → API responde com body OrderDto preenchido.
     */
    @Test
    void givenCreateOrder_whenApiReturns201WithBody_thenReturnOrderDto() {
        // Arrange
        OrderDto orderDto = OrderDto.builder()
                .customerId("customer-abc")
                .discount(BigDecimal.valueOf(10.00))
                .productAcronym("RASMP")
                .build();

        ResponseEntity<OrderDto> fakeResponse = ResponseEntity.of(Optional.of(orderDto));

        when(restTemplate.exchange(
                eq(RASPAY_HOST + ORDER_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(OrderDto.class)
        )).thenReturn(fakeResponse);

        // Act
        OrderDto result = wsRaspayIntegration.createOrder(orderDto);

        // Assert
        assertNotNull(result);
        assertEquals(orderDto.getCustomerId(),     result.getCustomerId());
        assertEquals(orderDto.getDiscount(),       result.getDiscount());
        assertEquals(orderDto.getProductAcronym(), result.getProductAcronym());

        verify(restTemplate, times(1)).exchange(
                eq(RASPAY_HOST + ORDER_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(OrderDto.class)
        );
    }

    /**
     * ⚠️ Body nulo: API responde sem body para createOrder.
     */
    @Test
    void givenCreateOrder_whenApiReturnsNoBody_thenReturnNull() {
        // Arrange
        OrderDto orderDto = OrderDto.builder().customerId("cust-001").build();

        when(restTemplate.exchange(
                eq(RASPAY_HOST + ORDER_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(OrderDto.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        // Act
        OrderDto result = wsRaspayIntegration.createOrder(orderDto);

        // Assert
        assertNull(result, "Quando a API não retorna body, o resultado deve ser null");
    }

    /**
     * ❌ Exceção: RestTemplate lança HttpClientErrorException em createOrder.
     */
    @Test
    void givenCreateOrder_whenApiThrowsHttpClientErrorException_thenRethrowException() {
        // Arrange
        OrderDto orderDto = OrderDto.builder().customerId("cust-002").build();

        when(restTemplate.exchange(
                eq(RASPAY_HOST + ORDER_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(OrderDto.class)
        )).thenThrow(HttpClientErrorException.NotFound.class);

        // Act & Assert
        assertThrows(HttpClientErrorException.class,
                () -> wsRaspayIntegration.createOrder(orderDto));

        verify(restTemplate, times(1)).exchange(
                anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(OrderDto.class));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // processPayment
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * ✅ Sucesso: POST /payment → API responde true (pagamento aprovado).
     */
    @Test
    void givenProcessPayment_whenApiReturnsTrue_thenReturnTrue() {
        // Arrange
        CreditCardDto creditCardDto = CreditCardDto.builder()
                .number("1234567812345678")
                .cvv(123L)
                .month(12L)
                .year(2027L)
                .installments(1L)
                .documentNumber("123.456.789-09")
                .build();

        PaymentDto paymentDto = PaymentDto.builder()
                .customerId("customer-abc")
                .orderId("order-xyz")
                .creditCard(creditCardDto)
                .build();

        ResponseEntity<Boolean> fakeResponse = ResponseEntity.of(Optional.of(Boolean.TRUE));

        when(restTemplate.exchange(
                eq(RASPAY_HOST + PAYMENT_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Boolean.class)
        )).thenReturn(fakeResponse);

        // Act
        Boolean result = wsRaspayIntegration.processPayment(paymentDto);

        // Assert
        assertNotNull(result);
        assertTrue(result, "Pagamento aprovado deve retornar true");

        verify(restTemplate, times(1)).exchange(
                eq(RASPAY_HOST + PAYMENT_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Boolean.class)
        );
    }

    /**
     * ⚠️ Body nulo: API processa pagamento mas não retorna body.
     */
    @Test
    void givenProcessPayment_whenApiReturnsNoBody_thenReturnNull() {
        // Arrange
        PaymentDto paymentDto = PaymentDto.builder()
                .customerId("cust-003")
                .orderId("order-003")
                .build();

        when(restTemplate.exchange(
                eq(RASPAY_HOST + PAYMENT_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Boolean.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        // Act
        Boolean result = wsRaspayIntegration.processPayment(paymentDto);

        // Assert
        assertNull(result, "Sem body na resposta o resultado deve ser null");
    }

    /**
     * ❌ Exceção: RestTemplate lança HttpClientErrorException em processPayment.
     */
    @Test
    void givenProcessPayment_whenApiThrowsHttpClientErrorException_thenRethrowException() {
        // Arrange
        PaymentDto paymentDto = PaymentDto.builder()
                .customerId("cust-004")
                .orderId("order-004")
                .build();

        when(restTemplate.exchange(
                eq(RASPAY_HOST + PAYMENT_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Boolean.class)
        )).thenThrow(HttpClientErrorException.UnprocessableEntity.class);

        // Act & Assert
        assertThrows(HttpClientErrorException.class,
                () -> wsRaspayIntegration.processPayment(paymentDto));

        verify(restTemplate, times(1)).exchange(
                anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Boolean.class));
    }
}
