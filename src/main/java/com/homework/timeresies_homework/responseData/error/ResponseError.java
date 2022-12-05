package com.homework.timeresies_homework.responseData.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseError {

    private int errorCode;
    private String errorMessage;
}
