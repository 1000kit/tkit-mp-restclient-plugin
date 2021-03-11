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


import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenParameter;
import io.swagger.codegen.v3.VendorExtendable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The extends code generation for the operation.
 */
public class ExtCodegenOperation extends CodegenOperation {

    /**
     * The bean parameters list.
     */
    @Getter @Setter
    public List<CodegenParameter> beanParams;

    /**
     * The check of the beanParam.
     */
    @Getter @Setter
    public boolean beanParam;

    /**
     * The beanParam attribute name.
     */
    @Getter @Setter
    public String beanParamName;

    /**
     * The complex query params list.
     */
    @Getter @Setter
    public List<CodegenParameter> complexQueryParams;

    private final String COMPLEX_QUERY_PARAM_KEY = "complex-query-param";

    /**
     * The default constructor.
     * @param op the original code gen operation.
     */
    public ExtCodegenOperation(CodegenOperation op, Map<String, CodegenModel> codegenModelMap) {
        responseHeaders.addAll(op.responseHeaders);
        returnTypeIsPrimitive = op.returnTypeIsPrimitive;
        returnSimpleType = op.returnSimpleType;
        subresourceOperation = op.subresourceOperation;
        path = op.path;
        operationId = op.operationId;
        returnType = op.returnType;
        httpMethod = op.httpMethod;
        returnBaseType = op.returnBaseType;
        returnContainer = op.returnContainer;
        summary = op.summary;
        unescapedNotes = op.unescapedNotes;
        notes = op.notes;
        baseName = op.baseName;
        defaultResponse = op.defaultResponse;
        testPath = op.testPath;
        discriminator = op.discriminator;
        consumes = op.consumes;
        produces = op.produces;
        prioritizedContentTypes = op.prioritizedContentTypes;
        bodyParam = op.bodyParam;
        contents = op.contents;
        allParams = op.allParams;
        bodyParams = op.bodyParams;
        pathParams = op.pathParams;
        queryParams = op.queryParams;
        headerParams = op.headerParams;
        formParams = op.formParams;
        requiredParams = op.requiredParams;
        authMethods = op.authMethods;
        tags = op.tags;
        responses = op.responses;
        imports = op.imports;
        examples = op.examples;
        requestBodyExamples = op.requestBodyExamples;
        externalDocs = op.externalDocs;
        nickname = op.nickname;
        operationIdLowerCase = op.operationIdLowerCase;
        operationIdCamelCase = op.operationIdCamelCase;
        operationIdSnakeCase = op.operationIdSnakeCase;
        vendorExtensions = op.vendorExtensions;

        beanParams = new ArrayList<>();
        complexQueryParams = new ArrayList<>();
        if (pathParams != null) {
            beanParams.addAll(pathParams);
        }
        if (queryParams != null) {
            beanParams.addAll(queryParams);

            complexQueryParams = queryParams.stream()
                    .filter(queryParam -> isQueryParamAComplexModel(codegenModelMap, queryParam))
                    .collect(Collectors.toList());

            complexQueryParams.forEach(this::markAsComplexQueryParam);
            allParams.forEach(this::markIfComplexQueryParam);

            queryParams.removeAll(complexQueryParams);
        }
        if (headerParams != null) {
            beanParams.addAll(headerParams);
        }
        if (formParams != null) {
            beanParams.addAll(formParams);
        }
    }

    private void markAsComplexQueryParam(CodegenParameter param) {
        param.getVendorExtensions().put(VendorExtendable.PREFIX_IS + COMPLEX_QUERY_PARAM_KEY, true);
    }

    private void markIfComplexQueryParam(CodegenParameter param) {
        boolean isComplex = complexQueryParams.stream()
                .anyMatch(complexQueryParam -> isEquals(param, complexQueryParam));
        if (isComplex) {
            markAsComplexQueryParam(param);
        }
    }

    private boolean isQueryParamAComplexModel(Map<String, CodegenModel> codegenModelMap, CodegenParameter param) {
        if (codegenModelMap.containsKey(param.getDataType())) {
            CodegenModel codegenModel = codegenModelMap.get(param.getDataType());
            return codegenModel.getBooleanValue(VendorExtendable.PREFIX_IS + COMPLEX_QUERY_PARAM_KEY);
        }
        return false;
    }

    private boolean isEquals(CodegenParameter param, CodegenParameter complexQueryParam) {
        return complexQueryParam.getParamName().equals(param.getParamName())
                && complexQueryParam.getIsQueryParam().equals(param.getIsQueryParam());
    }
}
