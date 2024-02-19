package com.akshit.api.repo;

import com.akshit.api.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<FolderEntity, Long> {
    public FolderEntity findFolderEntityById(Long id);
    public List<FolderEntity> findAllByParentFolderId(Long id);
    public FolderEntity findByFolderNameAndParentFolderId(String folderName, Long parentFolderId);
}
