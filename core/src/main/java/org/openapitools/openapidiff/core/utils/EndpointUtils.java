package org.openapitools.openapidiff.core.utils;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.openapitools.openapidiff.core.model.Endpoint;

public class EndpointUtils {

  private EndpointUtils() {}

  public static Collection<Endpoint> convert2Endpoints(
      String pathUrl, Map<PathItem.HttpMethod, Operation> map) {
    List<Endpoint> endpoints = new ArrayList<>();
    if (null == map) {
      return endpoints;
    }
    for (Map.Entry<PathItem.HttpMethod, Operation> entry : map.entrySet()) {
      PathItem.HttpMethod httpMethod = entry.getKey();
      Operation operation = entry.getValue();
      Endpoint endpoint = convert2Endpoint(pathUrl, httpMethod, operation);
      endpoints.add(endpoint);
    }
    return endpoints;
  }

  public static Endpoint convert2Endpoint(
      String pathUrl, PathItem.HttpMethod httpMethod, Operation operation) {
    Endpoint endpoint = new Endpoint();
    endpoint.setPathUrl(pathUrl);
    endpoint.setMethod(httpMethod);
    endpoint.setSummary(operation.getSummary());
    endpoint.setOperation(operation);
    return endpoint;
  }

  public static List<Endpoint> convert2EndpointList(Map<String, PathItem> map) {
    List<Endpoint> endpoints = new ArrayList<>();
    if (null == map) {
      return endpoints;
    }
    for (Map.Entry<String, PathItem> entry : map.entrySet()) {
      String url = entry.getKey();
      PathItem path = entry.getValue();

      Map<PathItem.HttpMethod, Operation> operationMap = path.readOperationsMap();
      for (Map.Entry<PathItem.HttpMethod, Operation> entryOper : operationMap.entrySet()) {
        PathItem.HttpMethod httpMethod = entryOper.getKey();
        Operation operation = entryOper.getValue();

        Endpoint endpoint = new Endpoint();
        endpoint.setPathUrl(url);
        endpoint.setMethod(httpMethod);
        endpoint.setSummary(operation.getSummary());
        endpoint.setPath(path);
        endpoint.setOperation(operation);
        endpoints.add(endpoint);
      }
    }
    return endpoints;
  }
}
