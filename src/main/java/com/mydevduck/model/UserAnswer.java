package com.mydevduck.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "question"})
@EqualsAndHashCode(exclude = {"user", "question"})
@Entity
@Table(name = "user_answers", indexes = {
        @Index(name = "idx_user_answer_user_id", columnList = "user_id"),
        @Index(name = "idx_user_answer_question_id", columnList = "question_id"),
        @Index(name = "idx_user_answer_user_question", columnList = "user_id, question_id")
})
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_answer_user"))
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_answer_question"))
    @JsonIgnore
    private MCQQuestion question;

    @Column(name = "selected_answer", nullable = false)
    @Min(0)
    @Max(3)
    private Integer selectedAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @CreationTimestamp
    @Column(name = "answered_at", nullable = false, updatable = false)
    private LocalDateTime answeredAt;

    @PrePersist
    public void checkCorrectness() {
        if (isCorrect == null && question != null && selectedAnswer != null) {
            isCorrect = selectedAnswer.equals(question.getCorrectAnswer());
        }
    }

}
