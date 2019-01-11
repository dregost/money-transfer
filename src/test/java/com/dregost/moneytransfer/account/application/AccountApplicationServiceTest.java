package com.dregost.moneytransfer.account.application;

import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.account.model.event.AccountEvent;
import com.dregost.moneytransfer.account.read.*;
import com.dregost.moneytransfer.common.IdGenerator;
import com.dregost.moneytransfer.common.event.EventStore;
import lombok.val;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Test
public class AccountApplicationServiceTest {
    private static final String ACCOUNT_ID = "FAKE_ID";
    private IdGenerator fakeIdGenerator;
    private EventStore<AccountEvent> fakeEventStore;
    private AccountRepository fakeAccountResponseRepository;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void setUp() {
        fakeIdGenerator = mock(IdGenerator.class);
        fakeEventStore = mock(EventStore.class);
        fakeAccountResponseRepository = mock(AccountRepository.class);
    }

    public void openAccount_whenInitialBalanceWasProvide_shouldOpenAccountWithInitialBalance() {
        val initialBalance = BigDecimal.valueOf(100);
        when(fakeIdGenerator.generateId(any())).thenReturn(AccountId.of(ACCOUNT_ID));
        val expectedResult = AccountResponse.builder()
                .id(ACCOUNT_ID)
                .balance(initialBalance)
                .build();
        val service = makeAccountApplicationService();

        val result = service.openAccount(initialBalance);

        assertThat(result).isEqualTo(expectedResult);
    }

    public void getDetails_whenRepositoryDoesNotContainRequiredAccount_shouldReturnEmptyOptional() {
        val service = makeAccountApplicationService();

        val result = service.getDetails("MISSING_ID");

        assertThat(result).isEmpty();
    }

    public void getDetails_whenRepositoryContainsRequiredAccount_shouldReturnAccount() {
        val expectedResult = AccountResponse.builder()
                .id(ACCOUNT_ID)
                .balance(BigDecimal.valueOf(200))
                .build();
        when(fakeAccountResponseRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(expectedResult));
        val service = makeAccountApplicationService();

        val result = service.getDetails(ACCOUNT_ID);

        assertThat(result).contains(expectedResult);
    }

    private AccountApplicationService makeAccountApplicationService() {
        return new AccountApplicationService(fakeIdGenerator, fakeEventStore, fakeAccountResponseRepository);
    }

}