package co.com.crediya.model.states;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatesEnum {
    APPROVED(1, "APROBADO"),
    DECLINE(2, "RECHAZADO"),
    PRE_APPROVED(3, "PRE_APROBADO"),
    PE_REVIEW(4, "PE_REVISION");

    private final int stateId;
    private final String name;
}
