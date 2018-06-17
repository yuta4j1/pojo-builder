package builder;

import java.io.IOException;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import common.Convertor;
import common.Pair;

public class PojoBuilder {

    public void build() throws IOException, ClassNotFoundException {

        TableAccess access = new TableAccess();
        List<Pair<Object, Object>> p = access.getTableColumnAndType();
        System.out.println(p);
        p.forEach(pair -> System.out.println(pair.getRight().getClass()));

        Pair<Object, Object> ap = p.get(0);
       FieldSpec field = defineField(ap);
       MethodSpec getter = defineGetter(ap);
       MethodSpec setter = defineSetter(ap);

        TypeSpec pojo = TypeSpec.classBuilder("HelloWorld").addModifiers(Modifier.PUBLIC)
                .addField(field)
                .addMethod(getter)
                .addMethod(setter)
                .build();

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
        MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec)
                .addStatement("this.$N = $N", fieldName, fieldName)
                .build();

        return methodSpec;

    }

}
