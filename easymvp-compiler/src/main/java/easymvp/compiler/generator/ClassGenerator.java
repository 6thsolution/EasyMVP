package easymvp.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class ClassGenerator {
    private String packageName;
    private String name;
    private ClassName className;

   public ClassGenerator(String packageName, String className) {
        this.packageName = packageName;
        this.name = className;
        this.className = ClassName.get(packageName, className);
    }

    public abstract JavaFile build();

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public ClassName getClassName() {
        return className;
    }

}
