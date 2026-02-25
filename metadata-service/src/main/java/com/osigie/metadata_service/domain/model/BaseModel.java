package com.osigie.metadata_service.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseModel {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createAt;

    @Column(updatable = true, name = "updated_at")
    private LocalDateTime updateAt;

    @PrePersist
    public void prePersist() {
        if (this.getId() == null) {
            this.setId(UUID.randomUUID());
        }
        this.createAt = LocalDateTime.now();
        this.updateAt = this.createAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}
