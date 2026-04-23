package com.al7afez.al7afez.service;

import com.al7afez.al7afez.infra.ObjectChecker;
import com.al7afez.al7afez.model.entities.BaseEntity;
import com.al7afez.al7afez.repositories.BaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class BaseService<E extends BaseEntity> {

    /**
     * Called before saving (create or update). Throw ResponseStatusException to reject.
     */
    protected void isValidForCommit(E entity) {
        if (entity == null) return;
        if (ObjectChecker.isEmptyOrNull(entity.getCode()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code is required");
    }

    /**
     * Called before deleting. Throw ResponseStatusException to reject.
     */
    protected void isValidForDelete(E entity) {
    }

    /**
     * Called after a successful save. Use for side-effects that must run post-persist.
     */
    protected void postCommitAction(E entity) {
    }

    /**
     * Called after apply() and before save. Use to recompute derived/denormalized fields.
     */
    protected void updateCalculatedFields(E entity) {
    }

    protected String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public E save(E entity, BaseRepository<E, Long> repository) {
        isValidForCommit(entity);
        updateCalculatedFields(entity);
        E savedEntity = repository.save(entity);
        postCommitAction(entity);
        return savedEntity;
    }
}