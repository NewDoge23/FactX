package ar.com.gaston.factx.service;

import ar.com.gaston.factx.domain.Cliente;
import ar.com.gaston.factx.repository.ClienteRepository;
import ar.com.gaston.factx.validation.ClienteValidator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "clienteRepository");
    }

    public Cliente create(String nombre, String razonSocial, String cuit, String notas) {
        ClienteValidator.validateForCreate(nombre);
        return clienteRepository.create(Cliente.create(nombre, razonSocial, cuit, notas));
    }

    public Optional<Cliente> findById(long id) {
        ClienteValidator.validateId(id);
        return clienteRepository.findById(id);
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> update(Long id, String nombre, String razonSocial, String cuit, String notas) {
        ClienteValidator.validateForUpdate(id, nombre);
        return clienteRepository.update(new Cliente(id, nombre, razonSocial, cuit, notas, null, null));
    }

    public boolean delete(long id) {
        ClienteValidator.validateId(id);
        return clienteRepository.delete(id);
    }
}
