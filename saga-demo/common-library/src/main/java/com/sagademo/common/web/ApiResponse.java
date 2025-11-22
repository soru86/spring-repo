package com.sagademo.common.web;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiResponse<T> {
    boolean success;
    String message;
    T data;
}

