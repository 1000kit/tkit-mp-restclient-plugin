package org.tkit.maven.mp.restclient;

import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.Generator;
import io.swagger.v3.oas.models.Paths;

import java.util.*;

import static org.tkit.maven.mp.restclient.MicroProfileRestClientCodegen.GROUP_BY_TAGS;

public class MicroProfileGenerator extends DefaultGenerator {

    private boolean groupByTags = false;

    @Override
    public Generator opts(ClientOptInput opts) {
        groupByTags = (boolean) opts.getConfig().additionalProperties().get(GROUP_BY_TAGS);
        return super.opts(opts);
    }

    @Override
    public Map<String, List<CodegenOperation>> processPaths(Paths paths) {
        Map<String, List<CodegenOperation>> originalCodegenOperationMap = super.processPaths(paths);
        Map<String, List<CodegenOperation>> modifiedCodegenOperationMap = new HashMap<>();

        if (!groupByTags) {
            return originalCodegenOperationMap;
        }

        for (List<CodegenOperation> values : originalCodegenOperationMap.values()) {
            String rootPath = "";

            // collect all methods
            List<String> codegenOperationPaths = new ArrayList<>();
            for (CodegenOperation co : values) {
                codegenOperationPaths.add(co.getPath());
            }

            // broke first method path into list
            ArrayList<String> codegenOperationPathParts = new ArrayList<>(Arrays.asList(codegenOperationPaths.get(0).split("/")));
            codegenOperationPathParts.remove("");

            if (codegenOperationPathParts.isEmpty()) {
                break;
            }

            // build root path based on common method path parts
            for (String codegenOperationPathPart : codegenOperationPathParts) {
                long matchingCount = codegenOperationPaths.stream().filter(cop -> cop.contains(codegenOperationPathPart)).count();
                if (codegenOperationPaths.size() == matchingCount) {
                    rootPath += "/" + codegenOperationPathPart;
                }
            }

            // remove root path from method paths
            for (CodegenOperation co : values) {
                co.path = co.getPath().replace(rootPath, "");
                if (co.path.isEmpty()) {
                    co.path = "/";
                }
            }

            // in case CodegenOperation has only 1 method, separate method resource from rootPath
            if (codegenOperationPaths.size() == 1) {
                rootPath = rootPath.replace(codegenOperationPathParts.get(codegenOperationPathParts.size() - 1), "");
                values.get(0).path = codegenOperationPathParts.get(codegenOperationPathParts.size() - 1);
            }

            rootPath = rootPath.replaceFirst("/", "");
            modifiedCodegenOperationMap.put(rootPath, values);
        }
        return modifiedCodegenOperationMap;
    }
}