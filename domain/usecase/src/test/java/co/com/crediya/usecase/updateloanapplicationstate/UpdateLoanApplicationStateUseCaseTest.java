package co.com.crediya.usecase.updateloanapplicationstate;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.gateways.AuthenticationGateway;
import co.com.crediya.model.loandetails.gateways.dto.UserByEmailDto;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.model.updateapplication.UpdateApplication;
import co.com.crediya.model.updateapplication.gateways.SQSSenderGateway;
import co.com.crediya.usecase.exception.ApplicationNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UpdateLoanApplicationStateUseCaseTest {
    @InjectMocks
    UpdateLoanApplicationStateUseCase updateLoanApplicationStateUseCase;

    @Mock
    LoanApplicationRepository loanApplicationRepository;
    @Mock
    AuthenticationGateway authenticationGateway;
    @Mock
    SQSSenderGateway sqsSenderGateway;
    @Mock
    LoanTypeRepository loanTypeRepository;

    private final UpdateApplication updateApplication = new UpdateApplication(
            2,
            "APPROVED"
    );

    @Test
    void shouldUpdateLoanApplicationState() {
        var application = LoanApplication.builder()
                .amount(new BigDecimal(1500000))
                .timeLimit(12)
                .email("juan@mail.com")
                .stateId(4)
                .loanTypeId(1)
                .build();

        var user = new UserByEmailDto(
                "juan",
                "gomez",
                "juan@mail.com",
                new BigDecimal(8500000)
        );

        var loanType = LoanType.builder()
                .name("libre inversión")
                .minAmount(new BigDecimal(500000))
                .maxAmount(new BigDecimal(10000000))
                .interestRate(new BigDecimal(2))
                .automaticValidation(false)
                .build();

        Mockito.when(loanApplicationRepository.findApplicationById(Mockito.anyInt()))
                        .thenReturn(Mono.just(application));

        Mockito.when(loanApplicationRepository.saveLoanApplication(Mockito.any(LoanApplication.class)))
                .thenReturn(Mono.just(application));

        Mockito.when(authenticationGateway.getUsersInformation(Mockito.anyString()))
                .thenReturn(Mono.just(List.of(user)));

        Mockito.when(loanTypeRepository.findById(Mockito.anyInt()))
                .thenReturn(Mono.just(loanType));

        Mockito.when(sqsSenderGateway.sendEmailQueue(Mockito.anyString()))
                .thenReturn(Mono.just("354tgfdas-24ref"));

        Mockito.when(sqsSenderGateway.sendApprovedReportQueue(Mockito.anyString()))
                .thenReturn(Mono.just("354tgfdas-24ref"));

        updateLoanApplicationStateUseCase.execute(updateApplication)
                .as(StepVerifier::create)
                .expectNext("OK")
                .verifyComplete();
    }

    @Test
    void shouldReturnApplicationNotFoundException() {
        Mockito.when(loanApplicationRepository.findApplicationById(Mockito.anyInt()))
                .thenReturn(Mono.empty());

        updateLoanApplicationStateUseCase.execute(updateApplication)
                .as(StepVerifier::create)
                .expectError(ApplicationNotFoundException.class)
                .verify();

    }

}