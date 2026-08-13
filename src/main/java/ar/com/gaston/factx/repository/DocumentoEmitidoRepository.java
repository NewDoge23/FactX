package ar.com.gaston.factx.repository;

import ar.com.gaston.factx.domain.DocumentoEmitido;
import ar.com.gaston.factx.domain.EstadoDocumentoEmitido;
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

public class DocumentoEmitidoRepository {
    private final Jdbi jdbi;

    public DocumentoEmitidoRepository(Jdbi jdbi) {
        this.jdbi = Objects.requireNonNull(jdbi, "jdbi");
    }

    public DocumentoEmitido create(DocumentoEmitido documento) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                INSERT INTO documento_emitido (
                                    cliente_id, tipo, numero, fecha_emision, fecha_vencimiento,
                                    moneda, total, estado, notas
                                )
                                VALUES (
                                    :clienteId, :tipo, :numero, :fechaEmision, :fechaVencimiento,
                                    :moneda, :total, :estado, :notas
                                )
                                RETURNING id, cliente_id, tipo, numero, fecha_emision, fecha_vencimiento,
                                          moneda, total, estado, notas, created_at, updated_at
                                """)
                        .bind("clienteId", documento.clienteId())
                        .bind("tipo", documento.tipo().name())
                        .bind("numero", documento.numero())
                        .bind("fechaEmision", documento.fechaEmision())
                        .bind("fechaVencimiento", documento.fechaVencimiento())
                        .bind("moneda", documento.moneda())
                        .bind("total", documento.total())
                        .bind("estado", documento.estado().name())
                        .bind("notas", documento.notas())
                        .map(DocumentoEmitidoRepository::mapDocumento)
                        .one()
        );
    }

    public Optional<DocumentoEmitido> findById(long id) {
        return jdbi.withHandle(handle ->
                handle.createQuery(selectDocuments() + " WHERE id = :id")
                        .bind("id", id)
                        .map(DocumentoEmitidoRepository::mapDocumento)
                        .findOne()
        );
    }

    public List<DocumentoEmitido> findAll() {
        return jdbi.withHandle(handle ->
                handle.createQuery(selectDocuments() + " ORDER BY fecha_emision DESC NULLS LAST, id DESC")
                        .map(DocumentoEmitidoRepository::mapDocumento)
                        .list()
        );
    }

    public List<DocumentoEmitido> findByClienteId(long clienteId) {
        return jdbi.withHandle(handle ->
                handle.createQuery(selectDocuments() + " WHERE cliente_id = :clienteId ORDER BY fecha_emision DESC NULLS LAST, id DESC")
                        .bind("clienteId", clienteId)
                        .map(DocumentoEmitidoRepository::mapDocumento)
                        .list()
        );
    }

    public Optional<DocumentoEmitido> update(DocumentoEmitido documento) {
        requireId(documento.id());
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                UPDATE documento_emitido
                                SET cliente_id = :clienteId,
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
                                RETURNING id, cliente_id, tipo, numero, fecha_emision, fecha_vencimiento,
                                          moneda, total, estado, notas, created_at, updated_at
                                """)
                        .bind("id", documento.id())
                        .bind("clienteId", documento.clienteId())
                        .bind("tipo", documento.tipo().name())
                        .bind("numero", documento.numero())
                        .bind("fechaEmision", documento.fechaEmision())
                        .bind("fechaVencimiento", documento.fechaVencimiento())
                        .bind("moneda", documento.moneda())
                        .bind("total", documento.total())
                        .bind("estado", documento.estado().name())
                        .bind("notas", documento.notas())
                        .map(DocumentoEmitidoRepository::mapDocumento)
                        .findOne()
        );
    }

    public boolean delete(long id) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("DELETE FROM documento_emitido WHERE id = :id");
            return update.bind("id", id).execute() > 0;
        });
    }

    private static String selectDocuments() {
        return """
                SELECT id, cliente_id, tipo, numero, fecha_emision, fecha_vencimiento,
                       moneda, total, estado, notas, created_at, updated_at
                FROM documento_emitido
                """;
    }

    private static DocumentoEmitido mapDocumento(ResultSet rs, org.jdbi.v3.core.statement.StatementContext ctx)
            throws SQLException {
        return new DocumentoEmitido(
                rs.getLong("id"),
                rs.getLong("cliente_id"),
                TipoDocumento.fromDatabaseValue(rs.getString("tipo")),
                rs.getString("numero"),
                rs.getObject("fecha_emision", LocalDate.class),
                rs.getObject("fecha_vencimiento", LocalDate.class),
                rs.getString("moneda"),
                rs.getBigDecimal("total"),
                EstadoDocumentoEmitido.fromDatabaseValue(rs.getString("estado")),
                rs.getString("notas"),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("updated_at", OffsetDateTime.class)
        );
    }

    private static void requireId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot update issued document without id.");
        }
    }
}
