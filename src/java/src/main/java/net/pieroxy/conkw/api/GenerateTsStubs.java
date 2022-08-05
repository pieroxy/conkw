package net.pieroxy.conkw.api;

import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiEndpoint;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.utils.reflection.GetAccessibleClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public class GenerateTsStubs {
    private static BufferedWriter writer;
    private static List<String> later = new ArrayList<>();

    private static void write(String s) throws IOException {
        writer.write(s);
        writer.newLine();
    }

    private static void writeLater(String s) {
        later.add(s);
    }

    public static void main(String[]args) throws Exception {
        File out = new File(args[0]);
        writer = new BufferedWriter(new FileWriter(out));

        write("/* tslint:disable */");
        write("/* eslint-disable */");
        write("// Generated on " + new Date());
        write("import { Api } from \"../utils/api/Api\";");
        writeLater("export class ApiEndpoints {");

        Map<String, ApiEndpoint> newEp = new HashMap<>();
        List<Class<?>> allclasses = GetAccessibleClasses.getClasses("net.pieroxy.conkw");
        for (Class c : allclasses) {
            if (ApiEndpoint.class.isAssignableFrom(c) && c.isAnnotationPresent(Endpoint.class)) {
                System.out.println("Processing " + c.getName());
                Endpoint endpoint = (Endpoint)c.getAnnotation(Endpoint.class);
                String input = getType(c,0);
                String output = getType(c, 1);
                String name = c.getSimpleName().substring(0, c.getSimpleName().length() - "Endpoint".length());
                List<String> toImport = new ArrayList<>();
                if (!input.equals("any")) toImport.add(input);
                if (!output.equals("any")) toImport.add(output);

                write("import { "+toImport.stream().collect(Collectors.joining(","))+" } from \"./pieroxy-conkw\";");
                writeLater("    public static "+name+" = {");
                writeLater("        call:(input:"+input+"):Promise<"+output+"> => {");
                writeLater("            return Api.call<"+input+", "+output+">({");
                writeLater("                    method:\""+endpoint.method()+"\",");
                writeLater("                    body:input,");
                writeLater("                    endpoint:\""+name+"\"");
                writeLater("            });");
                writeLater("        }");
                writeLater("    }");
                writeLater("");
            }
        }

        for (String s : later) write(s);
        write("}");

        writer.close();
    }

    private static String getType(Class subClass, int i) {
        while (subClass.getSuperclass() != AbstractApiEndpoint.class) {
            // instance.getClass() is no subclass of classOfInterest or instance is a direct instance of classOfInterest
            subClass = subClass.getSuperclass();
            if (subClass == null) throw new IllegalArgumentException();
        }
        ParameterizedType parameterizedType = (ParameterizedType) subClass.getGenericSuperclass();
        String res = ((Class)parameterizedType.getActualTypeArguments()[i]).getSimpleName();
        if (res.equals("Object")) res = "any";
        return res;
    }
}
