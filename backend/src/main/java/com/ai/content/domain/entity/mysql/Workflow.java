package com.ai.content.domain.entity.mysql;

import com.ai.content.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Workflow Entity Represents a workflow configuration for AI content processing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "workflow")
public class Workflow extends BaseEntity {

    /**
     * Workflow name
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Workflow description
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Whether the workflow is enabled
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /**
     * Soft delete flag
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    /**
     * Creator username
     */
    @Column(name = "creator", length = 100)
    private String creator;

    /**
     * Last editor username
     */
    @Column(name = "editor", length = 100)
    private String editor;
}
