package com.mydevduck.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "user")
@Entity
@Table(name = "pets", indexes = {
        @Index(name = "idx_pet_user_id", columnList = "user_id")
})
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pet_user"))
    @JsonIgnore
    private User user;

    @Column(nullable = false, length = 50)
    @Size(max = 50)
    private String name;

    @Column(nullable = false)
    @Min(0)
    @Max(100)
    private Integer health = 100;

    @Column(nullable = false)
    @Min(0)
    @Max(100)
    private Integer happiness = 100;

    @Column(nullable = false)
    @Min(0)
    @Max(100)
    private Integer hunger = 50;

    @Column(nullable = false)
    @Min(1)
    private Integer level = 1;

    @Column(nullable = false)
    @Min(0)
    private Integer xp = 0;

    @Column(name = "last_fed_at")
    private LocalDateTime lastFedAt;

    @Column(name = "last_played_at")
    private LocalDateTime lastPlayedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
