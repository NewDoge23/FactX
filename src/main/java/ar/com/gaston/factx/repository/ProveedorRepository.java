package ar.com.gaston.factx.repository;

import ar.com.gaston.factx.domain.Proveedor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Update;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProveedorRepository {
    private final Jdbi jdbi;

    public ProveedorRepository(Jdbi jdbi) {
        this.jdbi = Objects.requireNonNull(jdbi, "jdbi");
    }

    public Proveedor create(Proveedor proveedor) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                INSERT INTO proveedor (nombre, cuit, notas)
                                VALUES (:nombre, :cuit, :notas)
                                RETURNING id, nombre, cuit, notas, created_at, updated_at
                                """)
                        .bind("nombre", proveedor.nombre())
                        .bind("cuit", proveedor.cuit())
                        .bind("notas", proveedor.notas())
                        .map(ProveedorRepository::mapProveedor)
                        .one()
        );
    }

    public Optional<Proveedor> findById(long id) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id, nombre, cuit, notas, created_at, updated_at
                                FROM proveedor
                                WHERE id = :id
                                """)
                        .bind("id", id)
                        .map(ProveedorRepository::mapProveedor)
                        .findOne()
        );
    }

    public List<Proveedor> findAll() {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id, nombre, cuit, notas, created_at, updated_at
                                FROM proveedor
                                ORDER BY nombre, id
                                """)
                        .map(ProveedorRepository::mapProveedor)
                        .list()
        );
    }

    public Optional<Proveedor> update(Proveedor proveedor) {
        requireId(proveedor.id(), "supplier");
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                UPDATE proveedor
                                SET nombre = :nombre,
                                    cuit = :cuit,
                                    notas = :notas,
                                    updated_at = now()
                                WHERE id = :id
                                RETURNING id, nombre, cuit, notas, created_at, updated_at
                                """)
                        .bind("id", proveedor.id())
                        .bind("nombre", proveedor.nombre())
                        .bind("cuit", proveedor.cuit())
                        .bind("notas", proveedor.notas())
                        .map(ProveedorRepository::mapProveedor)
                        .findOne()
        );
    }

    public boolean delete(long id) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("DELETE FROM proveedor WHERE id = :id");
            return update.bind("id", id).execute() > 0;
        });
    }

    private static Proveedor mapProveedor(ResultSet rs, org.jdbi.v3.core.statement.StatementContext ctx)
            throws SQLException {
        return new Proveedor(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("cuit"),
                rs.getString("notas"),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("updated_at", OffsetDateTime.class)
        );
    }

    private static void requireId(Long id, String entityName) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot update " + entityName + " without id.");
        }
    }
}
