package com.dregost.moneytransfer.transfer.application;

import com.dregost.moneytransfer.common.IdGenerator;
import com.dregost.moneytransfer.common.event.EventStore;
import com.dregost.moneytransfer.transfer.infrastructure.CreateTransferRequest;
import com.dregost.moneytransfer.transfer.model.*;
import com.dregost.moneytransfer.transfer.model.event.TransferEvent;
import com.dregost.moneytransfer.transfer.read.*;
import lombok.val;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Test
public class TransferApplicationServiceTest {
    private static final String TRANSFER_ID = "FAKE_ID";
    private final static String FROM_ACCOUNT_ID = "FROM_ACCOUNT_ID";
    private final static String TO_ACCOUNT_ID = "TO_ACCOUNT_ID";
    private final static BigDecimal AMOUNT = BigDecimal.valueOf(100);

    private IdGenerator fakeIdGenerator;
    private EventStore<TransferEvent> fakeEventStore;
    private TransferRepository fakeTransferResponseRepository;


    @BeforeClass
    @SuppressWarnings("unchecked")
    public void setUp() {
        fakeIdGenerator = mock(IdGenerator.class);
        fakeEventStore = mock(EventStore.class);
        fakeTransferResponseRepository = mock(TransferRepository.class);
    }

    public void createTransfer_whenInitialBalanceWasProvide_shouldOpenAccountWithInitialBalance() {
        val amount = BigDecimal.valueOf(100);
        when(fakeIdGenerator.generateId(any())).thenReturn(TransferId.of(TRANSFER_ID));
        val createTransferRequest = CreateTransferRequest.builder()
                .fromAccountId(FROM_ACCOUNT_ID)
                .toAccountId(TO_ACCOUNT_ID)
                .amount(amount)
                .build();
        val expectedResult = makeTransferResponse();
        val service = makeTransferApplicationService();

        val result = service.createTransfer(createTransferRequest);

        assertThat(result).isEqualTo(expectedResult);
    }

    public void getDetails_whenRepositoryDoesNotContainRequiredAccount_shouldReturnEmptyOptional() {
        val service = makeTransferApplicationService();

        val result = service.getDetails("MISSING_ID");

        assertThat(result).isEmpty();
    }

    public void getDetails_whenRepositoryContainsRequiredAccount_shouldReturnAccount() {
        val expectedResult = makeTransferResponse();
        when(fakeTransferResponseRepository.findById(TRANSFER_ID)).thenReturn(Optional.of(expectedResult));
        val service = makeTransferApplicationService();

        val result = service.getDetails(TRANSFER_ID);

        assertThat(result).contains(expectedResult);
    }

    private TransferResponse makeTransferResponse() {
        return TransferResponse.builder()
                .id(TRANSFER_ID)
                .fromAccountId(FROM_ACCOUNT_ID)
                .toAccountId(TO_ACCOUNT_ID)
                .amount(AMOUNT)
                .status(TransferStatus.PENDING)
                .build();
    }

    private TransferApplicationService makeTransferApplicationService() {
        return new TransferApplicationService(fakeIdGenerator, fakeEventStore, fakeTransferResponseRepository);
    }
}