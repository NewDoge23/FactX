package ar.com.gaston.factx.repository;

import ar.com.gaston.factx.domain.Cliente;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Update;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClienteRepository {
    private final Jdbi jdbi;

    public ClienteRepository(Jdbi jdbi) {
        this.jdbi = Objects.requireNonNull(jdbi, "jdbi");
    }

    public Cliente create(Cliente cliente) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                INSERT INTO cliente (nombre, razon_social, cuit, notas)
                                VALUES (:nombre, :razonSocial, :cuit, :notas)
                                RETURNING id, nombre, razon_social, cuit, notas, created_at, updated_at
                                """)
                        .bind("nombre", cliente.nombre())
                        .bind("razonSocial", cliente.razonSocial())
                        .bind("cuit", cliente.cuit())
                        .bind("notas", cliente.notas())
                        .map(ClienteRepository::mapCliente)
                        .one()
        );
    }

    public Optional<Cliente> findById(long id) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id, nombre, razon_social, cuit, notas, created_at, updated_at
                                FROM cliente
                                WHERE id = :id
                                """)
                        .bind("id", id)
                        .map(ClienteRepository::mapCliente)
                        .findOne()
        );
    }

    public List<Cliente> findAll() {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id, nombre, razon_social, cuit, notas, created_at, updated_at
                                FROM cliente
                                ORDER BY nombre, id
                                """)
                        .map(ClienteRepository::mapCliente)
                        .list()
        );
    }

    public Optional<Cliente> update(Cliente cliente) {
        requireId(cliente.id());
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                UPDATE cliente
                                SET nombre = :nombre,
                                    razon_social = :razonSocial,
                                    cuit = :cuit,
                                    notas = :notas,
                                    updated_at = now()
                                WHERE id = :id
                                RETURNING id, nombre, razon_social, cuit, notas, created_at, updated_at
                                """)
                        .bind("id", cliente.id())
                        .bind("nombre", cliente.nombre())
                        .bind("razonSocial", cliente.razonSocial())
                        .bind("cuit", cliente.cuit())
                        .bind("notas", cliente.notas())
                        .map(ClienteRepository::mapCliente)
                        .findOne()
        );
    }

    public boolean delete(long id) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("DELETE FROM cliente WHERE id = :id");
            return update.bind("id", id).execute() > 0;
        });
    }

    private static Cliente mapCliente(ResultSet rs, org.jdbi.v3.core.statement.StatementContext ctx)
            throws SQLException {
        return new Cliente(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("razon_social"),
                rs.getString("cuit"),
                rs.getString("notas"),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("updated_at", OffsetDateTime.class)
        );
    }

    private static void requireId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot update customer without id.");
        }
    }
}
