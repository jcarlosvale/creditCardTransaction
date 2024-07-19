package com.studying.creditCardTransactionService.view.rs;

import com.studying.creditCardTransactionService.domain.model.BalanceOfCategory;
import com.studying.creditCardTransactionService.domain.model.Category;
import com.studying.creditCardTransactionService.domain.model.CreditCardTransaction;
import com.studying.creditCardTransactionService.domain.repository.BalanceOfCategoryRepository;
import com.studying.creditCardTransactionService.domain.repository.CreditCardTransactionRepository;
import com.studying.creditCardTransactionService.view.dto.CreditCardTransactionRequestDto;
import com.studying.creditCardTransactionService.view.dto.CreditCardTransactionResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.studying.creditCardTransactionService.domain.model.TransactionStatus.APROVADA;
import static com.studying.creditCardTransactionService.domain.model.TransactionStatus.ERRO;
import static com.studying.creditCardTransactionService.domain.model.TransactionStatus.REJEITADA;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreditCardTransactionControllerITTest {

    public static final String ACCOUNT_ID_1 = "account id 1";
    public static final String ACCOUNT_ID_2 = "account id 2";


    public static final String FOOD_MCC = "5412";
    public static final UUID USED_TRANSACTION = UUID.randomUUID();
    public static final UUID FOOD_ID = UUID.randomUUID();
    private static final BigDecimal FOOD_AMOUNT = new BigDecimal("1.00");

    public static final String MEAL_MCC = "5811";
    public static final UUID MEAL_ID = UUID.randomUUID();
    private static final BigDecimal MEAL_AMOUNT = new BigDecimal("10.00");

    public static final UUID CASH_ID = UUID.randomUUID();
    private static final BigDecimal CASH_AMOUNT = new BigDecimal("100.00");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CreditCardTransactionRepository creditCardTransactionRepository;

    @Autowired
    private BalanceOfCategoryRepository balanceOfCategoryRepository;

    private String URL;

    @BeforeEach
    void setup() {
        URL = "http://localhost:" + port + "/v1/transactions";

        final var balances = List.of(
                new BalanceOfCategory(CASH_ID, ACCOUNT_ID_1, Category.CASH, CASH_AMOUNT, null),
                new BalanceOfCategory(MEAL_ID, ACCOUNT_ID_1, Category.MEAL, MEAL_AMOUNT, null),
                new BalanceOfCategory(FOOD_ID, ACCOUNT_ID_1, Category.FOOD, FOOD_AMOUNT, null)
                                    );

        balanceOfCategoryRepository.saveAll(balances);

        final var simpleBalance = new BalanceOfCategory(UUID.randomUUID(), ACCOUNT_ID_2, Category.FOOD,
                                                        BigDecimal.ONE, null);
        balanceOfCategoryRepository.save(simpleBalance);

        final var simpleTransaction =
                CreditCardTransaction.builder()
                        .id(USED_TRANSACTION)
                        .accountId(ACCOUNT_ID_2)
                        .amount(BigDecimal.ONE)
                        .merchant("simple merchant")
                        .balance(simpleBalance)
                        .build();

        creditCardTransactionRepository.save(simpleTransaction);
    }

    @AfterEach
    void tearDown() {
        creditCardTransactionRepository.deleteAll(creditCardTransactionRepository.findAll());
        balanceOfCategoryRepository.deleteAll(balanceOfCategoryRepository.findAll());
    }

    @Test
    void registerTransactionApprovedTest() {
        //GIVEN
        final var previousTransactionsCount = creditCardTransactionRepository.count();
        final var creditCardTransactionRequestDto = CreditCardTransactionRequestDto.builder()
                .id(UUID.randomUUID())
                .accountId(ACCOUNT_ID_1)
                .amount(new BigDecimal("0.01"))
                .merchant("some merchant")
                .mcc(FOOD_MCC)
                .build();
        final var version = balanceOfCategoryRepository.findById(FOOD_ID).get().getVersion();

        //WHEN
        final var response = restTemplate.postForEntity(URL, creditCardTransactionRequestDto,
                                                        CreditCardTransactionResponseDto.class);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // verify dto
        final var body = Objects.requireNonNull(response.getBody());
        assertThat(body.id()).isEqualTo(creditCardTransactionRequestDto.id());
        assertThat(body.code()).isEqualTo(APROVADA.getCode());

        // verify database
        assertThat(creditCardTransactionRepository.count()).isEqualTo(previousTransactionsCount + 1);
        final var entity =  balanceOfCategoryRepository.findById(FOOD_ID).orElseThrow();
        assertThat(entity.getAmount()).isEqualTo(new BigDecimal("0.99"));
        assertThat(entity.getVersion()).isEqualTo(version + 1);
    }

    @Test
    void registerTransactionRejectedTest() {
        //GIVEN
        final var previousTransactionsCount = creditCardTransactionRepository.count();
        final var creditCardTransactionRequestDto = CreditCardTransactionRequestDto.builder()
                .id(UUID.randomUUID())
                .accountId(ACCOUNT_ID_1)
                .amount(new BigDecimal("100.01"))
                .merchant("some merchant")
                .mcc("9999")
                .build();
        final var version = balanceOfCategoryRepository.findById(MEAL_ID).get().getVersion();

        //WHEN
        final var response = restTemplate.postForEntity(URL, creditCardTransactionRequestDto,
                                                        CreditCardTransactionResponseDto.class);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // verify dto
        final var body = Objects.requireNonNull(response.getBody());
        assertThat(body.id()).isEqualTo(creditCardTransactionRequestDto.id());
        assertThat(body.code()).isEqualTo(REJEITADA.getCode());

        // verify database
        assertThat(creditCardTransactionRepository.count()).isEqualTo(previousTransactionsCount);
        final var entity =  balanceOfCategoryRepository.findById(MEAL_ID).orElseThrow();
        assertThat(entity.getAmount()).isEqualTo(MEAL_AMOUNT);
        assertThat(entity.getVersion()).isEqualTo(version);
    }

    @Test
    void registerTransactionFallbackApprovedTest() {
        //GIVEN
        final var previousTransactionsCount = creditCardTransactionRepository.count();
        final var creditCardTransactionRequestDto = CreditCardTransactionRequestDto.builder()
                .id(UUID.randomUUID())
                .accountId(ACCOUNT_ID_1)
                .amount(new BigDecimal("10.01"))
                .merchant("some merchant")
                .mcc(MEAL_MCC)
                .build();
        final var mealVersion = balanceOfCategoryRepository.findById(MEAL_ID).get().getVersion();
        final var cashVersion = balanceOfCategoryRepository.findById(CASH_ID).get().getVersion();

        //WHEN
        final var response = restTemplate.postForEntity(URL, creditCardTransactionRequestDto,
                                                        CreditCardTransactionResponseDto.class);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // verify dto
        final var body = Objects.requireNonNull(response.getBody());
        assertThat(body.id()).isEqualTo(creditCardTransactionRequestDto.id());
        assertThat(body.code()).isEqualTo(APROVADA.getCode());

        // verify database
        assertThat(creditCardTransactionRepository.count()).isEqualTo(previousTransactionsCount+1);
        final var mealEntity =  balanceOfCategoryRepository.findById(MEAL_ID).orElseThrow();
        assertThat(mealEntity.getAmount()).isEqualTo(MEAL_AMOUNT);
        assertThat(mealEntity.getVersion()).isEqualTo(mealVersion);

        final var cashEntity =  balanceOfCategoryRepository.findById(CASH_ID).orElseThrow();
        assertThat(cashEntity.getAmount()).isEqualTo(new BigDecimal("89.99"));
        assertThat(cashEntity.getVersion()).isEqualTo(cashVersion+1);
    }

    @Test
    void registerTransactionFallbackRejectedTest() {
        //GIVEN
        final var previousTransactionsCount = creditCardTransactionRepository.count();
        final var creditCardTransactionRequestDto = CreditCardTransactionRequestDto.builder()
                .id(UUID.randomUUID())
                .accountId(ACCOUNT_ID_1)
                .amount(new BigDecimal("100.01"))
                .merchant("some merchant")
                .mcc(MEAL_MCC)
                .build();
        final var mealVersion = balanceOfCategoryRepository.findById(MEAL_ID).get().getVersion();
        final var cashVersion = balanceOfCategoryRepository.findById(CASH_ID).get().getVersion();

        //WHEN
        final var response = restTemplate.postForEntity(URL, creditCardTransactionRequestDto,
                                                        CreditCardTransactionResponseDto.class);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // verify dto
        final var body = Objects.requireNonNull(response.getBody());
        assertThat(body.id()).isEqualTo(creditCardTransactionRequestDto.id());
        assertThat(body.code()).isEqualTo(REJEITADA.getCode());

        // verify database
        assertThat(creditCardTransactionRepository.count()).isEqualTo(previousTransactionsCount);
        final var mealEntity =  balanceOfCategoryRepository.findById(MEAL_ID).orElseThrow();
        assertThat(mealEntity.getAmount()).isEqualTo(MEAL_AMOUNT);
        assertThat(mealEntity.getVersion()).isEqualTo(mealVersion);

        final var cashEntity =  balanceOfCategoryRepository.findById(CASH_ID).orElseThrow();
        assertThat(cashEntity.getAmount()).isEqualTo(CASH_AMOUNT);
        assertThat(cashEntity.getVersion()).isEqualTo(cashVersion);
    }

    @Test
    void registerTransactionWithoutCategoryTest() {
        //GIVEN
        final var previousTransactionsCount = creditCardTransactionRepository.count();
        final var creditCardTransactionRequestDto = CreditCardTransactionRequestDto.builder()
                .id(UUID.randomUUID())
                .accountId(ACCOUNT_ID_2)
                .amount(new BigDecimal("0.01"))
                .merchant("some merchant")
                .mcc("9999")
                .build();

        //WHEN
        final var response = restTemplate.postForEntity(URL, creditCardTransactionRequestDto,
                                                        CreditCardTransactionResponseDto.class);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // verify dto
        final var body = Objects.requireNonNull(response.getBody());
        assertThat(body.id()).isEqualTo(creditCardTransactionRequestDto.id());
        assertThat(body.code()).isEqualTo(ERRO.getCode());

        // verify database
        assertThat(creditCardTransactionRepository.count()).isEqualTo(previousTransactionsCount);
    }

    @Test
    void registerTransactionWithSameIdTest() {
        //GIVEN
        final var previousTransactionsCount = creditCardTransactionRepository.count();
        final var creditCardTransactionRequestDto = CreditCardTransactionRequestDto.builder()
                .id(USED_TRANSACTION)
                .accountId(ACCOUNT_ID_1)
                .amount(new BigDecimal("0.01"))
                .merchant("some merchant")
                .mcc(MEAL_MCC)
                .build();

        //WHEN
        final var response = restTemplate.postForEntity(URL, creditCardTransactionRequestDto,
                                                        CreditCardTransactionResponseDto.class);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // verify dto
        final var body = Objects.requireNonNull(response.getBody());
        assertThat(body.id()).isEqualTo(creditCardTransactionRequestDto.id());
        assertThat(body.code()).isEqualTo(ERRO.getCode());

        // verify database
        assertThat(creditCardTransactionRepository.count()).isEqualTo(previousTransactionsCount);
    }

    @Test
    void badRequestTest() {
        //GIVEN
        final var previousTransactionsCount = creditCardTransactionRepository.count();
        final var creditCardTransactionRequestDto = CreditCardTransactionRequestDto.builder()
                .id(UUID.randomUUID())
                .accountId(ACCOUNT_ID_1)
                .amount(new BigDecimal("0.01"))
                .merchant("some merchant")
                .mcc("xyzw")
                .build();

        //WHEN
        final var response = restTemplate.postForEntity(URL, creditCardTransactionRequestDto,
                                                        CreditCardTransactionResponseDto.class);

        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // verify database
        assertThat(creditCardTransactionRepository.count()).isEqualTo(previousTransactionsCount);
    }


}