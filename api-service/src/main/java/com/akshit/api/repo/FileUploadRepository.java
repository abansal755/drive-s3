package com.akshit.api.repo;

import com.akshit.api.entity.FileUploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUploadEntity, Long> {
    public FileUploadEntity findFileUploadEntityById(Long id);
}
