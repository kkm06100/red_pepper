package org.example;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("com.example.annotation.Getter") // 처리할 어노테이션 지정
@SupportedSourceVersion(SourceVersion.RELEASE_17) // 지원할 Java 버전
public class GetterProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for(Element element:roundEnv.getElementsAnnotatedWith(Getter.class)){
            String fieldName = element.getSimpleName().toString();
            String className = ((TypeElement)element.getEnclosingElement()).getSimpleName().toString();

            String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            MethodSpec getterMethod = MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(element.asType()))
                    .addStatement("return this.$N",fieldName)
                    .build();

            TypeSpec generatedClass = TypeSpec.classBuilder(className + "Generated")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(getterMethod)
                    .build();

            JavaFile javaFile = JavaFile.builder(processingEnv.getElementUtils()
                    .getPackageOf(element).toString(),generatedClass)
                    .build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return true;
    }
}
