# tkit-mp-rest-client-plugin

Tkit microprofile  rest client generator plugin

[![License](https://img.shields.io/badge/license-Apache--2.0-green?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/org.tkit.maven/tkit-mp-restclient-plugin?logo=java&style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/org.tkit.maven/tkit-mp-restclient-plugin)

## Goal: codegen

```xml
			<plugin>
				<groupId>org.tkit.maven</groupId>
				<artifactId>tkit-mp-restclient-plugin</artifactId>
				<version>0.1.0-SNAPSHOT</version>
				<executions>
					<execution>
						<id>test</id>
						<goals>
							<goal>codegen</goal>
						</goals>
						<configuration>
							<inputSpec>src/main/resources/clients/openapi.yaml</inputSpec>
							<output>${project.build.directory}/generated-sources/mprestclient</output>
							<apiPackage>gen.org.tkit.test</apiPackage>
							<modelPackage>gen.org.tkit.test.model</modelPackage>
							<pathPrefix>v2/</pathPrefix>
							<fieldGen>LOMBOK</fieldGen>
							<jsonLib>JSONB</jsonLib>
							<providers>
								<provider>org.lorislab.quarkus.jel.log.interceptor.RestClientLogInterceptor</provider>
							</providers>
							<annotations>
								<annotation>org.lorislab.quarkus.jel.log.interceptor.LoggerService</annotation>
							</annotations>
							<modelAnnotations>
								<modelAnnotation>lombok.ToString</modelAnnotation>
								<modelAnnotation>io.quarkus.runtime.annotations.RegisterForReflection</modelAnnotation>
							</modelAnnotations>
							<configOptions>
								<sourceFolder>test</sourceFolder>
							</configOptions>
						</configuration>
					</execution>
				</executions>
			</plugin>
```

#### Parameters

The plugin extends the parameter from: [Swagger maven plugin](https://github.com/swagger-api/swagger-codegen/tree/master/modules/swagger-codegen-maven-plugin)
Extended parameters:

|  Name | Default  | Values | Description  |
|---|---|---|---|
| formatter | true | | The google source code formatter  |
| apiName | | | The api name |
| interfaceOnly | true | | The interface only |
| pathPrefix | | | The path prefix for all interfaces. Example 'v2/' |
| apiSuffix | RestClient | | The api interface suffix |
| providers | | | The list of rest client providers. Each provider is java fully qualified class name |
| annotations | | | The list of custom annotations for the interface. |
| modelAnnotations | | | The list of custom annotations for the model. |
| restClient | true | | The flag to generate the micro-profile rest client for the interface. |
| returnResponse | true | | The return type will be the Response. |
| beanParamSuffix | BeanParam | | The bean parameter suffix. |
| beanParamCount | 9 | | The number of the parameters to group by the bean parameter. |
| jsonLib | JSONB | JACKSON,JSONB | The JSON implementation. |
| fieldGen | PUBLIC | LOMBOK,GET_SET,PUBLIC | The model field generator type. |
| dateLibrary | java8 | | The date library. |
| useBeanValidation | true | | Use the bean validation on the methods. |
| apiInterfaceDoc | true | | Generate the micro-profile annotation on the generated interface. |

## Release

### Create a release

```bash
mvn semver-release:release-create
```

### Create a patch branch
```bash
mvn semver-release:patch-create -DpatchVersion=x.x.0
```
