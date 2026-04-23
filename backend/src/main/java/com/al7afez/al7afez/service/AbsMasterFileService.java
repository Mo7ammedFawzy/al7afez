package com.al7afez.al7afez.service;

import com.al7afez.al7afez.infra.ObjectChecker;
import com.al7afez.al7afez.model.entities.MasterFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class AbsMasterFileService<E extends MasterFile> extends BaseService<E> {
    @Override
    protected void isValidForCommit(E entity) {
        if (entity == null) return;
        super.isValidForCommit(entity);
        if (ObjectChecker.isEmptyOrNull(entity.getName()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");

    }
}
