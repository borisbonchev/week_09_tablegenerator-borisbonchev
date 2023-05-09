package io.github.fontysvenlo.tablegenerator;

import io.github.fontysvenlo.annotations.Default;
import io.github.fontysvenlo.annotations.NotNull;
import io.github.fontysvenlo.annotations.Check;
import io.github.fontysvenlo.annotations.ID;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.joining;

/**
 * Create table defintion for a class.
 * @author Pieter van den Hombergh {@code pieter.van.den.hombergh@gmail.com}
 * @param <E> type type of the entity
 */
public class PGTableCreator<E> implements TableCreator<E> {

    final Class<E> entityType;

    public PGTableCreator( Class<E> entityType ) {
        this.entityType = entityType;
    }

    /**
     * Create the table definition from the entityType
     *
     * @param out The Appendable where all lines of the table definition should be appended to
     * @throws IOException
     */
    @Override
    public void createTable( Appendable out ) throws IOException {
            //TODO start with CREATE TABLE definition
            String createTable = "CREATE TABLE " + entityType.getSimpleName().toLowerCase() + 's' + "\n";
            out.append(createTable);

            //TODO get all the fields of the entity
            Field[] declaredFields = entityType.getDeclaredFields();
            List<String> outLines = new ArrayList<>();
            for ( Field field : declaredFields ) {
                StringBuilder ol = new StringBuilder();
                String pgType = TypeMappings.getPGTypeName( field.getType() );
                String declaration = processAnnotations( pgType, field );
                ol.append( "    " )
                        .append(field.getName())
                        .append(" ")
                        .append( declaration );
                outLines.add( ol.toString() );
            }

            String body = outLines.stream().collect( joining( "," + System.lineSeparator() ) );
            out.append( body )
                    .append( System.lineSeparator() )
                    .append( ");" )
                    .append( System.lineSeparator() )
                    .append( System.lineSeparator() );
    }


    /**
     * Process the name and annotations on the field to a PostgreSQL column definition.
     *
     * @param pgTypeName name of the field/ column
     * @param field the java field definition, which may have annotations to be processed.
     * @return the column definition
     */
    String processAnnotations( String pgTypeName, Field field ) {
        //TODO implement method processAnnotations
        StringBuilder sb = new StringBuilder();
        Annotation[] annotations = field.getAnnotations();

        if (annotations.length == 0) {
            sb.append(pgTypeName);
            return sb.toString();
        }

        for (Annotation annotation:
             annotations) {
            Type type = annotation.annotationType();

            if (type == ID.class) {
                sb.append("SERIAL PRIMARY KEY");
            } if (type == NotNull.class) {
                sb.append(pgTypeName).append(" NOT NULL");
            }

            return sb.toString();
        }

        return null;
    }
}
