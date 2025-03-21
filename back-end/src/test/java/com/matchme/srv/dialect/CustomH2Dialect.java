package com.matchme.srv.dialect;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;

public class CustomH2Dialect extends H2Dialect {
  @Override
  protected void registerColumnTypes(
      TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
    // First, call the super implementation to register the default types
    super.registerColumnTypes(typeContributions, serviceRegistry);

    // Override the mapping for JSON (SqlTypes.JSON) to "clob" so that the "jsonb" columnDefinition
    // in your entity
    // is translated into a type H2 understands.
    typeContributions
        .getTypeConfiguration()
        .getDdlTypeRegistry()
        .addDescriptor(new DdlTypeImpl(SqlTypes.JSON, "clob", this));
  }
}
