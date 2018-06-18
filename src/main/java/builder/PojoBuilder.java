package builder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import common.Convertor;
import common.Pair;

public class PojoBuilder {

    public void build() throws IOException, ClassNotFoundException {

        TableAccess access = new TableAccess();
        List<Pair<Object, Object>> p = access.getTableColumnAndType();
        System.out.println(p);

        Builder pojoBuilder = TypeSpec.classBuilder("Test").addModifiers(Modifier.PUBLIC);
        p.forEach(fieldInfo -> builderAddFieldInfo(fieldInfo, pojoBuilder));
        TypeSpec pojo = pojoBuilder.build();

        JavaFile javaFile = JavaFile.builder("com.example.demo", pojo).build();
        javaFile.writeTo(System.out);

    }

    public FieldSpec defineField(Pair<Object, Object> p) throws ClassNotFoundException {

        Class<?> fieldType = Class.forName((String) p.getRight());
        String fieldName = Convertor.snakeCase2CamelCase((String) p.getLeft());
        FieldSpec fieldSpec = FieldSpec.builder(fieldType, fieldName).addModifiers(Modifier.PRIVATE).build();

        return fieldSpec;

    }

    public MethodSpec defineGetter(Pair<Object, Object> p) throws ClassNotFoundException {

        Class<?> returnType = Class.forName((String) p.getRight());
        String fieldName = Convertor.snakeCase2CamelCase((String) p.getLeft());
        String methodName = "get" + Convertor.firstCharUpperConvert(fieldName);
        MethodSpec methodSpec = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(returnType)
                .addStatement("return $N", fieldName).build();

        return methodSpec;

    }

    public MethodSpec defineSetter(Pair<Object, Object> p) throws ClassNotFoundException {

        Class<?> returnType = Class.forName((String) p.getRight());
        String fieldName = Convertor.snakeCase2CamelCase((String) p.getLeft());
        String methodName = "set" + Convertor.firstCharUpperConvert(fieldName);
        ParameterSpec parameterSpec = ParameterSpec.builder(returnType, fieldName).build();
        MethodSpec methodSpec = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec).addStatement("this.$N = $N", fieldName, fieldName).build();

        return methodSpec;

    }

    public void builderAddFieldInfo(Pair<Object, Object> fieldInfo, Builder instance) {
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
