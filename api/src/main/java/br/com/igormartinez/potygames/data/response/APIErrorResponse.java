package br.com.igormartinez.potygames.data.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record APIErrorResponse(
    String type,
    String title,
    Integer status,
    String detail,
    String instance,
    Map<String, String> errors
) {}

