package co.com.crediya.utils.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusResponse {
    OK("OK");

    private final String value;
}
