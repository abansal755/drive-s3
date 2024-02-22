package com.akshit.api.repo;

import com.akshit.api.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    public List<FileEntity> findAllByParentFolderId(Long id);
    public FileEntity findFileEntityById(Long id);
}
