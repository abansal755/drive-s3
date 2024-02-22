package com.akshit.api.model;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileCreateResponse {
    private Long uploadId;
}