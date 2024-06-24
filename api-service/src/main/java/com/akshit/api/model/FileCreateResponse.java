package com.akshit.api.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileCreateResponse {
    private String uploadId;
}