package coderepair;

import coderepair.antlr.JavaPBaseVisitor;
import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

public class Neo4JQueryBuilder extends JavaPBaseVisitor<Void> {

    private final JavaPParser.JavapContext javap;
    private final HashSet<String> packages = new HashSet<String>();
    private final HashMap<String, ClassNode> classes = new HashMap<String, ClassNode>();
    private final HashMap<ClassNode, List<ClassNode>> extensions = new HashMap<ClassNode, List<ClassNode>>();

    public Neo4JQueryBuilder(String fileName) throws IOException {
        this(fileName, new ArrayList<String>());
    }

    public Neo4JQueryBuilder(String fileName, Collection<String> okPackages) throws IOException {
        JavaPLexer lexer = new JavaPLexer(new ANTLRFileStream(fileName));
        JavaPParser parser = new JavaPParser(new BufferedTokenStream(lexer));
        javap = parser.javap();

        if (parser.getNumberOfSyntaxErrors() > 0)
            throw new RuntimeException("Syntax error in input.");

        packages.addAll(okPackages);
    }

    public String getQuery() {
        packages.add("java.lang");
        visitJavap(javap);

        // Build the nodes insertion
        StringBuilder createNodesQuery = new StringBuilder();
        for (ClassNode curClass : classes.values()) {
            if (excludeClass(curClass)) continue;
            createNodesQuery.append(String.format("CREATE (a:JavaClass { package: '%s', name: '%s' }) ;\n",
                                                  curClass.packageName, curClass.className));
        }

        final String superClassFormat =
                "MATCH (a:JavaClass),(b:JavaClass) " +
                        "WHERE a.package = '%s' AND a.name = '%s' " +
                        "AND b.package = '%s' AND b.name = '%s' " +
                        "CREATE (a)-[r:INHERITS { type : 'superclass' }]->(b) " +
                        "RETURN r ;\n";
        final StringBuilder superClassQuery = new StringBuilder();
        extensions.forEach(new BiConsumer<ClassNode, List<ClassNode>>() {
            @Override public void accept(ClassNode subClass, List<ClassNode> classNodes) {
                if (excludeClass(subClass)) return;
                for (ClassNode superClass : classNodes) {
                    if (excludeClass(superClass)) continue;
                    superClassQuery.append(String.format(superClassFormat,
                                                         subClass.packageName, subClass.className,
                                                         superClass.packageName, superClass.className));
                }
            }
        });

        String cqlFormat = "begin\n%s\ncommit\nexit";
        String resetQuery = "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r;\n";
        String wholeQuery = resetQuery + String.valueOf(createNodesQuery) + superClassQuery;

        return String.format(cqlFormat, wholeQuery);
    }

    private boolean excludeClass(ClassNode cls) {
        return cls.className.contains("$") || (packages.size() != 1 && !packages.contains(cls.packageName));
    }

    @Override public Void visitJavap(@NotNull JavaPParser.JavapContext ctx) {
        for (JavaPParser.ClassDeclarationContext classDeclaration : ctx.classDeclaration())
            visitClassDeclaration(classDeclaration);
        return null;
    }

    @Override public Void visitClassDeclaration(@NotNull JavaPParser.ClassDeclarationContext ctx) {
        getOrInsert(ctx.typeName().getText());
        if (ctx.extension() != null)
            visitExtension(ctx, ctx.extension());
        return null;
    }


    public Void visitExtension(@NotNull JavaPParser.ClassDeclarationContext parent,
                               @NotNull JavaPParser.ExtensionContext ctx) {
        for (String superClass : ruleTexts(ctx.typeList().typeName()))
            addExtension(parent.typeName().getText(), superClass);
        return null;
    }

    private @NotNull ClassNode getOrInsert(@NotNull String qualifiedName) {
        ClassNode extantNode = classes.get(qualifiedName);
        if (extantNode != null)
            return extantNode;
        ClassNode newClass = new ClassNode(qualifiedName);
        classes.put(qualifiedName, newClass);
        return newClass;
    }

    private void addExtension(String subClass, String superClass) {
        ClassNode subNode = getOrInsert(subClass);
        ClassNode superNode = getOrInsert(superClass);

        List<ClassNode> supers = extensions.getOrDefault(subNode, new ArrayList<ClassNode>());
        supers.add(superNode);

        extensions.put(subNode, supers);
    }

    private String[] ruleTexts(Collection<? extends RuleContext> rules) {
        String[] ruleValues = new String[rules.size()];
        int i = 0;
        for (RuleContext rule : rules)
            ruleValues[i++] = rule.getText();
        return ruleValues;
    }

    private static class ClassNode {
        public String qualifiedName;
        public String className;
        public String packageName;

        private ClassNode(String qualifiedName) {
            int lastSeparator = Math.max(qualifiedName.lastIndexOf('.'), qualifiedName.lastIndexOf('/'));

            this.qualifiedName = qualifiedName;
            this.className = qualifiedName.substring(lastSeparator + 1);
            this.packageName = qualifiedName.substring(0, lastSeparator);
        }

        @Override
        public boolean equals(Object o) {
            return this == o
                    || !(o == null || getClass() != o.getClass())
                    && qualifiedName.equals(((ClassNode) o).qualifiedName);
        }

        @Override
        public int hashCode() {
            return qualifiedName.hashCode();
        }
    }
}
