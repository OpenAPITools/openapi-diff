package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.OpenApiDiff;
import com.qdesrame.openapi.diff.model.ChangedEndpoint;
import com.qdesrame.openapi.diff.model.Endpoint;
import com.qdesrame.openapi.diff.output.HtmlRender;
import com.qdesrame.openapi.diff.output.MarkdownRender;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OpenApiDiffTest {

    private final String OPENAPI_DOC1 = "petstore_v2_1.yaml";
    private final String OPENAPI_DOC2 = "petstore_v2_2.yaml";
    private final String OPENAPI_EMPTY_DOC = "petstore_v2_empty.yaml";
    final String SWAGGER_V2_HTTP = "http://petstore.swagger.io/v2/swagger.json";

    @Test
    public void testEqual() {
        OpenApiDiff diff = OpenApiDiff.compare(OPENAPI_DOC2, OPENAPI_DOC2);
        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        List<ChangedEndpoint> changedEndpoints = diff.getChangedEndpoints();
        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndpoints.isEmpty());

    }

    @Test
    public void testNewApi() {
        OpenApiDiff diff = OpenApiDiff.compare(OPENAPI_EMPTY_DOC, OPENAPI_DOC2);
        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = diff.getChangedEndpoints();
        String html = new HtmlRender("Changelog",
                "http://deepoove.com/swagger-diff/stylesheets/demo.css")
                .render(diff);

        try {
            FileWriter fw = new FileWriter(
                    "target/testNewApi.html");
            fw.write(html);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(newEndpoints.size() > 0);
        Assert.assertTrue(missingEndpoints.isEmpty());
        Assert.assertTrue(changedEndPoints.isEmpty());

    }

    @Test
    public void testDeprecatedApi() {
        OpenApiDiff diff = OpenApiDiff.compare(OPENAPI_DOC1, OPENAPI_EMPTY_DOC);
        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        List<ChangedEndpoint> changedEndPoints = diff.getChangedEndpoints();
        String html = new HtmlRender("Changelog",
                "http://deepoove.com/swagger-diff/stylesheets/demo.css")
                .render(diff);

        try {
            FileWriter fw = new FileWriter(
                    "target/testDeprecatedApi.html");
            fw.write(html);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(newEndpoints.isEmpty());
        Assert.assertTrue(missingEndpoints.size() > 0);
        Assert.assertTrue(changedEndPoints.isEmpty());

    }

    @Test
    public void testDiff() {
        OpenApiDiff diff = OpenApiDiff.compare(OPENAPI_DOC1, OPENAPI_DOC2);
        List<ChangedEndpoint> changedEndPoints = diff.getChangedEndpoints();
        String html = new HtmlRender("Changelog",
                "http://deepoove.com/swagger-diff/stylesheets/demo.css")
                .render(diff);
        try {
            FileWriter fw = new FileWriter(
                    "target/testDiff.html");
            fw.write(html);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertFalse(changedEndPoints.isEmpty());

    }

    @Test
    public void testDiffAndMarkdown() {
        OpenApiDiff diff = OpenApiDiff.compare(OPENAPI_DOC1, OPENAPI_DOC2);
        String render = new MarkdownRender().render(diff);
        try {
            FileWriter fw = new FileWriter(
                    "target/testDiff.md");
            fw.write(render);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
