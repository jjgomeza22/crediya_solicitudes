package co.com.crediya.api.dto;

import co.com.crediya.model.states.StatesEnum;

public record UpdateApplicationStateDTO(
        StatesEnum state
) {
}
