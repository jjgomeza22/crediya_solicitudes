package co.com.crediya.api.validator;

import co.com.crediya.api.dto.UpdateApplicationStateDTO;
import co.com.crediya.api.exception.ApplicationExceptions;
import co.com.crediya.model.states.StatesEnum;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class UpdateApplicationStateValidator {
    public static UnaryOperator<Mono<UpdateApplicationStateDTO>> validate() {
        return mono -> mono
                .filter(invalidState())
                .switchIfEmpty(ApplicationExceptions.invalidState());
    }

    private static Predicate<UpdateApplicationStateDTO> invalidState() {
        return dto -> Objects.nonNull(dto.state()) && (
                StatesEnum.APPROVED.toString().equals(dto.state()) ||
                StatesEnum.DECLINE.toString().equals(dto.state()) ||
                StatesEnum.PRE_APPROVED.toString().equals(dto.state())
        );
    }
}
