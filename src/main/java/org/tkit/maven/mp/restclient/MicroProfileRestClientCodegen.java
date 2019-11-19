package org.tkit.maven.mp.restclient;

import com.google.googlejavaformat.java.Formatter;
import io.swagger.codegen.v3.*;
import io.swagger.codegen.v3.generators.java.AbstractJavaJAXRSServerCodegen;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MicroProfileRestClientCodegen extends AbstractJavaJAXRSServerCodegen {

    private static final String LOMBOK_DATA = "lombokData";

    private static final String FIELD_PUBLIC = "fieldPublic";

    private static final String GENERATE_GETTER_SETTER = "generateGetterSetter";

    private static final String GENERATE_EQUALS = "generateEquals";

    private static final String GENERATE_TO_STRING = "generateToString";

    private static final String JACKSON = "jackson";

    private static final String JSONB = "jsonb";

    static final String INTERFACE_ONLY = "interfaceOnly";

    static final String FORMATTER = "formatter";

    static final String API_NAME = "apiName";

    static final String GROUP_PREFIX = "groupPrefix";

    static final String API_SUFFIX = "apiSuffix";

    static final String PROVIDERS = "providers";

    private static final String HAS_PROVIDERS = "hasProviders";

    static final String ANNOTATIONS = "annotations";

    private static final String HAS_ANNOTATIONS = "hasAnnotations";

    static final String MODEL_ANNOTATIONS = "modelAnnotations";

    private static final String HAS_MODEL_ANNOTATIONS = "hasModelAnnotations";

    static final String REST_CLIENT = "restClient";

    static final String BEAN_PARAM_SUFFIX = "beanParamSuffix";

    static final String BEAN_PARAM_COUNT = "beanParamCount";

    static final String RETURN_RESPONSE = "returnResponse";

    static final String JSON_LIB = "jsonLib";

    static final String FIELD_GEN = "fieldGen";

    static final String USE_BEAN_VALIDATION = "useBeanValidation";

    private List<String> outputFiles = new ArrayList<>();

    private String apiSuffix = "Api";

    private boolean format = true;

    private String apiName = null;

    private String groupPrefix = null;

    private String beanParamSuffix = "BeanParam";

    private int beanParamCount = 4;

    public MicroProfileRestClientCodegen() {
        super();
        invokerPackage = "io.swagger.api";
        artifactId = "swagger-jaxrs-server";
        outputFolder = "generated-code/tkit-mp-restclient";
        apiPackage = "api";
        modelPackage = "model";
        additionalProperties.put("title", title);

//        additionalProperties.put(PROVIDERS, null);
//        additionalProperties.put(REST_CLIENT, true);
//        additionalProperties.put(BEAN_PARAM_SUFFIX, beanParamSuffix);
//        additionalProperties.put(LOMBOK_DATA, false);
//        additionalProperties.put(FIELD_PUBLIC, true);
//        additionalProperties.put(JSONB, true);
//        additionalProperties.put(JACKSON, false);
//        additionalProperties.put(GENERATE_GETTER_SETTER, false);
//        additionalProperties.put(API_SUFFIX, apiSuffix);
//        additionalProperties.put(RETURN_RESPONSE, true);

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

//        cliOptions.add(CliOption.newString(GROUP_PREFIX, "Group path prefix to group operation to interface. For example groupPrefix=v1/ v1/process to process interface").defaultValue(groupPrefix));
//        cliOptions.add(CliOption.newBoolean(FORMATTER, "Google formatter source code.").defaultValue(String.valueOf(format)));
//        cliOptions.add(CliOption.newString(PROVIDERS, "List of register providers @RegisterProvider('class_name') annotation on the interface. (Separator #)").defaultValue(null));
//        cliOptions.add(CliOption.newBoolean(REST_CLIENT, "Generate the @RegisterRestClient annotation on the interface.").defaultValue(String.valueOf(true)));
//        cliOptions.add(CliOption.newBoolean(RETURN_RESPONSE, "Return jaxrs response.").defaultValue(String.valueOf(true)));
//        cliOptions.add(CliOption.newBoolean(INTERFACE_ONLY, "Interface only.").defaultValue(String.valueOf(true)));
//        cliOptions.add(CliOption.newBoolean(JACKSON, "Use the jackson annotation.").defaultValue(String.valueOf(false)));
//        cliOptions.add(CliOption.newBoolean(JSONB, "Use the jsonb property annotation for pojo.").defaultValue(String.valueOf(true)));
//        cliOptions.add(CliOption.newBoolean(LOMBOK_DATA, "Use the lombok @Data annotation for pojo.").defaultValue(String.valueOf(false)));
//        cliOptions.add(CliOption.newBoolean(FIELD_PUBLIC, "Public fields in the pojo.").defaultValue(String.valueOf(true)));
//        cliOptions.add(CliOption.newBoolean(GENERATE_GETTER_SETTER, "Generate getter and setter for pojo.").defaultValue(String.valueOf(false)));
//        cliOptions.add(CliOption.newBoolean(GENERATE_TO_STRING, "Generate toString method for pojo.").defaultValue(String.valueOf(false)));
//        cliOptions.add(CliOption.newBoolean(GENERATE_EQUALS, "Generate equals/hash method for pojo.").defaultValue(String.valueOf(false)));
//        cliOptions.add(CliOption.newString(API_SUFFIX, "Name of the api client class suffic.").defaultValue(apiSuffix));
//        cliOptions.add(CliOption.newString(API_NAME, "Name of the api client class.").defaultValue(apiName));
//        cliOptions.add(CliOption.newString(BEAN_PARAM_SUFFIX, "The bean parameter suffix.").defaultValue(beanParamSuffix));
//        cliOptions.add(CliOption.newString(BEAN_PARAM_COUNT, "Generate the bean for more than {beanParamCount} parameters. Disable generator -1").defaultValue(String.valueOf(beanParamCount)));

    }

    @Override
    public void processOpts() {
        super.processOpts();

        writePropertyBack(USE_BEANVALIDATION, useBeanValidation);

        additionalProperties.put(HAS_PROVIDERS, updateCodegenExtraAnnotationList(PROVIDERS));
        additionalProperties.put(HAS_ANNOTATIONS, updateCodegenExtraAnnotationList(ANNOTATIONS));
        additionalProperties.put(HAS_MODEL_ANNOTATIONS, updateCodegenExtraAnnotationList(MODEL_ANNOTATIONS));

        if (additionalProperties.containsKey(API_SUFFIX)) {
            apiSuffix = (String) additionalProperties.get(API_SUFFIX);
        }

        if (additionalProperties.containsKey(API_NAME)) {
            apiName = (String) additionalProperties.get(API_NAME);
        }

        if (additionalProperties.containsKey(GROUP_PREFIX)) {
            groupPrefix = (String) additionalProperties.get(GROUP_PREFIX);
        }

        if (additionalProperties.containsKey(BEAN_PARAM_SUFFIX)) {
            beanParamSuffix = (String) additionalProperties.get(BEAN_PARAM_SUFFIX);
        }

        if (additionalProperties.containsKey(BEAN_PARAM_COUNT)) {
            beanParamCount = Integer.parseInt(additionalProperties.get(BEAN_PARAM_COUNT).toString());
            additionalProperties.put(BEAN_PARAM_COUNT, beanParamCount);
        }

        format = updateBoolean(FORMATTER, format);
        updateBoolean(REST_CLIENT, true);


        writePropertyBack(FIELD_PUBLIC, true);
        writePropertyBack(LOMBOK_DATA, false);
        writePropertyBack(GENERATE_EQUALS, false);
        writePropertyBack(GENERATE_TO_STRING, false);
        writePropertyBack(GENERATE_GETTER_SETTER, false);
        if (additionalProperties.containsKey(FIELD_GEN)) {
            FieldGenerator fieldgen = (FieldGenerator) additionalProperties.getOrDefault(FIELD_GEN, FieldGenerator.PUBLIC);
            switch (fieldgen) {
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

        writePropertyBack(JSONB, true);
        writePropertyBack(JACKSON, false);
        if (additionalProperties.containsKey(JSON_LIB)) {
            JsonLib jsonLib = (JsonLib) additionalProperties.getOrDefault(JSON_LIB, JsonLib.JSONB.name());
            switch (jsonLib) {
                case JSONB:
                    writePropertyBack(JSONB, true);
                    break;
                case JACKSON:
                    writePropertyBack(JACKSON, true);
                    break;
            }
        }

        updateBoolean(RETURN_RESPONSE, true);
        updateBoolean(INTERFACE_ONLY, true);

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
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return "tkit-mp-restclient-plugin";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getHelp() {
        return "Micro-profile rest client generator according to JAXRS 2.0 specification.";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDefaultTemplateDir() {
        return "tkit-mp-restclient";
    }

    private boolean updateBoolean(String name, boolean value) {
        boolean result = value;
        if (additionalProperties.containsKey(name)) {
            result = convertPropertyToBoolean(name);
            writePropertyBack(name, result);
        }
        return result;
    }

    @Override
    public String toApiName(String name) {
        if (apiName != null) {
            return apiName;
        }
        if (groupPrefix != null) {
            name = name.replaceFirst(groupPrefix, "");
        }
        String computed = name;
        if (computed.length() == 0) {
            return "Default" + apiSuffix;
        }
        computed = sanitizeName(computed);
        return camelize(computed) + apiSuffix;
    }

    @Override
    public void addOperationToGroup(String tag, String resourcePath, Operation operation, CodegenOperation co, Map<String, List<CodegenOperation>> operations) {
        String basePath = resourcePath;
        if (groupPrefix != null) {
            basePath = basePath.replaceFirst(groupPrefix, "");
        }
        if (basePath.startsWith("/")) {
            basePath = basePath.substring(1);
        }
        int pos = basePath.indexOf('/');
        if (pos > 0) {
            basePath = basePath.substring(0, pos);
        }

        if (basePath.equals("")) {
            basePath = "default";
        } else {
            if (co.path.startsWith("/" + basePath)) {
                co.path = co.path.substring(("/" + basePath).length());
            }
            co.subresourceOperation = !co.path.isEmpty();
        }
        if (groupPrefix != null) {
            co.baseName = groupPrefix + basePath;
            if (co.path.startsWith("/")) {
                co.path = co.path.substring(1);
            }
            co.path = co.path.replaceFirst(co.baseName, "");
        } else {
            co.baseName = basePath;
        }
        List<CodegenOperation> opList = operations.computeIfAbsent(co.baseName, v -> new ArrayList<>());
        opList.add(co);
    }

    @Override
    public boolean shouldOverwrite(String filename) {
        if (outputFiles != null) {
            outputFiles.add(filename);
        }
        return super.shouldOverwrite(filename);
    }

    @Override
    public void processOpenAPI(OpenAPI openAPI) {
        if (format) {
            log.info("Google formatter source code");
            if (outputFiles != null && !outputFiles.isEmpty()) {
                Formatter gf = new Formatter();
                outputFiles.forEach(file -> {
                    try {
                        log.info("Formatter source code: {}", file);
                        Path path = Paths.get(file);
                        String sourceString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                        sourceString = gf.formatSource(sourceString);
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

    private boolean updateCodegenExtraAnnotationList(String name) {
        if (additionalProperties.containsKey(name)) {

            List<String> items = (List<String>) additionalProperties.get(name);
            if (items != null && !items.isEmpty()) {
                List<CodegenExtraAnnotation> data = new ArrayList<>();
                items.forEach(item -> {
                    int index = item.lastIndexOf('.');
                    CodegenExtraAnnotation p = new CodegenExtraAnnotation();
                    String tmp = item;
                    int index2 = item.lastIndexOf('(');
                    if (index2 > -1) {
                        tmp = tmp.substring(0, index2);
                    }
                    p.setImports(tmp);
                    p.setName(item.substring(index + 1));
                    data.add(p);
                });
                additionalProperties.put(name, data);
                return true;
            }
        }
        return false;
    }

    @Override
    public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, Map<String, Schema> schemas, OpenAPI openAPI) {
        CodegenOperation op = super.fromOperation(path, httpMethod, operation, schemas, openAPI);
        ExtCodegenOperation e = new ExtCodegenOperation(op);
        e.setBeanParamName(camelize(op.operationId) + beanParamSuffix);
        if (e.beanParams.size() >= beanParamCount) {
            e.setBeanParam(true);
        }
        return e;
    }

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
}
