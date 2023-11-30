package org.giftorg.common.tokenpool;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class APITaskResult {
    private final Boolean success;
    private final Exception exception;

    public APITaskResult() {
        this.success = true;
        this.exception = null;
    }

    public APITaskResult(Exception exception) {
        this.success = false;
        this.exception = exception;
    }
}