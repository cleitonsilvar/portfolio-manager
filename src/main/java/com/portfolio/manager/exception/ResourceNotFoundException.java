package com.portfolio.manager.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " com id " + id + " não encontrado", 404);
    }
}
