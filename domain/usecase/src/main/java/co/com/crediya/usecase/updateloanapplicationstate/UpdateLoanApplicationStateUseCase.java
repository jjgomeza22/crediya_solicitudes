package co.com.crediya.usecase.updateloanapplicationstate;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.gateways.AuthenticationGateway;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.model.updateapplication.UpdateApplication;
import co.com.crediya.model.updateapplication.gateways.SQSSenderGateway;
import co.com.crediya.usecase.IUseCaseMono;
import co.com.crediya.usecase.exception.ApplicationExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Optional;

@RequiredArgsConstructor
public class UpdateLoanApplicationStateUseCase implements IUseCaseMono<UpdateApplication, String> {
    private final LoanApplicationRepository loanApplicationRepository;
    private final AuthenticationGateway authenticationGateway;
    private final SQSSenderGateway sqsSenderGateway;
    private final LoanTypeRepository loanTypeRepository;

    @Override
    public Mono<String> execute(UpdateApplication request) {
        return loanApplicationRepository.findApplicationById(request.getId())
                .switchIfEmpty(ApplicationExceptions.applicationNotFound(request.getId()))
                .doOnNext(la -> la.setStateId(StatesEnum.valueOf(request.getState()).getStateId()))
                .flatMap(la -> loanApplicationRepository.saveLoanApplication(la)
                        .zipWith(findUserAndLoanTypeToSend(la, request.getState()))
                )
                .map(Tuple2::getT1);
    }

    private Mono<String> findUserAndLoanTypeToSend(LoanApplication la, String state) {
        return authenticationGateway.getUsersInformation(la.getEmail())
                .zipWith(loanTypeRepository.findById(la.getLoanTypeId()))
                .flatMap(tuple -> {
                    var users = tuple.getT1();
                    var loanType = tuple.getT2();

                    var username = Optional.ofNullable(users.get(0).name()).orElse("user");
                    return sendSqsMessage(la.getEmail(), username, state, loanType.getName());
                });
    }

    private Mono<String> sendSqsMessage(String email, String name, String state, String loanType) {
        String message = String.format(
                "{\"email\": \"%s\", \"name\": \"%s\", \"state\": \"%s\", \"loanType\": \"%s\"}",
                email,
                name,
                state,
                loanType
        );
        return sqsSenderGateway.sendEmailQueue(message);
    }
}
