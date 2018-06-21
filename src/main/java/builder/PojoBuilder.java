package builder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import common.Convertor;

public class PojoBuilder {

    private TableAccess tableAccess;
    private String tableName;

    public PojoBuilder(final TableAccess tableAccess, final String tableName) {
        this.tableAccess = tableAccess;
        this.tableName = tableName;
    }

    public PojoBuilder(final TableAccess tableAccess) {
        this(tableAccess, "");
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    // public void build() throws IOException, ClassNotFoundException {
    //
    // TableAccess access = new TableAccess();
    // List<Pair<Object, Object>> p = access.getTableColumnAndType();
    // System.out.println(p);
    //
    // Builder pojoBuilder =
    // TypeSpec.classBuilder("Test").addModifiers(Modifier.PUBLIC);
    // p.forEach(fieldInfo -> builderAddFieldInfo(fieldInfo, pojoBuilder));
    // TypeSpec pojo = pojoBuilder.build();
    //
    // JavaFile javaFile = JavaFile.builder("com.example.demo", pojo).build();
    // javaFile.writeTo(System.out);
    //
    // }

    public void writeTo(final Writer out) throws IOException, ClassNotFoundException {
        try {
            Builder pojoBuilder = TypeSpec.classBuilder("Test").addModifiers(Modifier.PUBLIC);

            tableAccess.getTableColumnAndType(tableName)
            .forEach(fieldInfo -> builderAddFieldInfo(fieldInfo, pojoBuilder));

            TypeSpec pojo = pojoBuilder.build();

            JavaFile javaFile = JavaFile.builder("com.example.demo", pojo).build();
            javaFile.writeTo(out);

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(out != null) {
                out.close();
            }
        }
    }

    public void writeTo(final OutputStream out) throws IOException, ClassNotFoundException {
        writeTo(new OutputStreamWriter(out));
    }

    public String renderToString() throws IOException, ClassNotFoundException {
        StringWriter writer = new StringWriter();
        writeTo(writer);

        return writer.toString();
    }

    public FieldSpec defineField(TableMetadata tm) throws ClassNotFoundException {

        Class<?> fieldType = Class.forName(tm.getClassName());
        String fieldName = Convertor.snakeCase2CamelCase(tm.getFieldName());
        FieldSpec fieldSpec = FieldSpec.builder(fieldType, fieldName).addModifiers(Modifier.PRIVATE).build();

        return fieldSpec;

    }

    public MethodSpec defineGetter(TableMetadata tm) throws ClassNotFoundException {

        Class<?> returnType = Class.forName(tm.getClassName());
        String fieldName = Convertor.snakeCase2CamelCase(tm.getFieldName());
        String methodName = "get" + Convertor.firstCharUpperConvert(fieldName);
        MethodSpec methodSpec = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(returnType)
                .addStatement("return $N", fieldName).build();

        return methodSpec;

    }

    public MethodSpec defineSetter(TableMetadata tm) throws ClassNotFoundException {

        Class<?> returnType = Class.forName(tm.getClassName());
        String fieldName = Convertor.snakeCase2CamelCase(tm.getFieldName());
        String methodName = "set" + Convertor.firstCharUpperConvert(fieldName);
        ParameterSpec parameterSpec = ParameterSpec.builder(returnType, fieldName).build();
        MethodSpec methodSpec = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec).addStatement("this.$N = $N", fieldName, fieldName).build();

        return methodSpec;

    }

    public void builderAddFieldInfo(TableMetadata fieldInfo, Builder instance) {
        try {
            Class<?> clazz = Class.forName("com.squareup.javapoet.TypeSpec$Builder");
            Method fieldAdd = clazz.getMethod("addField", FieldSpec.class);
            fieldAdd.invoke(instance, defineField(fieldInfo));
            Method getterAdd = clazz.getMethod("addMethod", MethodSpec.class);
            getterAdd.invoke(instance, defineGetter(fieldInfo));
            Method setterAdd = clazz.getMethod("addMethod", MethodSpec.class);
            setterAdd.invoke(instance, defineSetter(fieldInfo));

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

}
