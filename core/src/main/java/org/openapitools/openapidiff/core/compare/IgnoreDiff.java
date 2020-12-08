package org.openapitools.openapidiff.core.compare;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.exception.ReadContentException;
import io.swagger.v3.parser.util.ClasspathHelper;
import io.swagger.v3.parser.util.RemoteUrl;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.SSLHandshakeException;
import org.apache.commons.io.FileUtils;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.Endpoint;

// [Parts of this file were taken from https://github.com/swagger-api/swagger-parser]

public class IgnoreDiff {
    private List<String> ignoredAttributesList;
    private String specPath;
    private String specString;
    private static ObjectMapper JSON_MAPPER, YAML_MAPPER;
    private static String encoding = StandardCharsets.UTF_8.displayName();
    static ObjectMapper mapper;
    private JsonNode rootNode;
    private JsonNode pathsNode;
    private Map<String, Set<String>> skippedPaths = new HashMap<String, Set<String>>();

    static {
        JSON_MAPPER = new JsonMapper();
        YAML_MAPPER = new YAMLMapper();
    }

    public IgnoreDiff(List<String> ignoredAttributesList, String specPath) {
        this.ignoredAttributesList = ignoredAttributesList;
        this.specPath = specPath;
        this.specString = readContentFromLocation(specPath, null);
        this.mapper = getRightMapper(specString);
        try {
            this.rootNode = mapper.readTree(specString);
            this.pathsNode = getNode("paths", rootNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    
    private String readContentFromLocation(String location, List<AuthorizationValue> auth) {
        final String adjustedLocation = location.replaceAll("\\\\", "/");
        try {
            if (adjustedLocation.toLowerCase().startsWith("http")) {
                return RemoteUrl.urlToString(adjustedLocation, auth);
            } else {
                final String fileScheme = "file:";
                final Path path = adjustedLocation.toLowerCase().startsWith(fileScheme) ?
                        Paths.get(URI.create(adjustedLocation)) : Paths.get(adjustedLocation);
                if (Files.exists(path)) {
                    return FileUtils.readFileToString(path.toFile(), encoding);
                } else {
                    return ClasspathHelper.loadFileFromClasspath(adjustedLocation);
                }
            }
        } catch (SSLHandshakeException e) {
            final String message = String.format(
                    "Unable to read location `%s` due to a SSL configuration error. It is possible that the server SSL certificate is invalid, self-signed, or has an untrusted Certificate Authority.",
                    adjustedLocation);
            throw new ReadContentException(message, e);
        } catch (Exception e) {
            throw new ReadContentException(String.format("Unable to read location `%s`", adjustedLocation), e);
        }
    }

    private ObjectMapper getRightMapper(String data) {
        if (data.trim().startsWith("{")) {
            return JSON_MAPPER;
        }
        return YAML_MAPPER;
    }

    private JsonNode getNode(String key, JsonNode parentNode) {
        JsonNode node = parentNode.get(key);
        return node;
    }

    public void setSkippedPaths() {
        List<String> httpVerbs = Stream.of("get", "put", "post", "delete", "patch").collect(Collectors.toList());

        Iterator<Map.Entry<String, JsonNode>> paths = this.pathsNode.fields();
        while (paths.hasNext()) {       
            Map.Entry<String, JsonNode> pathObj = (Map.Entry<String, JsonNode>) paths.next();
            String path = pathObj.getKey();
            JsonNode node = pathObj.getValue();
            Set<String> skippedVerbs = new HashSet<>();
            for (String attribute : ignoredAttributesList) {
                JsonNode childNode = getNode(attribute, node);
                if (childNode != null) {
                    skippedVerbs.add("all");
                    break;
                }
            }
            if (skippedVerbs.size() > 0) {
                this.skippedPaths.put(path, skippedVerbs);
                continue;
            }
            for (String httpVerb : httpVerbs) {
                JsonNode verbNode = getNode(httpVerb, node);
                if (verbNode != null) {
                    for (String attribute : ignoredAttributesList) {
                        JsonNode childNode = getNode(attribute, verbNode);
                        if (childNode != null) {
                            skippedVerbs.add(httpVerb);
                            break;
                        }
                    }
                }
            }
            if (skippedVerbs.size() > 0) {
                this.skippedPaths.put(path, skippedVerbs);
                continue;
            }
        }

    }

    public ChangedOpenApi removeSkippedPaths(ChangedOpenApi diff) {
        List<ChangedOperation> changedOperations = diff.getChangedOperations();
        List<ChangedOperation> filteredOperations = changedOperations.stream()
            .filter(
                operation -> {
                    String pathUrl = operation.getPathUrl();
                    String method = operation.getHttpMethod().toString();
                    Set<String> skippedPathObj = skippedPaths.get(pathUrl);
                    return (skippedPathObj == null || !( skippedPathObj.contains("all") || skippedPathObj.contains(method.toLowerCase()) ));
                }
            ).collect(Collectors.toList());
        diff.setChangedOperations(filteredOperations);

        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        List<Endpoint> filteredEndpoints = missingEndpoints.stream()
            .filter(
                endpoint -> {
                    String pathUrl = endpoint.getPathUrl();
                    String method = endpoint.getMethod().toString();
                    Set<String> skippedPathObj = skippedPaths.get(pathUrl);
                    return (skippedPathObj == null || !( skippedPathObj.contains("all") || skippedPathObj.contains(method.toLowerCase()) ));
                }
            ).collect(Collectors.toList());

        diff.setMissingEndpoints(filteredEndpoints);

        return diff;
    }

}