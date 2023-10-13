package com.mhafizsir.miniwalletservice.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralResponse<T> {

    private String status;
    private String message;
    private T data;

    public GeneralResponse(String message) {

        this.status = EStatus.FAIL.label;
        this.message = message;
    }

    public GeneralResponse(EStatus eStatus, T data) {
        this.status = eStatus.label;
        this.data = data;

    }

    public enum EStatus {
        SUCCESS("success"),
        FAIL("fail"),
        ERROR("error");

        public final String label;

        EStatus(String label) {
            this.label = label;
        }
    }
}
