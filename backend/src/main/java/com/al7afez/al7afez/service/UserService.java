package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.ChangePasswordRequest;
import com.al7afez.al7afez.dto.UserRequest;
import com.al7afez.al7afez.dto.UserResponse;
import com.al7afez.al7afez.infra.ObjectChecker;
import com.al7afez.al7afez.model.entities.AppUser;
import com.al7afez.al7afez.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService extends AbsMasterFileService<AppUser> {
    private final UserRepository repository;
    private final MappingService mappingService;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository repository, MappingService mappingService, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.mappingService = mappingService;
        this.encoder = encoder;
    }

    public Page<UserResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mappingService::toUserResponse);
    }

    public UserResponse getById(Long id) {
        return mappingService.toUserResponse(findOrThrow(id));
    }

    public UserResponse create(UserRequest request) {
        if (repository.existsByUsername(request.username()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        AppUser user = new AppUser();
        mappingService.toUser(user, request);
        user.setPassword(encoder.encode(request.password()));
        return mappingService.toUserResponse(save(user, repository));
    }

    public UserResponse update(Long id, UserRequest request) {
        AppUser user = findOrThrow(id);
        if (repository.existsByUsernameAndIdNot(request.username(), id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        mappingService.toUser(user, request);
        return mappingService.toUserResponse(save(user, repository));
    }

    @Override
    protected void isValidForCommit(AppUser entity) {
        super.isValidForCommit(entity);
        if (ObjectChecker.isEmptyOrNull(entity.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        if (ObjectChecker.isEmptyOrNull(entity.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
    }

    public void changePassword(Long id, ChangePasswordRequest request) {
        AppUser user = findOrThrow(id);
        user.setPassword(encoder.encode(request.password()));
        repository.save(user);
    }

    public void delete(Long id) {
        AppUser user = findOrThrow(id);
        isValidForDelete(user);
        repository.delete(user);
    }

    private AppUser findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}