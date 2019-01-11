package com.dregost.moneytransfer.account.read;

import com.google.common.eventbus.EventBus;
import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.account.model.event.*;
import com.dregost.moneytransfer.transfer.model.TransferId;
import lombok.val;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@Test
public class ReadAccountServiceTest {
    private static final AccountId ACCOUNT_ID = AccountId.of("ACCOUNT_ID");
    private static final TransferId TRANSFER_ID = TransferId.of("TRANSFER__ID");
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100);

    private AccountRepository fakeAccountRepository;
    private EventBus fakeEventBus;

    @BeforeMethod
    public void setUp() {
        fakeAccountRepository = mock(AccountRepository.class);
        fakeEventBus = mock(EventBus.class);
    }

    public void opened_shouldSaveEntity() {
        val accountOpened = AccountOpened.builder()
                .accountId(ACCOUNT_ID)
                .initialBalance(AMOUNT)
                .build();
        val expectedResponse = makeAccountResponse(AMOUNT);
        val service = makeReadAccountService();

        service.opened(accountOpened);

        verify(fakeAccountRepository, times(1)).save(expectedResponse);
    }

    public void credited_whenEntityExists_shouldSaveEntityWithIncreasedBalance() {
        val accountCredited = AccountCredited.builder()
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT)
                .transferId(TRANSFER_ID)
                .build();
        val id = ACCOUNT_ID.getValue();
        val initialBalance = BigDecimal.valueOf(300);
        val existingEntity = makeAccountResponse(initialBalance);
        when(fakeAccountRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        val service = makeReadAccountService();
        val expectedResponse = makeAccountResponse(initialBalance.add(AMOUNT));

        service.credited(accountCredited);

        verify(fakeAccountRepository, times(1)).save(expectedResponse);
    }

    public void credited_whenEntityDoesNotExist_shouldDoNothing() {
        val accountCredited = AccountCredited.builder()
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT)
                .transferId(TRANSFER_ID)
                .build();
        val service = makeReadAccountService();

        service.credited(accountCredited);

        verify(fakeAccountRepository, never()).save(any());
    }

    public void debited_whenEntityExists_shouldSaveEntityWithDecreasedBalance() {
        val accountDebited = AccountDebited.builder()
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT)
                .transferId(TRANSFER_ID)
                .build();
        val id = ACCOUNT_ID.getValue();
        val initialBalance = BigDecimal.valueOf(300);
        val existingEntity = makeAccountResponse(initialBalance);
        when(fakeAccountRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        val service = makeReadAccountService();
        val expectedResponse = makeAccountResponse(initialBalance.subtract(AMOUNT));

        service.debited(accountDebited);

        verify(fakeAccountRepository, times(1)).save(expectedResponse);
    }

    public void debited_whenEntityDoesNotExist_shouldDoNothing() {
        val accountDebited = AccountDebited.builder()
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT)
                .transferId(TRANSFER_ID)
                .build();
        val service = makeReadAccountService();

        service.debited(accountDebited);

        verify(fakeAccountRepository, never()).save(any());
    }

    public void fundsReturned_whenEntityExists_shouldSaveEntityWithIncreasedBalance() {
        val fundsReturned = FundsReturned.builder()
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT)
                .transferId(TRANSFER_ID)
                .build();
        val id = ACCOUNT_ID.getValue();
        val initialBalance = BigDecimal.valueOf(300);
        val existingEntity = makeAccountResponse(initialBalance);
        when(fakeAccountRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        val service = makeReadAccountService();
        val expectedResponse = makeAccountResponse(initialBalance.add(AMOUNT));

        service.fundsReturned(fundsReturned);

        verify(fakeAccountRepository, times(1)).save(expectedResponse);
    }

    public void fundsReturned_whenEntityDoesNotExist_shouldDoNothing() {
        val fundsReturned = FundsReturned.builder()
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT)
                .transferId(TRANSFER_ID)
                .build();
        val service = makeReadAccountService();

        service.fundsReturned(fundsReturned);

        verify(fakeAccountRepository, never()).save(any());
    }

    private ReadAccountService makeReadAccountService() {
        return new ReadAccountService(fakeEventBus, fakeAccountRepository);
    }

    private AccountResponse makeAccountResponse(BigDecimal balance) {
        return AccountResponse.builder()
                .id(ACCOUNT_ID.getValue())
                .balance(balance)
                .build();
    }

}