package com.dregost.moneytransfer.transfer.model;

import com.dregost.moneytransfer.account.model.AccountId;
import com.dregost.moneytransfer.transfer.model.command.CreateTransferCommand;
import com.dregost.moneytransfer.transfer.model.event.*;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.dregost.moneytransfer.transfer.model.TransferStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@Test
public class TransferTest {
    private final static TransferId TRANSFER_ID = TransferId.of("TRANSFER_ID");
    private final static AccountId FROM_ACCOUNT_ID = AccountId.of("FROM_ACCOUNT_ID");
    private final static AccountId TO_ACCOUNT_ID = AccountId.of("TO_ACCOUNT_ID");
    private final static BigDecimal AMOUNT = BigDecimal.valueOf(100);
    private final static TransferDetails TRANSFER_DETAILS = TransferDetails.builder()
            .transferId(TRANSFER_ID)
            .fromAccountId(FROM_ACCOUNT_ID)
            .toAccountId(TO_ACCOUNT_ID)
            .amount(AMOUNT)
            .build();

    public void create_shouldCreateTransfer() {
        val createTransferCommand = makeCreateTransferCommand();

        val result = Transfer.create(createTransferCommand);

        val softly = new SoftAssertions();
        softly.assertThat(result.getId()).isEqualTo(TRANSFER_ID);
        softly.assertThat(result.getFromAccountId()).isEqualTo(FROM_ACCOUNT_ID);
        softly.assertThat(result.getFromAccountId()).isEqualTo(FROM_ACCOUNT_ID);
        softly.assertThat(result.getAmount()).isEqualTo(AMOUNT);
        softly.assertAll();
    }

    public void getPendingEvent_afterCreating_shouldContainsTransferCreatedEvent() {
        val createTransferCommand = makeCreateTransferCommand();
        val transferCreated = TransferCreated.of(TRANSFER_DETAILS);

        val result = Transfer.create(createTransferCommand);

        assertThat(result.getPendingEvents()).contains(transferCreated);
    }

    public void markAsDebited_shouldChangeStatusToDebited() {
        val createTransferCommand = makeCreateTransferCommand();
        val transfer = Transfer.create(createTransferCommand);

        val result = transfer.markAsDebited();

        assertThat(result.getStatus()).isEqualTo(DEBITED);
    }

    public void getPendingEvent_afterTransferHasBeenMarkedAsDebited_shouldContainsDebitRecordedEvent() {
        val createTransferCommand = makeCreateTransferCommand();
        val debitRecorded = DebitRecorded.of(TRANSFER_DETAILS);
        val transfer = Transfer.create(createTransferCommand);

        val result = transfer.markAsDebited();

        assertThat(result.getPendingEvents()).contains(debitRecorded);
    }

    public void markAsCredited_shouldChangeStatusToCompleted() {
        val createTransferCommand = makeCreateTransferCommand();
        val transfer = Transfer.create(createTransferCommand);

        val result = transfer.markAsCredited();

        assertThat(result.getStatus()).isEqualTo(COMPLETED);
    }

    public void getPendingEvent_afterTransferHasBeenMarkedAsCredited_shouldContainsCreditRecordedEvent() {
        val createTransferCommand = makeCreateTransferCommand();
        val creditRecorded = CreditRecorded.of(TRANSFER_DETAILS);
        val transfer = Transfer.create(createTransferCommand);

        val result = transfer.markAsCredited();

        assertThat(result.getPendingEvents()).contains(creditRecorded);
    }

    public void markAsFailed_shouldChangeStatusToFailed() {
        val createTransferCommand = makeCreateTransferCommand();
        val transfer = Transfer.create(createTransferCommand);

        val result = transfer.markAsFailed();

        assertThat(result.getStatus()).isEqualTo(FAILED);
    }

    public void getPendingEvent_whenTransferWasPendingAndHasBeenMarkedAsFailed_shouldContainsTransferFailedEvent() {
        val createTransferCommand = makeCreateTransferCommand();
        val transferFailed = TransferFailed.of(TRANSFER_DETAILS);
        val transfer = Transfer.create(createTransferCommand);

        val result = transfer.markAsFailed();

        assertThat(result.getPendingEvents()).contains(transferFailed);
    }

    public void getPendingEvent_whenTransferWasDebitedAndHasBeenMarkedAsFailed_shouldContainsRefundCreatedEvent() {
        val createTransferCommand = makeCreateTransferCommand();
        val refundCreated = RefundCreated.of(TRANSFER_DETAILS);
        val transfer = Transfer.create(createTransferCommand);

        val result = transfer.markAsDebited()
                .markAsFailed();

        assertThat(result.getPendingEvents()).contains(refundCreated);
    }

    private CreateTransferCommand makeCreateTransferCommand() {
        return CreateTransferCommand.builder()
                .transferId(TRANSFER_ID)
                .fromAccountId(FROM_ACCOUNT_ID)
                .toAccountId(TO_ACCOUNT_ID)
                .amount(AMOUNT)
                .build();
    }
}