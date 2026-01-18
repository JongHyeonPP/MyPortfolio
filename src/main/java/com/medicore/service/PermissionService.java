package com.medicore.service;

import com.medicore.domain.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final RolePermissionRepository permissionRepository;

    public boolean hasPermission(Long roleId, String resource, String action) {
        return permissionRepository.existsByRoleIdAndResourceAndAction(roleId, resource, action);
    }
}
