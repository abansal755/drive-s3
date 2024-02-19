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
    private long parentFolderId;

    @Column(nullable = false)
    private String name;

    private String extension;

    @Column(nullable = false)
    private String s3BucketName;

    @Column(nullable = false)
    private String s3ObjectKey;

    @Column(nullable = false)
    private Long createdAt;
}
