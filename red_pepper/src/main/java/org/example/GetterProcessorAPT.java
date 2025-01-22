package org.example;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Set;

/**
 * <h1> 겁나 구린 어노테이션 프로세서</h1>
 * <strong>새로운 객체를 컴파일 타임에</strong> 생성함 -> <i>그걸 참조해야 Getter 기능을 쓸 수 있음</i>
 */
@SupportedAnnotationTypes({"Getter"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class GetterProcessorAPT extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for(Element element : roundEnv.getElementsAnnotatedWith(Getter.class)){
            if(element.getKind() == ElementKind.CLASS){
                try{

                }catch (Exception e){
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                }
            }
        }
        return true;
    }
    private void generateGetter(TypeElement classElement) throws Exception{
        String className = classElement.getSimpleName().toString();
        String packageName = processingEnv.getElementUtils().getPackageOf(classElement).toString();

        StringBuilder code = new StringBuilder();
        code.append("package ").append(className).append("\n\n");
        code.append("public class").append(className).append("Getters {\n");

        for(Element enclosed : classElement.getEnclosedElements()){
            // not including modifier PRIVATE: !enclosed.getModifiers().contains(Modifier.PRIVATE)
            if(enclosed.getKind() == ElementKind.FIELD){
                String fieldName = enclosed.getSimpleName().toString();
                String fieldType = enclosed.asType().toString();
                String methodName = "get" + capitalize(fieldName);

                code.append("   public").append(fieldType).append(" ").append(methodName)
                        .append("() {\n}");
                code.append("return this.").append(fieldName).append(";\n");
                code.append("   }\n");
            }
        }
        code.append("\n");

        JavaFileObject file = processingEnv.getFiler()
                .createSourceFile(packageName+"."+className+"Getters");
        try(Writer writer =file.openWriter()){
            writer.write(code.toString());
        }
    }
    private String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
