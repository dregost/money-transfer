package com.dregost.moneytransfer.account.model;

import com.google.common.eventbus.EventBus;
import com.dregost.moneytransfer.account.model.event.*;
import com.dregost.moneytransfer.common.event.EventStore;
import com.dregost.moneytransfer.transfer.model.*;
import com.dregost.moneytransfer.transfer.model.event.*;
import lombok.val;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@Test
public class AccountServiceTest {
    private static final AccountId FROM_ACCOUNT_ID = AccountId.of("FROM_ACCOUNT");
    private static final AccountId TO_ACCOUNT_ID = AccountId.of("TO_ACCOUNT");
    private static final TransferId TRANSFER_ID = TransferId.of("TRANSFER_ID");
    private AccountReadOnlyRepository fakeAccountRepository;
    private EventBus fakeEventBus;
    private EventStore<AccountEvent> fakeEventStore;
    private Account fakeFromAccount;
    private Account fakeToAccount;
    private TransferDetails transferDetails;


    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setUp() {
        fakeAccountRepository = mock(AccountReadOnlyRepository.class);
        fakeEventBus = mock(EventBus.class);
        fakeEventStore = mock(EventStore.class);
        transferDetails = TransferDetails.builder()
                .transferId(TRANSFER_ID)
                .fromAccountId(FROM_ACCOUNT_ID)
                .toAccountId(TO_ACCOUNT_ID)
                .amount(BigDecimal.valueOf(100))
                .build();
        fakeFromAccount = mock(Account.class);
        when(fakeFromAccount.getId()).thenReturn(FROM_ACCOUNT_ID);
        fakeToAccount = mock(Account.class);
        when(fakeToAccount.getId()).thenReturn(TO_ACCOUNT_ID);
    }

    public void handleTransferCreated_whenBothAccountsExistAndDebitingWasSuccessful_shouldAppendAccountEvent() {
        val service = makeAccountService();
        val transferCreated = TransferCreated.of(transferDetails);
        when(fakeFromAccount.debit(any())).thenReturn(fakeFromAccount);
        when(fakeAccountRepository.findById(FROM_ACCOUNT_ID)).thenReturn(Optional.of(fakeFromAccount));
        when(fakeAccountRepository.findById(TO_ACCOUNT_ID)).thenReturn(Optional.of(fakeToAccount));

        service.handleTransferCreated(transferCreated);

        verify(fakeEventStore, times(1)).append(fakeFromAccount);
    }

    public void handleTransferCreated_whenBothAccountsExistAndDebitFailed_shouldPublishFailedEvent() {
        val service = makeAccountService();
        val transferCreated = TransferCreated.of(transferDetails);
        val description = "error description";
        when(fakeFromAccount.debit(any())).thenThrow(new RuntimeException(description));
        when(fakeAccountRepository.findById(FROM_ACCOUNT_ID)).thenReturn(Optional.of(fakeFromAccount));
        when(fakeAccountRepository.findById(TO_ACCOUNT_ID)).thenReturn(Optional.of(fakeToAccount));
        val expectedEvent = AccountDebitFailed.builder()
                .accountId(FROM_ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .description(description)
                .build();

        service.handleTransferCreated(transferCreated);

        verify(fakeEventBus, times(1)).post(expectedEvent);
    }

    public void handleTransferCreated_whenToAccountDoesNotExist_shouldPublishFailedEvent() {
        val service = makeAccountService();
        val transferCreated = TransferCreated.of(transferDetails);
        val description = "The credited account has not been found.";
        when(fakeAccountRepository.findById(FROM_ACCOUNT_ID)).thenReturn(Optional.of(fakeFromAccount));
        val expectedEvent = AccountDebitFailed.builder()
                .accountId(FROM_ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .description(description)
                .build();

        service.handleTransferCreated(transferCreated);

        verify(fakeEventBus, times(1)).post(expectedEvent);
    }

    public void handleTransferCreated_whenFromAccountDoesNotExist_shouldPublishFailedEvent() {
        val service = makeAccountService();
        val transferCreated = TransferCreated.of(transferDetails);
        val description = "The debited account has not been found.";
        when(fakeAccountRepository.findById(TO_ACCOUNT_ID)).thenReturn(Optional.of(fakeToAccount));
        val expectedEvent = AccountDebitFailed.builder()
                .accountId(FROM_ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .description(description)
                .build();

        service.handleTransferCreated(transferCreated);

        verify(fakeEventBus, times(1)).post(expectedEvent);
    }

    public void handleDebitRecorded_whenToAccountsExistsAndCreditingWasSuccessful_shouldAppendAccountEvent() {
        val service = makeAccountService();
        val debitRecorded = DebitRecorded.of(transferDetails);
        when(fakeToAccount.credit(any())).thenReturn(fakeToAccount);
        when(fakeAccountRepository.findById(TO_ACCOUNT_ID)).thenReturn(Optional.of(fakeToAccount));

        service.handleDebitRecorded(debitRecorded);

        verify(fakeEventStore, times(1)).append(fakeToAccount);
    }

    public void handleDebitRecorded_whenToAccountsExistsAndCreditFailed_shouldAppendAccountEvent() {
        val service = makeAccountService();
        val debitRecorded = DebitRecorded.of(transferDetails);
        val description = "error description";
        when(fakeToAccount.credit(any())).thenThrow(new RuntimeException(description));
        when(fakeAccountRepository.findById(TO_ACCOUNT_ID)).thenReturn(Optional.of(fakeToAccount));
        val expectedEvent = AccountCreditFailed.builder()
                .accountId(TO_ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .description(description)
                .build();

        service.handleDebitRecorded(debitRecorded);

        verify(fakeEventBus, times(1)).post(expectedEvent);
    }

    public void handleDebitRecorded_whenToAccountsDoesNotExists_shouldPublishFailedEvent() {
        val service = makeAccountService();
        val debitRecorded = DebitRecorded.of(transferDetails);
        val description = "The credited account has not been found.";

        val expectedEvent = AccountCreditFailed.builder()
                .accountId(TO_ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .description(description)
                .build();

        service.handleDebitRecorded(debitRecorded);

        verify(fakeEventBus, times(1)).post(expectedEvent);
    }

    public void handleRefundCreated_whenToAccountsExistsAndRefundingWasSuccessful_shouldAppendAccountEvent() {
        val service = makeAccountService();
        val refundCreated = RefundCreated.of(transferDetails);
        when(fakeFromAccount.refund(any())).thenReturn(fakeFromAccount);
        when(fakeAccountRepository.findById(FROM_ACCOUNT_ID)).thenReturn(Optional.of(fakeFromAccount));

        service.handleRefundCreated(refundCreated);

        verify(fakeEventStore, times(1)).append(fakeFromAccount);
    }

    public void handleRefundCreated_whenToAccountsExistsAndCreditFailed_shouldAppendAccountEvent() {
        val service = makeAccountService();
        val refundCreated = RefundCreated.of(transferDetails);
        val description = "error description";
        when(fakeFromAccount.refund(any())).thenThrow(new RuntimeException(description));
        when(fakeAccountRepository.findById(FROM_ACCOUNT_ID)).thenReturn(Optional.of(fakeFromAccount));
        val expectedEvent = ReturnFundsFailed.builder()
                .accountId(TO_ACCOUNT_ID)
                .transferId(TRANSFER_ID)
                .description(description)
                .build();

        service.handleRefundCreated(refundCreated);

        verify(fakeEventBus, times(1)).post(expectedEvent);
    }

    private AccountService makeAccountService() {
        return new AccountService(fakeAccountRepository, fakeEventBus, fakeEventStore);
    }
}