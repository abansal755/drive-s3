package com.akshit.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AncestorsResponse {
    private List<Folder> ancestors;
}
