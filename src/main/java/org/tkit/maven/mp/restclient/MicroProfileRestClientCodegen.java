/*
 * Copyright 2020 tkit.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tkit.maven.mp.restclient;

import com.google.googlejavaformat.java.Formatter;
import io.swagger.codegen.v3.CliOption;
import io.swagger.codegen.v3.CodegenConstants;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenObject;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenParameter;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.codegen.v3.VendorExtendable;
import io.swagger.codegen.v3.generators.java.AbstractJavaJAXRSServerCodegen;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The micro-profile client code generator.
 */
@Slf4j
public class MicroProfileRestClientCodegen extends AbstractJavaJAXRSServerCodegen {

    /**
     * The key for lombok data.
     */
    private static final String LOMBOK_DATA = "lombokData";

    /**
     * The key for field public.
     */
    private static final String IMPL_REST_CLASS = "implRestClass";

    /**
     * The key for field public.
     */
    static final String IMPL_PROXY = "implProxy";

    /**
     * The key for field public.
     */
    private static final String FIELD_PUBLIC = "fieldPublic";

    /**
     * The key for generate getter setter.
     */
    private static final String GENERATE_GETTER_SETTER = "generateGetterSetter";

    /**
     * The key for generate equals.
     */
    private static final String GENERATE_EQUALS = "generateEquals";

    /**
     * The key for api interface doc.
     */
    private static final String GENERATE_TO_STRING = "generateToString";

    /**
     * The key for jackson.
     */
    private static final String JACKSON = "jackson";

    /**
     * The key for jsonb.
     */
    private static final String JSONB = "jsonb";

    /**
     * The key for interface only.
     */
    static final String INTERFACE_ONLY = "interfaceOnly";

    /**
     * The key for formatter.
     */
    static final String FORMATTER = "formatter";

    /**
     * Groups REST by tags
     */
    static final String GROUP_BY_TAGS = "groupByTags";

    /**
     * The key for api name.
     */
    static final String API_NAME = "apiName";

    /**
     * The key for path prefix.
     */
    static final String PATH_PREFIX = "pathPrefix";

    /**
     * The key for api interface doc.
     */
    static final String API_SUFFIX = "apiSuffix";

    /**
     * The key for api interface doc.
     */
    static final String PROXY_CLIENT_CLASS = "proxyClientClass";

    /**
     * The key for api interface doc.
     */
    static final String ANNOTATIONS = "annotations";

    /**
     * The key for model annotations.
     */
    private static final String HAS_ANNOTATIONS = "hasAnnotations";

    /**
     * The key for api interface doc.
     */
    static final String MODEL_ANNOTATIONS = "modelAnnotations";

    /**
     * The key for has model annotations.
     */
    private static final String HAS_MODEL_ANNOTATIONS = "hasModelAnnotations";

    /**
     * The key for bean parameter suffix.
     */
    static final String BEAN_PARAM_SUFFIX = "beanParamSuffix";

    /**
     * The key for bean parameter count.
     */
    static final String BEAN_PARAM_COUNT = "beanParamCount";

    /**
     * The key for return response flag.
     */
    static final String RETURN_RESPONSE = "returnResponse";

    /**
     * The key for api interface doc.
     */
    static final String JSON_LIB = "jsonLib";

    /**
     * The key for api interface doc.
     */
    static final String FIELD_GEN = "fieldGen";

    /**
     * The implementation type.
     */
    static final String IMPL_TYPE = "implType";

    /**
     * The key for api interface doc.
     */
    static final String USE_BEAN_VALIDATION = "useBeanValidation";

    /**
     * The key for api interface doc.
     */
    static final String API_INTERFACE_DOC = "apiInterfaceDoc";

    /**
     * The key for apiPathFromBasePath option
     */
    static final String BAS_PATH_PREFIX = "basePathPrefix";

    /**
     * The list of generated files.
     */
    private List<String> outputFiles = new ArrayList<>();

    /**
     * The api suffix.
     */
    private String apiSuffix = "Api";

    /**
     * Use the formatter flag.
     */
    private boolean format = true;

    /**
     * The api name.
     */
    private String apiName = null;

    /**
     * Group REST by tags
     */
    private boolean groupByTags = false;

    /**
     * The path prefix.
     */
    private String pathPrefix = null;

    /**
     * The bean parameter suffix.
     */
    private String beanParamSuffix = "BeanParam";

    /**
     * The bean parameter count.
     */
    private int beanParamCount = 4;

    private Map<String, CodegenModel> codegenModelMap = new HashMap<>();

    private final String COMPLEX_QUERY_PARAM_KEY = "complex-query-param";

    /**
     * The default constructor.
     */
    public MicroProfileRestClientCodegen() {
        super();
        invokerPackage = "io.swagger.api";
        artifactId = "swagger-jaxrs-server";
        outputFolder = "generated-code/mp-rest-client";
        apiPackage = "api";
        modelPackage = "model";
        additionalProperties.put("title", title);

        additionalProperties.put(BEAN_PARAM_SUFFIX, beanParamSuffix);
        additionalProperties.put(LOMBOK_DATA, false);
        additionalProperties.put(FIELD_PUBLIC, true);
        additionalProperties.put(JSONB, true);
        additionalProperties.put(JACKSON, false);
        additionalProperties.put(GENERATE_GETTER_SETTER, false);
        additionalProperties.put(API_SUFFIX, apiSuffix);
        additionalProperties.put(RETURN_RESPONSE, true);

        for (int i = 0; i < cliOptions.size(); i++) {
            if (CodegenConstants.LIBRARY.equals(cliOptions.get(i).getOpt())) {
                cliOptions.remove(i);
                break;
            }
        }

        CliOption library = new CliOption(CodegenConstants.LIBRARY, "library template (sub-template) to use");
        library.setDefault(DEFAULT_LIBRARY);

        Map<String, String> supportedLibraries = new LinkedHashMap<>();
        supportedLibraries.put(DEFAULT_LIBRARY, "JAXRS");
        library.setEnum(supportedLibraries);
        cliOptions.add(library);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void processOpts() {
        super.processOpts();

        writePropertyBack(USE_BEANVALIDATION, useBeanValidation);

        additionalProperties.put(HAS_ANNOTATIONS, updateCodegenExtraAnnotationList(ANNOTATIONS));
        additionalProperties.put(HAS_MODEL_ANNOTATIONS, updateCodegenExtraAnnotationList(MODEL_ANNOTATIONS));

        if (additionalProperties.containsKey(API_SUFFIX)) {
            apiSuffix = (String) additionalProperties.get(API_SUFFIX);
        }

        if (additionalProperties.containsKey(API_NAME)) {
            apiName = (String) additionalProperties.get(API_NAME);
        }

        if (additionalProperties.containsKey(PATH_PREFIX)) {
            pathPrefix = (String) additionalProperties.get(PATH_PREFIX);
        }

        if (additionalProperties.containsKey(BEAN_PARAM_SUFFIX)) {
            beanParamSuffix = (String) additionalProperties.get(BEAN_PARAM_SUFFIX);
        }

        if (additionalProperties.containsKey(BEAN_PARAM_COUNT)) {
            beanParamCount = Integer.parseInt(additionalProperties.get(BEAN_PARAM_COUNT).toString());
            additionalProperties.put(BEAN_PARAM_COUNT, beanParamCount);
        }

        groupByTags = updateBoolean(GROUP_BY_TAGS, groupByTags);
        format = updateBoolean(FORMATTER, format);
        updateBoolean(API_INTERFACE_DOC, true);

        writePropertyBack(FIELD_PUBLIC, true);
        writePropertyBack(LOMBOK_DATA, false);
        writePropertyBack(GENERATE_EQUALS, false);
        writePropertyBack(GENERATE_TO_STRING, false);
        writePropertyBack(GENERATE_GETTER_SETTER, false);
        if (additionalProperties.containsKey(FIELD_GEN)) {
            FieldGenerator gen = (FieldGenerator) additionalProperties.getOrDefault(FIELD_GEN, FieldGenerator.PUBLIC);
            switch (gen) {
                case PUBLIC:
                    writePropertyBack(FIELD_PUBLIC, true);
                    break;
                case LOMBOK:
                    writePropertyBack(LOMBOK_DATA, true);
                    break;
                case GET_SET:
                    writePropertyBack(GENERATE_EQUALS, true);
                    writePropertyBack(GENERATE_TO_STRING, true);
                    writePropertyBack(GENERATE_GETTER_SETTER, true);
                    break;
            }
        }

        boolean proxyImplementation = false;
        updateBoolean(INTERFACE_ONLY, true);
        boolean interfaceOnly = (Boolean) additionalProperties.get(INTERFACE_ONLY);
        if (interfaceOnly) {
            writePropertyBack(IMPL_REST_CLASS, false);
        } else {
            writePropertyBack(IMPL_REST_CLASS, true);
            if (additionalProperties.containsKey(IMPL_TYPE)) {
                ImplType impl = (ImplType) additionalProperties.getOrDefault(IMPL_TYPE, ImplType.CLASS);
                switch (impl) {
                    case CLASS:
                        writePropertyBack(IMPL_REST_CLASS, true);
                        break;
                    case INTERFACE:
                        writePropertyBack(IMPL_REST_CLASS, false);
                        break;
                    case PROXY:
                        writePropertyBack(IMPL_REST_CLASS, true);
                        writePropertyBack(IMPL_PROXY, true);
                        proxyImplementation = true;
                        break;
                }
            }
        }

        writePropertyBack(JSONB, true);
        writePropertyBack(JACKSON, false);
        if (additionalProperties.containsKey(JSON_LIB)) {
            JsonLib jsonLib = (JsonLib) additionalProperties.getOrDefault(JSON_LIB, JsonLib.JSONB.name());
            switch (jsonLib) {
                case JSONB:
                    writePropertyBack(JSONB, true);
                    writePropertyBack(JACKSON, false);
                    break;
                case JACKSON:
                    writePropertyBack(JACKSON, true);
                    writePropertyBack(JSONB, false);
                    break;
            }
        }

        updateBoolean(RETURN_RESPONSE, true);


        if (StringUtils.isBlank(templateDir)) {
            embeddedTemplateDir = templateDir = getTemplateDir();
        }

        modelTemplateFiles.put("model.mustache", ".java");
        apiTemplateFiles.put("api.mustache", ".java");
        apiTestTemplateFiles.clear();
        modelTestTemplateFiles.clear();
        modelDocTemplateFiles.remove("model_doc.mustache");
        apiDocTemplateFiles.remove("api_doc.mustache");
        supportingFiles.clear();

        // do not generate the models. We will use the models from the rest client
        if (proxyImplementation) {
            modelDocTemplateFiles.clear();
            modelTemplateFiles.clear();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return "tkit-mp-rest-client-plugin";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getHelp() {
        return "Micro-profile rest client generator according to JAXRS 2.0 specification.";
    }

    @Override
    public String customTemplateDir() {
        return getDefaultTemplateDir();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDefaultTemplateDir() {
        return "tkit-mp-rest-client";
    }

    /**
     * Updates the boolean parameter value.
     *
     * @param name  the name of the parameter.
     * @param value the default value.
     * @return the value or default value of the parameter.
     */
    private boolean updateBoolean(String name, boolean value) {
        boolean result = value;
        if (additionalProperties.containsKey(name)) {
            result = convertPropertyToBoolean(name);
            writePropertyBack(name, result);
        }
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toApiName(String name) {
        if (!groupByTags && apiName != null) {
            return apiName;
        }
        String computed = name;
        if (computed.length() == 0) {
            return "Default" + apiSuffix;
        }
        if (groupByTags) {
            computed = computed.replace("internal", "");
        }
        computed = sanitizeName(computed);
        return camelize(computed) + apiSuffix;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addOperationToGroup(String tag, String resourcePath, Operation operation, CodegenOperation co, Map<String, List<CodegenOperation>> operations) {
        // groups REST services by tag
        if (groupByTags) {
            super.addOperationToGroup(tag, resourcePath, operation, co, operations);
            co.subresourceOperation = !co.path.isEmpty();
            return;
        }

        // group REST service by REST-path
        String restPath = resourcePath;
        // remove the first /
        if (restPath.startsWith("/")) {
            restPath = restPath.substring(1);
        }

        // group all REST services in the schema to `/` to one file
        if ((apiName != null && !apiName.isEmpty() && (pathPrefix == null || pathPrefix.isEmpty())) || "/".equals(pathPrefix)) {

            co.baseName = "";
            co.path = restPath;
            co.subresourceOperation = !co.path.isEmpty();
        } else {
            // check the path prefix
            if (pathPrefix != null && !pathPrefix.isEmpty()) {
                // remove / from path prefix
                String tmp = pathPrefix;
                if (tmp.startsWith("/")) {
                    tmp = tmp.substring(1);
                }
                // check if path start with `pathPrefix`
                if (restPath.startsWith(tmp)) {
                    co.baseName = tmp;
                    int pathPrefixIndex = tmp.endsWith("/") ? tmp.length() : tmp.length() + 1;
                    if (pathPrefixIndex <= restPath.length()) {
                        co.path = restPath.substring(pathPrefixIndex);
                    } else {
                        co.path = "";
                    }
                    co.subresourceOperation = !co.path.isEmpty();
                } else {
                    log.warn("Resource path `" + resourcePath + "` does not start with a prefix `" + tmp + "` and will be ignored!");
                    return;
                }
            } else {
                // no common path prefix defined get first item of the path
                int index = restPath.indexOf("/");
                if (index > 0) {
                    co.baseName = restPath.substring(0, index);
                    co.path = restPath.substring(index);
                } else {
                    // no items in the path, set path to base
                    co.baseName = restPath;
                    co.path = "";
                }
                co.subresourceOperation = !co.path.isEmpty();
            }

        }
        List<CodegenOperation> opList = operations.computeIfAbsent(co.baseName, v -> new ArrayList<>());
        opList.add(co);

        // check multiple operation in one class
        int counter = 0;
        for (CodegenOperation op : opList) {
            if (co.operationId.equals(op.operationId)) {
                counter++;
            }
        }
        // add the tag prefix for the operation.
        // if there are multiple nickname the same name in one class generator add suffix _<number> later
        if (counter > 1) {
            co.nickname = camelize(tag + "-" + co.operationId, true);
        }
        co.operationIdLowerCase = co.operationId.toLowerCase();
        co.operationIdCamelCase = camelize(co.operationId);
        co.operationIdSnakeCase = underscore(co.operationId);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean shouldOverwrite(String filename) {
        if (outputFiles != null) {
            if (outputFiles.contains(filename)) {
                File tmp = new File(filename);
                String f = tmp.getName().replace(".java", "");
                log.error("Wrong plugin configuration. Please check the API schema paths and maven configuration for <groupByTags>, <apiName> and <pathPrefix>.");
                log.error("Add the maven configuration <apiName>{}<apiName>, remove <pathPrefix> and <groupByTags> to generate one java class for open API schema", f);
                log.error("Remove the <groupByTags> if you what to have one file defined by <groupByTags> and the API schema has more tags.");
                throw new IllegalStateException("File '" + tmp.getName() + "' already generated.");
            }
            outputFiles.add(filename);
        }
        return super.shouldOverwrite(filename);
    }

    @Override
    public boolean checkAliasModel() {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void processOpenAPI(OpenAPI openAPI) {
        if (format) {
            log.info("Google formatter source code");
            if (outputFiles != null && !outputFiles.isEmpty()) {
                Formatter gf = new Formatter();
                outputFiles.forEach(file -> {
                    try {
                        log.debug("Formatter source code: {}", file);
                        Path path = Paths.get(file);
                        String sourceString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                        sourceString = gf.formatSourceAndFixImports(sourceString);
                        Files.write(path, sourceString.getBytes(StandardCharsets.UTF_8));
                    } catch (Exception ex) {
                        log.error("Skip format source code of the file {} ", file);
                        log.error("Error by formatting the source code", ex);
                    }
                });
            }
        }
        super.processOpenAPI(openAPI);
    }

    /**
     * Update the codegen extra annotation list.
     *
     * @param name the class name.
     * @return returns {@code true} if there is extra annotations list.
     */
    private boolean updateCodegenExtraAnnotationList(String name) {
        if (additionalProperties.containsKey(name)) {
            List<String> items = (List<String>) additionalProperties.get(name);
            if (items != null && !items.isEmpty()) {
                additionalProperties.put(name, items);
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, Map<String, Schema> schemas, OpenAPI openAPI) {
        CodegenOperation op = super.fromOperation(path, httpMethod, operation, schemas, openAPI);
        ExtCodegenOperation e = new ExtCodegenOperation(op, codegenModelMap);
        e.setBeanParamName(camelize(op.operationId) + beanParamSuffix);
        if (e.beanParams.size() >= beanParamCount) {
            e.setBeanParam(true);
        }
        return e;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, Object> postProcessAllModels(Map<String, Object> processedModels) {
        Map<String, CodegenModel> modelMap = new HashMap<>();

        processedModels.forEach((key, value) -> {
            Map<String, Object> valueMap = (Map<String, Object>) value;
            List<Map<String, Object>> modelsMap = (List<Map<String, Object>>) valueMap.get("models");

            modelMap.putAll(modelsMap
                    .stream()
                    .collect(Collectors.toMap(
                            map -> ((CodegenModel) map.get("model")).getName(),
                            map -> (CodegenModel) map.get("model")
                    )));
        });

        modelMap.forEach((key, value) -> markComplexQueryParams(value, modelMap));

        codegenModelMap.putAll(modelMap);
        return super.postProcessAllModels(processedModels);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);
        model.imports.remove("Schema");
        model.imports.remove("JsonSerialize");
        model.imports.remove("ToStringSerializer");
        model.imports.remove("JsonValue");
        model.imports.remove("JsonProperty");
        model.imports.remove("Data");
        model.imports.remove("ToString");
    }

    private void markComplexQueryParams(CodegenModel model, Map<String, CodegenModel> modelMap) {
        for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
            PathItem pathItem = entry.getValue();
            Set<Parameter> parameters = getAllOperationParameters(pathItem);
            parameters.forEach(markIfComplexQueryParam(model, modelMap));
        }
    }

    private Consumer<Parameter> markIfComplexQueryParam(CodegenModel model, Map<String, CodegenModel> modelMap) {
        return param -> {
            if (isComplexQueryParam(model, param)) {
                markAsComplexQueryParam(model);
                markChildrenIfComplexQueryParam(model, modelMap);
            }
        };
    }

    private void markAsComplexQueryParam(CodegenObject codegenObject) {
        codegenObject.getVendorExtensions().put(VendorExtendable.PREFIX_IS + COMPLEX_QUERY_PARAM_KEY, true);
    }

    private void markChildrenIfComplexQueryParam(CodegenModel model, Map<String, CodegenModel> modelMap) {
        if (model.getHasVars() && !model.getVars().isEmpty()) {
            model.getVars().forEach(codegenProperty -> {
                if (modelMap.containsKey(codegenProperty.getBaseType())) {
                    CodegenModel codegenModel = modelMap.get(codegenProperty.getBaseType());
                    if (isCodegenModelAnObject(codegenModel)) {
                        markAsComplexQueryParam(codegenProperty);
                        markAsComplexQueryParam(codegenModel);
                        markChildrenIfComplexQueryParam(codegenModel, modelMap);
                    }
                }
            });
        }
    }

    private boolean isComplexQueryParam(CodegenModel model, Parameter param) {
        CodegenParameter codegenParameter = fromParameter(param, new HashSet<>());
        return model.getClassname().equals(codegenParameter.getDataType())
                && isCodegenModelAnObject(model)
                && param.getIn().equals("query");
    }

    private boolean isCodegenModelAnObject(CodegenModel model) {
        return !model.getIsPrimitiveType()
                && !model.getIsEnum()
                && !model.getIsArrayModel()
                && !model.getIsListContainer()
                && !model.getIsMapContainer();
    }

    private Set<Parameter> getAllOperationParameters(io.swagger.v3.oas.models.PathItem value) {
        Set<Parameter> parameters = new HashSet<>();
        if (value.getGet() != null && value.getGet().getParameters() != null)
            parameters.addAll(value.getGet().getParameters());
        if (value.getPut() != null && value.getPut().getParameters() != null)
            parameters.addAll(value.getPut().getParameters());
        if (value.getPost() != null && value.getPost().getParameters() != null)
            parameters.addAll(value.getPost().getParameters());
        if (value.getDelete() != null && value.getDelete().getParameters() != null)
            parameters.addAll(value.getDelete().getParameters());
        if (value.getOptions() != null && value.getOptions().getParameters() != null)
            parameters.addAll(value.getOptions().getParameters());
        if (value.getHead() != null && value.getHead().getParameters() != null)
            parameters.addAll(value.getHead().getParameters());
        if (value.getPatch() != null && value.getPatch().getParameters() != null)
            parameters.addAll(value.getPatch().getParameters());
        if (value.getTrace() != null && value.getTrace().getParameters() != null)
            parameters.addAll(value.getTrace().getParameters());
        if (value.getParameters() != null && value.getParameters() != null)
            parameters.addAll(value.getParameters());
        return parameters;
    }
}
