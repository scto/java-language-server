package org.javacs;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collections;
import org.javacs.guava.ClassPath;
import org.junit.Ignore;
import org.junit.Test;

public class ClassesTest {

    @Test
    public void list() {
        var jdk = Classes.jdkTopLevelClasses();
        assertThat(jdk, hasItem("java.util.List"));

        var empty = Classes.classPathTopLevelClasses(Collections.emptySet());
        assertThat(empty, not(hasItem("java.util.List")));
    }

    @Test
    public void arrayList() {
        var jdk = Classes.jdkTopLevelClasses();
        assertThat(jdk, hasItem("java.util.ArrayList"));
    }

    @Test
    @Ignore
    public void platformClassPath() throws Exception {
        var fs = FileSystems.getFileSystem(URI.create("jrt:/"));
        var path = fs.getPath("/");
        Files.walk(path).forEach(p -> System.out.println(p));
    }

    @Test
    public void loadMain() throws Exception {
        var classes = ClassPath.from(this.getClass().getClassLoader());
        var found = classes.getTopLevelClasses("org.javacs");
        assertThat(found, hasItem(hasToString("org.javacs.Main")));

        var main = found.stream().filter(c -> c.getName().equals("org.javacs.Main")).findFirst();
        assertTrue(main.isPresent());
    }

    void ancestors(ClassLoader classLoader) {
        while (classLoader != null) {
            System.out.println(classLoader.toString());
            classLoader = classLoader.getParent();
        }
    }

    @Test
    @Ignore
    public void printAncestors() throws Exception {
        System.out.println("This:");
        ancestors(this.getClass().getClassLoader());
        System.out.println("List:");
        ancestors(java.util.List.class.getClassLoader());
        System.out.println("System:");
        ancestors(ClassLoader.getSystemClassLoader());
        System.out.println("Platform:");
        ancestors(ClassLoader.getPlatformClassLoader());
    }
}
