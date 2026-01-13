package com.client.ws.rasmooplus.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Data
@AllArgsConstructor
@Builder
public class ResponseErrorMessageDto {

    private String message;
    private HttpStatus httpStatus;
    private Integer statusCode;
}
