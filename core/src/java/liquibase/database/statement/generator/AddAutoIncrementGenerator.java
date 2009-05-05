package liquibase.database.statement.generator;

import liquibase.database.*;
import liquibase.database.structure.Column;
import liquibase.database.structure.Table;
import liquibase.database.statement.AddAutoIncrementStatement;
import liquibase.database.statement.syntax.Sql;
import liquibase.database.statement.syntax.UnparsedSql;

public class AddAutoIncrementGenerator implements SqlGenerator<AddAutoIncrementStatement> {

    public int getSpecializationLevel() {
        return SPECIALIZATION_LEVEL_DEFAULT;
    }

    public boolean isValidGenerator(AddAutoIncrementStatement statement, Database database) {
        return (database.supportsAutoIncrement()
                && !(database instanceof DerbyDatabase)
                && !(database instanceof MSSQLDatabase)
                && !(database instanceof HsqlDatabase));
    }

    public GeneratorValidationErrors validate(AddAutoIncrementStatement statement, Database database) {
        GeneratorValidationErrors validationErrors = new GeneratorValidationErrors();
        if (database instanceof MSSQLDatabase) {
            validationErrors.addError("Cannot make the column as auto-increment.");
        }
        return validationErrors;
    }

    public Sql[] generateSql(AddAutoIncrementStatement statement, Database database) {
        String sql = "ALTER TABLE "
                + database.escapeTableName(statement.getSchemaName(), statement.getTableName())
                + " MODIFY " + database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), statement.getColumnName())
                + " " + database.getColumnType(statement.getColumnDataType(), true)
                + " AUTO_INCREMENT";

        return new Sql[]{
                new UnparsedSql(sql, new Column()
                        .setTable(new Table(statement.getTableName()).setSchema(statement.getSchemaName()))
                        .setName(statement.getColumnName()))
        };
    }
}