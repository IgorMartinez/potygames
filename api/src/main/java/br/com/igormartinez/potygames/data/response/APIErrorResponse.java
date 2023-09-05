package br.com.igormartinez.potygames.data.response;

import java.util.Map;

public record APIErrorResponse(
    String type,
    String title,
    Integer status,
    String detail,
    String instance,
    Map<String, String> errors
) {}

