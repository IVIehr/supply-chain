package ir.alimojahed.general.elasticwrapper.data.elastic;

import ir.alimojahed.general.elasticwrapper.domain.model.dto.LogDto;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ElasticSearchQuery {

    public static ElasticsearchResult performElasticsearchQuery(
            String cargoId, int minRange, int maxRange, String elasticsearchHost, int elasticsearchPort,
            String elasticsearchUsername, String elasticsearchPassword) throws Exception {

        // Create a basic credentials provider for basic authentication
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(org.apache.http.auth.AuthScope.ANY,
                new org.apache.http.auth.UsernamePasswordCredentials(elasticsearchUsername, elasticsearchPassword));

        // Create an HttpClient with basic authentication
        HttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        // Build the Elasticsearch query
        JSONObject query = buildElasticsearchQuery(cargoId, minRange, maxRange);

        // Create an HTTP POST request
        HttpPost httpPost = new HttpPost("http://" + elasticsearchHost + ":" + elasticsearchPort + "/mehr_iot_log/_search");
        httpPost.setConfig(RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(5000).build());
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(query.toString(), ContentType.APPLICATION_JSON));

        // Execute the HTTP request
        HttpResponse response = httpClient.execute(httpPost);

        // Parse the response
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        System.out.println(responseString);
        JSONObject jsonResponse = new JSONObject(responseString);
        ElasticsearchResult result = new ElasticsearchResult();
        long totalCount  = jsonResponse.getJSONObject("hits").getJSONObject("total").getLong("value");
        result.setTotalCount(totalCount);

        if (totalCount > 0) {
            // Extract aggregation results
            JSONObject aggregations = jsonResponse.getJSONObject("aggregations");
            double avgHumidity = aggregations.getJSONObject("avg_humidity").getDouble("value");
            double avgTemperature = aggregations.getJSONObject("avg_temperature").getDouble("value");
            long alertTrueCount = aggregations.getJSONObject("alert_true_count").getLong("doc_count");
            String firstInsertTime = aggregations.getJSONObject("first_insert_time").getString("value_as_string");
            String lastInsertTime = aggregations.getJSONObject("last_insert_time").getString("value_as_string");

            // Create and return a POJO with the results
            return ElasticsearchResult.builder()
                    .averageTemperature(avgTemperature)
                    .startTime(firstInsertTime)
                    .endTime(lastInsertTime)
                    .alertsCount(alertTrueCount)
                    .averageHumidity(avgHumidity)
                    .totalCount(totalCount)
                    .build();
        }

        return result;
    }

    private static JSONObject buildElasticsearchQuery(String cargoId, int minRange, int maxRange) {
        JSONObject whole = new JSONObject();

        JSONObject query = new JSONObject();

        JSONObject boolQuery = new JSONObject();
        query.put("bool", boolQuery);

        JSONArray filter = new JSONArray();
        boolQuery.put("filter", filter);


        filter.put(new JSONObject().put("term", new JSONObject().put("cargoId", cargoId)));

        JSONObject range = new JSONObject();
        range.put("stateId", new JSONObject().put("gte", minRange).put("lte", maxRange));
        filter.put(new JSONObject().put("range", range));

        JSONObject aggs = new JSONObject();
        whole.put("aggs", aggs);

        aggs.put("avg_humidity", new JSONObject().put("avg", new JSONObject().put("field", "humidity")));
        aggs.put("avg_temperature", new JSONObject().put("avg", new JSONObject().put("field", "temperature")));
        aggs.put("alert_true_count", new JSONObject().put("filter", new JSONObject().put("term", new JSONObject().put("alert", true))));
        aggs.put("first_insert_time", new JSONObject().put("min", new JSONObject().put("field", "insertTime")));
        aggs.put("last_insert_time", new JSONObject().put("max", new JSONObject().put("field", "insertTime")));

        whole.put("query", query);
        whole.put("size", 0);
        System.out.println(whole);
        return whole;
    }


    public static void saveLogDtoToElasticsearch(LogDto logDto, String elasticsearchHost, int elasticsearchPort,
                                          String elasticsearchUsername, String elasticsearchPassword) throws Exception {

        // Create a basic credentials provider for basic authentication
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(org.apache.http.auth.AuthScope.ANY,
                new org.apache.http.auth.UsernamePasswordCredentials(elasticsearchUsername, elasticsearchPassword));

        // Create an HttpClient with basic authentication
        HttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        // Convert the LogDto to a JSON object
        JSONObject logJson = new JSONObject(logDto);

        // Create an HTTP POST request to index the LogDto
        HttpPost httpPost = new HttpPost("http://" + elasticsearchHost + ":" + elasticsearchPort + "/mehr_iot_log/_doc");
        httpPost.setConfig(RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(5000).build());
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(logJson.toString(), ContentType.APPLICATION_JSON));

        // Execute the HTTP request
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        System.out.println(responseString);
        // Handle the response as needed (e.g., check for success)
        // ...
    }



}
