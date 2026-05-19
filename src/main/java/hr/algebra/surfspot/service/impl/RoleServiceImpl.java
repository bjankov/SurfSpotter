package hr.algebra.surfspot.service.impl;

import hr.algebra.surfspot.model.Role;
import hr.algebra.surfspot.repository.RoleRepository;
import hr.algebra.surfspot.service.RoleService;

import java.util.List;
import java.util.Optional;

public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void update(Role role) {
        roleRepository.update(role);
    }

    @Override
    public void delete(Long id) {
        roleRepository.delete(id);
    }
}
