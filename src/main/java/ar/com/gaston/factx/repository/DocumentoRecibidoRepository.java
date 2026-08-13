package ar.com.gaston.factx.repository;

import ar.com.gaston.factx.domain.DocumentoRecibido;
import ar.com.gaston.factx.domain.EstadoDocumentoRecibido;
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

public class DocumentoRecibidoRepository {
    private final Jdbi jdbi;

    public DocumentoRecibidoRepository(Jdbi jdbi) {
        this.jdbi = Objects.requireNonNull(jdbi, "jdbi");
    }

    public DocumentoRecibido create(DocumentoRecibido documento) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                INSERT INTO documento_recibido (
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
                        .map(DocumentoRecibidoRepository::mapDocumento)
                        .one()
        );
    }

    public Optional<DocumentoRecibido> findById(long id) {
        return jdbi.withHandle(handle ->
                handle.createQuery(selectDocuments() + " WHERE id = :id")
                        .bind("id", id)
                        .map(DocumentoRecibidoRepository::mapDocumento)
                        .findOne()
        );
    }

    public List<DocumentoRecibido> findAll() {
        return jdbi.withHandle(handle ->
                handle.createQuery(selectDocuments() + " ORDER BY fecha_emision DESC NULLS LAST, id DESC")
                        .map(DocumentoRecibidoRepository::mapDocumento)
                        .list()
        );
    }

    public List<DocumentoRecibido> findByProveedorId(long proveedorId) {
        return jdbi.withHandle(handle ->
                handle.createQuery(selectDocuments() + " WHERE proveedor_id = :proveedorId ORDER BY fecha_emision DESC NULLS LAST, id DESC")
                        .bind("proveedorId", proveedorId)
                        .map(DocumentoRecibidoRepository::mapDocumento)
                        .list()
        );
    }

    public Optional<DocumentoRecibido> update(DocumentoRecibido documento) {
        requireId(documento.id());
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                UPDATE documento_recibido
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
                        .bind("id", documento.id())
                        .bind("proveedorId", documento.proveedorId())
                        .bind("tipo", documento.tipo().name())
                        .bind("numero", documento.numero())
                        .bind("fechaEmision", documento.fechaEmision())
                        .bind("fechaVencimiento", documento.fechaVencimiento())
                        .bind("moneda", documento.moneda())
                        .bind("total", documento.total())
                        .bind("estado", documento.estado().name())
                        .bind("notas", documento.notas())
                        .map(DocumentoRecibidoRepository::mapDocumento)
                        .findOne()
        );
    }

    public boolean delete(long id) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("DELETE FROM documento_recibido WHERE id = :id");
            return update.bind("id", id).execute() > 0;
        });
    }

    private static String selectDocuments() {
        return """
                SELECT id, proveedor_id, tipo, numero, fecha_emision, fecha_vencimiento,
                       moneda, total, estado, notas, created_at, updated_at
                FROM documento_recibido
                """;
    }

    private static DocumentoRecibido mapDocumento(ResultSet rs, org.jdbi.v3.core.statement.StatementContext ctx)
            throws SQLException {
        return new DocumentoRecibido(
                rs.getLong("id"),
                rs.getLong("proveedor_id"),
                TipoDocumento.fromDatabaseValue(rs.getString("tipo")),
                rs.getString("numero"),
                rs.getObject("fecha_emision", LocalDate.class),
                rs.getObject("fecha_vencimiento", LocalDate.class),
                rs.getString("moneda"),
                rs.getBigDecimal("total"),
                EstadoDocumentoRecibido.fromDatabaseValue(rs.getString("estado")),
                rs.getString("notas"),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("updated_at", OffsetDateTime.class)
        );
    }

    private static void requireId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot update received document without id.");
        }
    }
}
