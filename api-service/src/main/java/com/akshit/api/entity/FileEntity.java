package com.akshit.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long parentFolderId;

    @Column(nullable = false)
    private String name;

    private String extension;

    private String s3BucketName;

    private String s3ObjectKey;

    @Column(nullable = false)
    private Long createdAt;

    private Long sizeInBytes;
}
