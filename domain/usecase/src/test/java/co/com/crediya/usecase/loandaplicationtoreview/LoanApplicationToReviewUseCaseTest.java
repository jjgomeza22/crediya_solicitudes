package co.com.crediya.usecase.loandaplicationtoreview;

import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.model.loandetails.gateways.UsersByEmailGateway;
import co.com.crediya.model.loandetails.gateways.dto.UserByEmailDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LoanApplicationToReviewUseCaseTest {
    @InjectMocks
    LoanApplicationToReviewUseCase loanApplicationToReviewUseCase;

    @Mock
    LoanApplicationRepository loanApplicationRepository;

    @Mock
    UsersByEmailGateway usersByEmailGateway;

    @Test
    void shouldGetLoanApplicationDetails() {
        var loanDetail = new LoanDetails(
                new BigDecimal(1000000),
                48,
                "juan@mail.com",
                "APROBADO",
                "Crédito de consumo",
                new BigDecimal("0.0178000")
        );

        var user = new UserByEmailDto(
                "juan",
                "gomez",
                "juan@mail.com",
                new BigDecimal(8500000)
        );

        Mockito.when(loanApplicationRepository.getPendingLoanApplications(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(List.class)))
                .thenReturn(Flux.just(loanDetail));
        Mockito.when(usersByEmailGateway.getUsersInformation(Mockito.anyString())).thenReturn(Mono.just(List.of(user)));

        loanApplicationToReviewUseCase.execute(5, 0, List.of("APPROVED"))
                .as(StepVerifier::create)
                .expectNextMatches(loanUser -> loanUser.totalMonthlyDebt().compareTo(new BigDecimal("31159.65")) == 0)
                .verifyComplete();

    }

}