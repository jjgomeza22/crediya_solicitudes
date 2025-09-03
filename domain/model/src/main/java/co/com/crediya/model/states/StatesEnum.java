package co.com.crediya.model.states;

import lombok.Getter;

@Getter
public enum StatesEnum {
    APPROVED(1),
    DECLINE(2),
    PRE_APPROVED(3),
    PE_REVIEW(4);

    private final int stateId;

    StatesEnum(int stateId) {
        this.stateId = stateId;
    }
}
