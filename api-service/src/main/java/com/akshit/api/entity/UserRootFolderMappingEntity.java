package com.akshit.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRootFolderMappingEntity {

    @Id
    private Long userId;

    @Column(nullable = false)
    private Long folderId;
}
