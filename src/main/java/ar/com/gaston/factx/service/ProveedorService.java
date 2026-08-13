package ar.com.gaston.factx.service;

import ar.com.gaston.factx.domain.Proveedor;
import ar.com.gaston.factx.repository.ProveedorRepository;
import ar.com.gaston.factx.validation.ProveedorValidator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ProveedorService {
    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = Objects.requireNonNull(proveedorRepository, "proveedorRepository");
    }

    public Proveedor create(String nombre, String cuit, String notas) {
        ProveedorValidator.validateForCreate(nombre);
        return proveedorRepository.create(Proveedor.create(nombre, cuit, notas));
    }

    public Optional<Proveedor> findById(long id) {
        ProveedorValidator.validateId(id);
        return proveedorRepository.findById(id);
    }

    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor> update(Long id, String nombre, String cuit, String notas) {
        ProveedorValidator.validateForUpdate(id, nombre);
        return proveedorRepository.update(new Proveedor(id, nombre, cuit, notas, null, null));
    }

    public boolean delete(long id) {
        ProveedorValidator.validateId(id);
        return proveedorRepository.delete(id);
    }
}
