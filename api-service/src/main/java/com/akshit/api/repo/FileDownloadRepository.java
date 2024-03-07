package com.akshit.api.repo;

import com.akshit.api.entity.FileDownloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDownloadRepository extends JpaRepository<FileDownloadEntity, Long> {
    public FileDownloadEntity findFileDownloadEntityById(Long id);
}
