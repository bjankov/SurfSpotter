package hr.algebra.surfspot.service.impl;

import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.repository.InstructorRepository;
import hr.algebra.surfspot.service.InstructorService;
import java.util.List;
import java.util.Optional;

public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorServiceImpl(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Override
    public List<Instructor> findAll() {
        return instructorRepository.findAll();
    }

    @Override
    public Optional<Instructor> findById(Long id) {
        return instructorRepository.findById(id);
    }

    @Override
    public Instructor save(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    @Override
    public void update(Instructor instructor) {
        instructorRepository.update(instructor);
    }

    @Override
    public void delete(Long id) {
        instructorRepository.delete(id);
    }
}