package com.qdesrame.openapi.diff.output;

import com.qdesrame.openapi.diff.model.*;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class MarkdownRender implements Render {

    public static final Logger LOGGER = LoggerFactory.getLogger(MarkdownRender.class);

    private final String H3 = "### ";
    private final String H4 = "#### ";
    private final String H5 = "##### ";
    private final String H6 = "###### ";
    private final String BLOCKQUOTE = "> ";
    private final String CODE = "`";
    private final String PRE_CODE = "    ";
    private final String PRE_LI = "    ";
    private final String LI = "* ";
    private final String HR = "---\n";

    private static RefPointer<Schema> refPointer = new RefPointer<>(RefType.SCHEMAS);
    private ChangedOpenApi diff;

    public MarkdownRender() {
    }

    public String render(ChangedOpenApi diff) {
        this.diff = diff;
        StringBuilder sb = new StringBuilder();
        sb.append(listEndpoints("What's New", diff.getNewEndpoints()))
                .append(listEndpoints("What's Deleted", diff.getMissingEndpoints()))
                .append(listEndpoints("What's Deprecated", diff.getDeprecatedEndpoints()))
                .append(listEndpoints(diff.getChangedOperations()));
        return sb.toString();
    }

    private String sectionTitle(String title) {
        return H4 + title + '\n' + HR + '\n';
    }

    private String listEndpoints(String title, List<Endpoint> endpoints) {
        if (null == endpoints || endpoints.size() == 0) return "";
        StringBuilder sb = new StringBuilder(sectionTitle(title));
        endpoints.stream().map(e -> itemEndpoint(e.getMethod().toString(), e.getPathUrl(), e.getSummary())).forEach(sb::append);
        return sb.toString();
    }

    private String itemEndpoint(String method, String path, String summary) {
        StringBuilder sb = new StringBuilder();
        sb.append(H5).append(CODE).append(method).append(CODE).append(" ")
                .append(path).append("\n\n").append(description(summary)).append("\n");
        /*.append(desc.replaceAll("\n", "\n> "))*/
        return sb.toString();
    }

    private String titleH5(String title) {
        return H6 + title + '\n';
    }

    private String listEndpoints(List<ChangedOperation> changedOperations) {
        if (null == changedOperations || changedOperations.size() == 0) return "";
        StringBuilder sb = new StringBuilder(sectionTitle("What's Changed"));
        changedOperations.stream().map(operation -> {
            StringBuilder details = new StringBuilder()
                    .append(itemEndpoint(operation.getHttpMethod().toString(), operation.getPathUrl(), operation.getSummary()));
            if (operation.isChangedParam().isDifferent()) {
                details.append(titleH5("Parameters:")).append(parameters(operation.getChangedParameters()));
            }
            if (operation.isChangedRequest().isDifferent()) {
                details.append(titleH5("Request:")).append(bodyContent(operation.getChangedRequestBody().getChangedContent()));
            }
            if (operation.isChangedResponse().isDifferent()) {
                details.append(titleH5("Return Type:")).append(responses(operation.getChangedApiResponse()));
            }
            return details.toString();
        }).forEach(sb::append);
        return sb.toString();
    }

    private String responses(ChangedApiResponse changedApiResponse) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(listResponse("New response", changedApiResponse.getAddResponses()));
        sb.append(listResponse("Deleted response", changedApiResponse.getMissingResponses()));
        changedApiResponse.getChangedResponses().entrySet().stream().map(e -> this.itemResponse(e.getKey(), e.getValue())).forEach(sb::append);
        return sb.toString();
    }

    private String listResponse(String title, Map<String, ApiResponse> responses) {
        StringBuilder sb = new StringBuilder();
        responses.entrySet().stream().map(e -> this.itemResponse(title, e.getKey(), e.getValue())).forEach(sb::append);
        return sb.toString();
    }

    private String itemResponse(String title, String code, ApiResponse response) {
        return this.itemResponse(title, code, response.getDescription());
    }

    private String itemResponse(String code, ChangedResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.itemResponse("Changed response", code, null == response.getNewApiResponse() ?
                "" : response.getNewApiResponse().getDescription()));
        sb.append(headers(response.getChangedHeaders()));
        if (response.getChangedContent() != null) {
            sb.append(this.bodyContent(LI, response.getChangedContent()));
        }
        return sb.toString();
    }

    private String itemResponse(String title, String code, String description) {
        StringBuilder sb = new StringBuilder("");
        String status = "";
        if (!code.equals("default")) {
            status = HttpStatus.getStatusText(Integer.parseInt(code));
        }
        sb.append(format("%s : **%s %s**\n", title, code, status));
        sb.append(description(description));
        return sb.toString();
    }

    private String headers(ChangedHeaders headers) {
        StringBuilder sb = new StringBuilder("");
        if (headers != null) {
            sb.append(listHeader("New header", headers.getIncreased()));
            sb.append(listHeader("Deleted header", headers.getMissing()));
            headers.getChanged().entrySet().stream().map(e -> this.itemHeader(e.getKey(), e.getValue())).forEach(sb::append);
        }
        return sb.toString();
    }

    private String listHeader(String title, Map<String, Header> headers) {
        StringBuilder sb = new StringBuilder();
        headers.entrySet().stream().map(e -> this.itemHeader(title, e.getKey(), e.getValue())).forEach(sb::append);
        return sb.toString();
    }

    private String itemHeader(String title, String name, Header header) {
        return this.itemHeader(title, name, header.getDescription());
    }

    private String itemHeader(String code, ChangedHeader header) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.itemHeader("Changed header", code, null == header.getNewHeader() ?
                "" : header.getNewHeader().getDescription()));
        return sb.toString();
    }

    private String itemHeader(String title, String mediaType, String description) {
        StringBuilder sb = new StringBuilder("");
        sb.append(format("%s : `%s`\n\n", title, mediaType));
        sb.append(description(description)).append('\n');
        return sb.toString();
    }

    private String bodyContent(String prefix, ChangedContent changedContent) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(listContent(prefix, "New content type", changedContent.getIncreased()));
        sb.append(listContent(prefix, "Deleted content type", changedContent.getMissing()));
        final int deepness;
//        if (StringUtils.isNotBlank(prefix)) {
//            deepness = 1;
//        } else {
        deepness = 0;
//        }
        changedContent.getChanged().entrySet().stream().findFirst().map(e -> this.itemContent(deepness, e.getKey(), e.getValue())).map(sb::append);
        return sb.toString();
    }

    private String bodyContent(ChangedContent changedContent) {
        return bodyContent("", changedContent);
    }

    private String listContent(String prefix, String title, Map<String, MediaType> mediaTypes) {
        StringBuilder sb = new StringBuilder();
        mediaTypes.entrySet().stream().findFirst().map(e -> this.itemContent(title, e.getKey(), e.getValue())).map(e -> sb.append(prefix).append(e));
        return sb.toString();
    }

    private String itemContent(String title, String mediaType) {
        StringBuilder sb = new StringBuilder("");
        sb.append(format("%s : `%s`\n\n", title, mediaType));
        return sb.toString();
    }

    private String itemContent(String title, String mediaType, MediaType content) {
        StringBuilder sb = new StringBuilder("");
        sb.append(itemContent(title, mediaType));
        return sb.toString();
    }

    private String itemContent(int deepness, String mediaType, ChangedMediaType content) {
        StringBuilder sb = new StringBuilder("");
//        sb.append(itemContent("Changed content type", mediaType));
        String schema = schema(deepness, content.getChangedSchema());
        if (StringUtils.isNotBlank(schema)) {
            sb.append(schema);
        } else {
            sb.append(indent(deepness)).append("> **/!\\ TODO /!\\**\n\n");
        }
        return sb.toString();
    }

    private String schema(ChangedSchema schema) {
        return schema(1, schema);
    }

    private static boolean isDisplayed(ChangedSchema schema) {
        return isDisplayed(schema.getNewSchema(), schema.getContext());
    }

    private static boolean isDisplayed(Schema schema, DiffContext context) {
        return !(Boolean.TRUE.equals(schema.getWriteOnly()) && context.isResponse())
                || !(Boolean.TRUE.equals(schema.getReadOnly()) && context.isRequest());
    }

    private String oneOfSchema(int deepness, ChangedOneOfSchema schema, String discriminator) {
        StringBuilder sb = new StringBuilder("");
        sb.append(format("%sSwitch `%s`:\n", indent(deepness), discriminator));
        schema.getMissingMapping().keySet()
                .forEach(key -> sb.append(format("%s- Removed '%s'\n", indent(deepness), key)));
        schema.getIncreasedMapping().forEach((key, sub) ->
                sb.append(format("%s- Added '%s':\n", indent(deepness), key)).append(schema(deepness + 1, sub, schema.getContext())));
        schema.getChangedMapping().forEach((key, sub) ->
                sb.append(format("%s- Updated `%s`:\n", indent(deepness), key))
                        .append(schema(deepness + 1, sub)));
        return sb.toString();
    }

    private String required(int deepness, String title, List<String> required) {
        StringBuilder sb = new StringBuilder("");
        if (required.size() > 0) {
            sb.append(format("%s%s:\n", indent(deepness), title));
            required.forEach(s -> sb.append(format("%s- `%s`\n", indent(deepness), s)));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String schema(int deepness, ChangedSchema schema) {
        StringBuilder sb = new StringBuilder("");
        if (!isDisplayed(schema)) {
            return sb.toString();
        }
        if (schema.isDiscriminatorPropertyChanged()) {
            LOGGER.debug("Discriminator property changed");
        }
        if (schema.getChangedOneOfSchema() != null) {
            String discriminator = schema.getNewSchema().getDiscriminator() != null ?
                    schema.getNewSchema().getDiscriminator().getPropertyName() : "";
            sb.append(oneOfSchema(deepness, schema.getChangedOneOfSchema(), discriminator));
        }
        if (schema.getChangeRequired() != null) {
            sb.append(required(deepness, "New required properties", schema.getChangeRequired().getIncreased()));
            sb.append(required(deepness, "New optional properties", schema.getChangeRequired().getMissing()));
        }
        sb.append(listDiff(deepness, "enum", schema.getChangeEnum()));
        sb.append(properties(deepness, "Added property", schema.getIncreasedProperties(), true, schema.getContext()));
        sb.append(properties(deepness, "Deleted property", schema.getMissingProperties(), false, schema.getContext()));
        schema.getChangedProperties().forEach((name, property) -> sb.append(property(deepness, name, property)));
        return sb.toString();
    }

    private String schema(int deepness, ComposedSchema schema, DiffContext context) {
        StringBuilder sb = new StringBuilder("");
        if (schema.getAllOf() != null && schema.getAllOf() != null) {
            LOGGER.debug("All of schema");
            schema.getAllOf().stream()
                    .map(this::resolve)
                    .forEach(composedChild -> sb.append(schema(deepness, composedChild, context)));
        }
        if (schema.getOneOf() != null && schema.getOneOf() != null) {
            LOGGER.debug("One of schema");
            sb.append(format("%sOne of:\n\n", indent(deepness)));
            schema.getOneOf().stream()
                    .map(this::resolve)
                    .forEach(composedChild -> sb.append(schema(deepness + 1, composedChild, context)));
        }
        return sb.toString();
    }

    private String schema(int deepness, Schema schema, DiffContext context) {
        StringBuilder sb = new StringBuilder("");
        if (!isDisplayed(schema, context)) {
            return sb.toString();
        }
        sb.append(listItem(deepness, "Enum", schema.getEnum()));
        sb.append(properties(deepness, "Property", schema.getProperties(), true, context));
        if (schema instanceof ComposedSchema) {
            sb.append(schema(deepness, (ComposedSchema) schema, context));
        } else if (schema instanceof ArraySchema) {
            sb.append(items(deepness, resolve(((ArraySchema) schema).getItems()), context));
        }
        return sb.toString();
    }

    private String property(int deepness, String name, ChangedSchema schema) {
        StringBuilder sb = new StringBuilder();
        String type = type(schema.getNewSchema());
        if (schema.isChangedType()) {
            type = type(schema.getOldSchema()) + " -> " + type(schema.getNewSchema());
        }
        sb.append(property(deepness, "Changed property", name, type, schema.getNewSchema().getDescription()));
        String strSchema = schema(++deepness, schema);
        if (StringUtils.isNotBlank(strSchema)) {
            sb.append(strSchema);
        } else {
            sb.append(indent(deepness)).append("> ** /!\\ TODO /!\\ **\n\n");
        }
        return sb.toString();
    }

    private String property(int deepness, String title, String name, Schema schema) {
        return property(deepness, title, name, type(schema), schema.getDescription());
    }

    private String property(int deepness, String title, String name, String type, String description) {
        return format("%s* %s `%s` (%s)\n%s\n", indent(deepness), title, name, type, description(indent(deepness + 1), description));
    }

    private String listDiff(int deepness, String name, ListDiff listDiff) {
        if (listDiff == null) {
            return "";
        }
        return listItem(deepness, "Added " + name, listDiff.getIncreased()) +
                listItem(deepness, "Removed " + name, listDiff.getMissing());
    }

    private <T> String listItem(int deepness, String name, List<T> list) {
        StringBuilder sb = new StringBuilder("");
        if (list != null && list.size() > 0) {
            sb.append(format("%s%s value%s:\n\n", indent(deepness), name, list.size() > 1 ? "s" : ""));
            list.forEach(p -> sb.append(format("%s* `%s`\n", indent(deepness), p)));
        }
        return sb.toString();
    }

    private String parameters(ChangedParameters changedParameters) {
        List<ChangedParameter> changed = changedParameters.getChanged();
        StringBuilder sb = new StringBuilder("\n");
        sb.append(listParameter("Added", changedParameters.getIncreased()))
                .append(listParameter("Deleted", changedParameters.getMissing()));
        changed.stream().map(this::itemParameter).forEach(sb::append);
        return sb.toString();
    }

    private String listParameter(String title, List<Parameter> parameters) {
        StringBuilder sb = new StringBuilder("");
        parameters.stream().map(p -> itemParameter(title, p)).forEach(sb::append);
        return sb.toString();
    }

    private String itemParameter(String title, Parameter parameter) {
        return this.itemParameter(title, parameter.getName(), parameter.getIn(), parameter.getDescription());
    }

    private String itemParameter(String title, String name, String in, String description) {
        return format("%s: ", title) + code(name) + " in " + code(in) + '\n' + description(description) + '\n';
    }

    private String itemParameter(ChangedParameter param) {
        Parameter rightParam = param.getNewParameter();
        if (param.isDeprecated()) {
            return itemParameter("Deprecated", rightParam.getName(), rightParam.getIn(), rightParam.getDescription());
        }
        return itemParameter("Changed", rightParam.getName(), rightParam.getIn(), rightParam.getDescription());
    }

    private String code(String string) {
        return CODE + string + CODE;
    }

    private String description(String description) {
        return description("", description);
    }

    private String description(String beginning, String description) {
        String result = "";
        if (StringUtils.isBlank(description)) {
            description = "";
        }
        String blockquote = beginning + BLOCKQUOTE;
        if (!description.equals("")) {
            result = blockquote + description.trim().replaceAll("\n", "\n" + blockquote) + '\n';
        }
        return result;
    }

    private String type(Schema schema) {
        String result = "object";
        if (schema instanceof ArraySchema) {
            result = "array";
        } else if (schema.getType() != null) {
            result = schema.getType();
        }
        return result;
    }

    private String indent(int deepness) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deepness; i++) {
            sb.append(PRE_LI);
        }
        return sb.toString();
    }

    private Schema resolve(Schema schema) {
        return refPointer.resolveRef(diff.getNewSpecOpenApi().getComponents(), schema, schema.get$ref());
    }

    private String items(int deepness, Schema schema, DiffContext context) {
        StringBuilder sb = new StringBuilder("");
        sb.append(format("%sItems (%s)%s\n", indent(deepness), type(schema), Arrays.asList("object", "array").contains(type(schema)) ? " :\n" : ""));
        description(indent(deepness + 1), schema.getDescription());
        sb.append(schema(deepness, schema, context));
        return sb.toString();
    }

    private String properties(final int deepness, String title, Map<String, Schema> properties, boolean showContent, DiffContext context) {
        StringBuilder sb = new StringBuilder("");
        if (properties != null) {
            properties.forEach((key, value) -> {
                sb.append(property(deepness, title, key, resolve(value)));
                if (showContent) {
                    sb.append(schema(deepness + 1, resolve(value), context));
                }
            });
        }
        return sb.toString();
    }
}
