package ar.com.gaston.factx.repository;

import ar.com.gaston.factx.domain.Documento;
import ar.com.gaston.factx.domain.EstadoDocumento;
import ar.com.gaston.factx.domain.TipoDocumento;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Update;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DocumentoRepository {
    private final Jdbi jdbi;

    public DocumentoRepository(Jdbi jdbi) {
        this.jdbi = Objects.requireNonNull(jdbi, "jdbi");
    }

    public Documento create(Documento documento) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                INSERT INTO documento (
                                    proveedor_id, tipo, numero, fecha_emision, fecha_vencimiento,
                                    moneda, total, estado, notas
                                )
                                VALUES (
                                    :proveedorId, :tipo, :numero, :fechaEmision, :fechaVencimiento,
                                    :moneda, :total, :estado, :notas
                                )
                                RETURNING id, proveedor_id, tipo, numero, fecha_emision, fecha_vencimiento,
                                          moneda, total, estado, notas, created_at, updated_at
                                """)
                        .bind("proveedorId", documento.proveedorId())
                        .bind("tipo", documento.tipo().name())
                        .bind("numero", documento.numero())
                        .bind("fechaEmision", documento.fechaEmision())
                        .bind("fechaVencimiento", documento.fechaVencimiento())
                        .bind("moneda", documento.moneda())
                        .bind("total", documento.total())
                        .bind("estado", documento.estado().name())
                        .bind("notas", documento.notas())
                        .map(DocumentoRepository::mapDocumento)
                        .one()
        );
    }

    public Optional<Documento> findById(long id) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id, proveedor_id, tipo, numero, fecha_emision, fecha_vencimiento,
                                       moneda, total, estado, notas, created_at, updated_at
                                FROM documento
                                WHERE id = :id
                                """)
                        .bind("id", id)
                        .map(DocumentoRepository::mapDocumento)
                        .findOne()
        );
    }

    public List<Documento> findAll() {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id, proveedor_id, tipo, numero, fecha_emision, fecha_vencimiento,
                                       moneda, total, estado, notas, created_at, updated_at
                                FROM documento
                                ORDER BY fecha_emision DESC NULLS LAST, id DESC
                                """)
                        .map(DocumentoRepository::mapDocumento)
                        .list()
        );
    }

    public List<Documento> findByProveedorId(long proveedorId) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id, proveedor_id, tipo, numero, fecha_emision, fecha_vencimiento,
                                       moneda, total, estado, notas, created_at, updated_at
                                FROM documento
                                WHERE proveedor_id = :proveedorId
                                ORDER BY fecha_emision DESC NULLS LAST, id DESC
                                """)
                        .bind("proveedorId", proveedorId)
                        .map(DocumentoRepository::mapDocumento)
                        .list()
        );
    }

    public Optional<Documento> update(Documento documento) {
        requireId(documento.id(), "document");
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                UPDATE documento
                                SET proveedor_id = :proveedorId,
                                    tipo = :tipo,
                                    numero = :numero,
                                    fecha_emision = :fechaEmision,
                                    fecha_vencimiento = :fechaVencimiento,
                                    moneda = :moneda,
                                    total = :total,
                                    estado = :estado,
                                    notas = :notas,
                                    updated_at = now()
                                WHERE id = :id
                                RETURNING id, proveedor_id, tipo, numero, fecha_emision, fecha_vencimiento,
                                          moneda, total, estado, notas, created_at, updated_at
                                """)
                        .bind("proveedorId", documento.proveedorId())
                        .bind("tipo", documento.tipo().name())
                        .bind("numero", documento.numero())
                        .bind("fechaEmision", documento.fechaEmision())
                        .bind("fechaVencimiento", documento.fechaVencimiento())
                        .bind("moneda", documento.moneda())
                        .bind("total", documento.total())
                        .bind("estado", documento.estado().name())
                        .bind("notas", documento.notas())
                        .bind("id", documento.id())
                        .map(DocumentoRepository::mapDocumento)
                        .findOne()
        );
    }

    public boolean delete(long id) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("DELETE FROM documento WHERE id = :id");
            return update.bind("id", id).execute() > 0;
        });
    }

    private static Documento mapDocumento(ResultSet rs, org.jdbi.v3.core.statement.StatementContext ctx)
            throws SQLException {
        return new Documento(
                rs.getLong("id"),
                rs.getLong("proveedor_id"),
                TipoDocumento.fromDatabaseValue(rs.getString("tipo")),
                rs.getString("numero"),
                rs.getObject("fecha_emision", LocalDate.class),
                rs.getObject("fecha_vencimiento", LocalDate.class),
                rs.getString("moneda"),
                rs.getBigDecimal("total"),
                EstadoDocumento.fromDatabaseValue(rs.getString("estado")),
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
