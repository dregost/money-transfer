package com.dregost.moneytransfer.transfer.read;

import com.google.common.eventbus.EventBus;
import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.transfer.model.*;
import com.dregost.moneytransfer.transfer.model.event.*;
import lombok.val;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.util.Optional;

import static com.dregost.moneytransfer.transfer.model.TransferStatus.*;
import static org.mockito.Mockito.*;

@Test
public class ReadTransferServiceTest {
    private static final TransferId TRANSFER_ID = TransferId.of("TRANSFER__ID");
    private static final AccountId FROM_ACCOUNT_ID = AccountId.of("FROM_ACCOUNT_ID");
    private static final AccountId TO_ACCOUNT_ID = AccountId.of("TO_ACCOUNT_ID");
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100);
    private static final TransferDetails TRANSFER_DETAILS = TransferDetails.builder()
            .transferId(TRANSFER_ID)
            .fromAccountId(FROM_ACCOUNT_ID)
            .toAccountId(TO_ACCOUNT_ID)
            .amount(AMOUNT)
            .build();

    private TransferRepository fakeTransferRepository;
    private EventBus fakeEventBus;

    @BeforeMethod
    public void setUp() {
        fakeTransferRepository = mock(TransferRepository.class);
        fakeEventBus = mock(EventBus.class);
    }

    public void created_shouldSaveEntity() {
        val transferCreated = TransferCreated.of(TRANSFER_DETAILS);
        val expectedResponse = makeTransferResponse(PENDING);
        val service = makeReadTransferService();

        service.created(transferCreated);

        verify(fakeTransferRepository, times(1)).save(expectedResponse);
    }

    public void creditRecorded_whenTransferExists_shouldSaveEntity() {
        val creditRecorded = CreditRecorded.of(TRANSFER_DETAILS);
        val existingEntity = makeTransferResponse(DEBITED);
        val expectedResponse = makeTransferResponse(COMPLETED);
        when(fakeTransferRepository.findById(TRANSFER_ID.getValue())).thenReturn(Optional.of(existingEntity));
        val service = makeReadTransferService();

        service.creditRecorded(creditRecorded);

        verify(fakeTransferRepository, times(1)).save(expectedResponse);
    }

    public void creditRecorded_whenTransferDoesNotExist_shouldDoNothing() {
        val creditRecorded = CreditRecorded.of(TRANSFER_DETAILS);
        val service = makeReadTransferService();

        service.creditRecorded(creditRecorded);

        verify(fakeTransferRepository, never()).save(any());
    }

    public void debitRecorded_whenTransferExists_shouldSaveEntity() {
        val debitRecorded = DebitRecorded.of(TRANSFER_DETAILS);
        val existingEntity = makeTransferResponse(PENDING);
        val expectedResponse = makeTransferResponse(DEBITED);
        when(fakeTransferRepository.findById(TRANSFER_ID.getValue())).thenReturn(Optional.of(existingEntity));
        val service = makeReadTransferService();

        service.debitRecorded(debitRecorded);

        verify(fakeTransferRepository, times(1)).save(expectedResponse);
    }

    public void debitRecorded_whenTransferDoesNotExist_shouldDoNothing() {
        val debitRecorded = DebitRecorded.of(TRANSFER_DETAILS);
        val service = makeReadTransferService();

        service.debitRecorded(debitRecorded);

        verify(fakeTransferRepository, never()).save(any());
    }

    public void refundCreated_whenTransferExists_shouldSaveEntity() {
        val refundCreated = RefundCreated.of(TRANSFER_DETAILS);
        val existingEntity = makeTransferResponse(PENDING);
        val expectedResponse = makeTransferResponse(FAILED);
        when(fakeTransferRepository.findById(TRANSFER_ID.getValue())).thenReturn(Optional.of(existingEntity));
        val service = makeReadTransferService();

        service.refundCreated(refundCreated);

        verify(fakeTransferRepository, times(1)).save(expectedResponse);
    }

    public void refundCreated_whenTransferDoesNotExist_shouldDoNothing() {
        val refundCreated = RefundCreated.of(TRANSFER_DETAILS);
        val service = makeReadTransferService();

        service.refundCreated(refundCreated);

        verify(fakeTransferRepository, never()).save(any());
    }

    public void transferFailed_whenTransferExists_shouldSaveEntity() {
        val transferFailed = TransferFailed.of(TRANSFER_DETAILS);
        val existingEntity = makeTransferResponse(PENDING);
        val expectedResponse = makeTransferResponse(FAILED);
        when(fakeTransferRepository.findById(TRANSFER_ID.getValue())).thenReturn(Optional.of(existingEntity));
        val service = makeReadTransferService();

        service.transferFailed(transferFailed);

        verify(fakeTransferRepository, times(1)).save(expectedResponse);
    }

    public void transferFailed_whenTransferDoesNotExist_shouldDoNothing() {
        val transferFailed = TransferFailed.of(TRANSFER_DETAILS);
        val service = makeReadTransferService();

        service.transferFailed(transferFailed);

        verify(fakeTransferRepository, never()).save(any());
    }

    private ReadTransferService makeReadTransferService() {
        return new ReadTransferService(fakeEventBus, fakeTransferRepository);
    }

    private TransferResponse makeTransferResponse(TransferStatus status) {
        return TransferResponse.builder()
                .id(TRANSFER_ID.getValue())
                .fromAccountId(FROM_ACCOUNT_ID.getValue())
                .toAccountId(TO_ACCOUNT_ID.getValue())
                .amount(AMOUNT)
                .status(status)
                .build();
    }
}