package builder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.lang.model.element.Modifier;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

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

    /**
     * 与えられたWriterインスタンスに生成したJavaコード文字列を出力する.
     * 出力後、Writerインスタンスのclose()を呼ぶ.
     * 
     * @param writer Writer
     */
    public void writeTo(final Writer out) throws IOException, ClassNotFoundException {

        try {
            Builder pojoBuilder = TypeSpec.classBuilder("Test").addModifiers(Modifier.PUBLIC);
    
            tableAccess.getTableColumnAndType(tableName)
                .forEach(fieldInfo -> builderAddFieldInfo(fieldInfo, pojoBuilder));
    
            TypeSpec pojo = pojoBuilder.build();
    
            JavaFile javaFile = JavaFile.builder("com.example.demo", pojo).build();
            javaFile.writeTo(out);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) {
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
        String fieldName = CaseFormat.LOWER_UNDERSCORE.to(
            CaseFormat.LOWER_CAMEL, tm.getFieldName());
        FieldSpec fieldSpec = FieldSpec.builder(fieldType, fieldName).addModifiers(Modifier.PRIVATE).build();

        return fieldSpec;
    }

    public MethodSpec defineGetter(TableMetadata tm) throws ClassNotFoundException {

        Class<?> returnType = Class.forName(tm.getClassName());
        String fieldName = CaseFormat.LOWER_UNDERSCORE.to(
            CaseFormat.LOWER_CAMEL, tm.getFieldName());
        String methodName = "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);

        return MethodSpec.methodBuilder(methodName)
            .addModifiers(Modifier.PUBLIC)
            .returns(returnType)
            .addStatement("return $N", fieldName)
            .build();
    }

    public MethodSpec defineSetter(TableMetadata tm) throws ClassNotFoundException {

        Class<?> returnType = Class.forName(tm.getClassName());
        String fieldName = CaseFormat.LOWER_UNDERSCORE.to(
            CaseFormat.LOWER_CAMEL, tm.getFieldName());
        String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
        ParameterSpec parameterSpec = ParameterSpec.builder(returnType, fieldName).build();

        return MethodSpec.methodBuilder(methodName)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(parameterSpec)
            .addStatement("this.$N = $N", fieldName, fieldName)
            .build();
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

        } catch(ClassNotFoundException e) {
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
