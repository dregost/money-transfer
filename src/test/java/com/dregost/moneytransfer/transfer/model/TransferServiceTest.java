package com.dregost.moneytransfer.transfer.model;

import com.google.common.eventbus.EventBus;
import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.account.model.event.*;
import com.dregost.moneytransfer.common.event.EventStore;
import com.dregost.moneytransfer.transfer.model.event.TransferEvent;
import lombok.val;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@Test
public class TransferServiceTest {
    private static final TransferId TRANSFER_ID = TransferId.of("TRANSFER_ID");
    private static final AccountId ACCOUNT_ID = AccountId.of("ACCOUNT");
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100);
    private static final String ERROR_DESCRIPTION = "ERROR_DESCRIPTION";

    private TransferReadOnlyRepository fakeTransferRepository;
    private EventBus fakeEventBus;
    private EventStore<TransferEvent> fakeEventStore;
    private Transfer fakeTransfer;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setUp() {
        fakeTransferRepository = mock(TransferReadOnlyRepository.class);
        fakeEventBus = mock(EventBus.class);
        fakeEventStore = mock(EventStore.class);
        fakeTransfer = mock(Transfer.class);
        when(fakeTransfer.getId()).thenReturn(TRANSFER_ID);
    }

    public void handleAccountDebited_whenTransferExists_shouldAppendAccountEvent() {
        val service = makeTransferService();
        val accountDebited = AccountDebited.builder()
                .transferId(TRANSFER_ID)
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT)
                .build();

        when(fakeTransfer.markAsDebited()).thenReturn(fakeTransfer);
        when(fakeTransferRepository.findById(TRANSFER_ID)).thenReturn(Optional.of(fakeTransfer));

        service.handleAccountDebited(accountDebited);

        verify(fakeEventStore, times(1)).append(fakeTransfer);
    }

    public void handleAccountDebited_whenTransferDoesNotExist_shouldDoNothing() {
        val service = makeTransferService();
        val accountDebited = AccountDebited.builder()
                .transferId(TRANSFER_ID)
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT)
                .build();

        service.handleAccountDebited(accountDebited);

        verify(fakeEventStore, never()).append(any());
    }

    public void handleAccountDebitFailed_whenTransferExists_shouldAppendAccountEvent() {
        val service = makeTransferService();
        val accountDebitFailed = AccountDebitFailed.builder()
                .transferId(TRANSFER_ID)
                .accountId(ACCOUNT_ID)
                .description(ERROR_DESCRIPTION)
                .build();

        when(fakeTransfer.markAsFailed()).thenReturn(fakeTransfer);
        when(fakeTransferRepository.findById(TRANSFER_ID)).thenReturn(Optional.of(fakeTransfer));

        service.handleAccountDebitFailed(accountDebitFailed);

        verify(fakeEventStore, times(1)).append(fakeTransfer);
    }

    public void handleAccountDebitFailed_whenTransferDoesNotExist_shouldDoNothing() {
        val service = makeTransferService();
        val accountDebitFailed = AccountDebitFailed.builder()
                .transferId(TRANSFER_ID)
                .accountId(ACCOUNT_ID)
                .description(ERROR_DESCRIPTION)
                .build();

        service.handleAccountDebitFailed(accountDebitFailed);

        verify(fakeEventStore, never()).append(any());
    }

    public void handleAccountCredited_whenTransferExists_shouldAppendAccountEvent() {
        val service = makeTransferService();
        val accountCredited = AccountCredited.builder()
                .transferId(TRANSFER_ID)
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT)
                .build();

        when(fakeTransfer.markAsCredited()).thenReturn(fakeTransfer);
        when(fakeTransferRepository.findById(TRANSFER_ID)).thenReturn(Optional.of(fakeTransfer));

        service.handleAccountCredited(accountCredited);

        verify(fakeEventStore, times(1)).append(fakeTransfer);
    }

    public void handleAccountCredited_whenTransferDoesNotExist_shouldDoNothing() {
        val service = makeTransferService();
        val accountCredited = AccountCredited.builder()
                .transferId(TRANSFER_ID)
                .accountId(ACCOUNT_ID)
                .amount(AMOUNT)
                .build();

        service.handleAccountCredited(accountCredited);

        verify(fakeEventStore, never()).append(any());
    }

    public void handleAccountCreditFailed_whenTransferExists_shouldAppendAccountEvent() {
        val service = makeTransferService();
        val accountCreditFailed = AccountCreditFailed.builder()
                .transferId(TRANSFER_ID)
                .accountId(ACCOUNT_ID)
                .description(ERROR_DESCRIPTION)
                .build();

        when(fakeTransfer.markAsFailed()).thenReturn(fakeTransfer);
        when(fakeTransferRepository.findById(TRANSFER_ID)).thenReturn(Optional.of(fakeTransfer));

        service.handleAccountCreditFailed(accountCreditFailed);

        verify(fakeEventStore, times(1)).append(fakeTransfer);
    }

    public void handleAccountCreditFailed_whenTransferDoesNotExist_shouldDoNothing() {
        val service = makeTransferService();
        val accountCreditFailed = AccountCreditFailed.builder()
                .transferId(TRANSFER_ID)
                .accountId(ACCOUNT_ID)
                .description(ERROR_DESCRIPTION)
                .build();

        service.handleAccountCreditFailed(accountCreditFailed);

        verify(fakeEventStore, never()).append(any());
    }

    private TransferService makeTransferService() {
        return new TransferService(fakeEventBus, fakeTransferRepository, fakeEventStore);
    }
}