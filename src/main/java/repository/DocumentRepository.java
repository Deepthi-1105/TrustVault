
package com.trustvault.trustvault.repository;

import com.trustvault.trustvault.entity.Document;
import com.trustvault.trustvault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOwner(User owner);
}
