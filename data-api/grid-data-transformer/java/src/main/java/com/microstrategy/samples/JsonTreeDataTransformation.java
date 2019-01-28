package com.microstrategy.samples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class for transforming the json tree data to array data(grid data), and the origin json data is the output of the endpoints POST /cubes/{cubeId}/instances,
 * POST /reports/{reportId}/instances,  GET /cubes/{cubeId}/instances/{instanceId} and GET /reports/{reportId}/instances/{instanceId}
 * Please use the Set function or constructor to input the parameter 'originTreeJsonStr', it is an json str for report or cube 's raw data, and it is json structure as below:
 *  originTreeJsonStr:
 *  {
 *   "id": "string",
 *   "name": "string",
 *   "instanceId": "string",
 *   "result": {
 *     "definition": {
 *       "attributes": [...],
 *       "metrics": [...],
 *       "thresholds": [],
 *       "sorting": [...]
 *     },
 *     "data": {
 *       "paging": {},
 *       "root": {
 *         "isPartial": "boolean",
 *         "children": [...]
 *       }
 *     }
 *   }
 * }
 *
 */
public class JsonTreeDataTransformation {

    private static final ObjectMapper mapper = new ObjectMapper();


    public static List<List<String>> transformTreeJsonToGridData(String originTreeJsonStr) throws IOException {
        List<List<String>> gridData = new ArrayList<>();
        JsonNode originJson = mapper.readTree(originTreeJsonStr);

        if (originJson != null && !(originJson.path("result").path("definition").isMissingNode()) && !(originJson.path("result").path("data").isMissingNode())) {
            JsonNode data = originJson.path("result").path("data");
            JsonNode definition = originJson.path("result").path("definition");
            ArrayNode attributes = (ArrayNode) definition.path("attributes");
            ArrayNode metrics = (ArrayNode) definition.path("metrics");

            if (attributes.size() == 0 && metrics.size() == 0) {
                return gridData;
            }

            List<String> attributeFormInfos = new ArrayList<>();
            List<String> metricInfos = new ArrayList<>();

            //collect attributeForms info
            attributes.forEach((attribute) -> {
                if (!(attribute.path("forms").isMissingNode())) {
                    ((ArrayNode) attribute.path("forms")).forEach((form) -> {
                        attributeFormInfos.add(attribute.path("name").asText() + '@' + form.path("name").asText());  //multi-form feature case
                    });
                } else {
                    attributeFormInfos.add(attribute.path("name").asText()); //single-form feature case
                }
            });
            //collect metrics info
            metrics.forEach((metric) -> {
                metricInfos.add(metric.path("name").asText());
            });

            List<String> headerTittleRow = new ArrayList<>();
            headerTittleRow.addAll(attributeFormInfos);
            headerTittleRow.addAll(metricInfos);
            gridData.add(headerTittleRow);


            List<String> dataCellRow = new ArrayList<>();
            if (!(data.path("root").isMissingNode())) {
                deepVisitTree(data.path("root"), gridData, dataCellRow);
            }

        }

        return gridData;

    }

    private static void deepVisitTree(JsonNode node, List<List<String>> gridData, List<String> row) {
        if (node.isMissingNode()) {
            List<String> copyRow = new ArrayList<>(row);
            gridData.add(copyRow);
            return;
        }

        ArrayNode children = ((ObjectNode)node).withArray("children");
        JsonNode element = node.path("element");
        JsonNode metrics = node.path("metrics");

        List<String> metricNames = new ArrayList<>();
        List<String> formNames = new ArrayList<>();
        if (!element.isMissingNode()) {
            if (!element.path("formValues").isMissingNode()) {
                //multi-form: add the attribute form values to row
                JsonNode formValueMap = element.path("formValues");

                formValueMap.fields().forEachRemaining((formValue) -> {
                    formNames.add(formValue.getKey());
                    row.add(formValue.getValue().asText());
                });

            } else {
                row.add(element.path("name").asText());
            }
        }


        if (!metrics.isMissingNode()) { //if metric exists, need handle metric value
            //add the metric values to row
            metrics.fieldNames().forEachRemaining((metricName) -> {
                metricNames.add(metricName);
            });

            for (String metricName : metricNames) {
                row.add(metrics.path(metricName).path("rv").asText());
            }
        }

        if (children.size() == 0) {
            children = mapper.createArrayNode();
            children.add(MissingNode.getInstance()); //here current node is an leafNode, making leaf node' s child an MissingNode
        }

        for (JsonNode childNode : children) {
            deepVisitTree(childNode, gridData, row);
        }

        if (!metrics.isMissingNode()) { //if metric exists, here need to pop metric value from row
            for (int i = 0; i < metricNames.size(); i++) {
                row.remove(row.size() - 1);
            }
        }

        if (!element.isMissingNode()) {//if attribute forms exists, here need to pop attribute forms from row
            if (!formNames.isEmpty()) {
                for (int i = 0; i < formNames.size(); i++) {
                    row.remove(row.size() - 1); //multi-form feature case
                }
            } else {
                row.remove(row.size() - 1); //single-form  feature case
            }
        }

    }

    public static void main(String... args) {
        //samples json string
        String originTreeJsonStr = "{\"id\":\"8CCD8D9D4051A4C533C719A6590DEED8\",\"name\":\"Intelligent Cube - Drilling outside the cube is disabled\",\"instanceId\":\"F98D0E4E11E8E277E12F0080EF75D2A3\",\"result\":{\"definition\":{\"attributes\":[{\"name\":\"Category\",\"id\":\"8D679D3711D3E4981000E787EC6DE8A4\",\"type\":\"Attribute\",\"forms\":[{\"id\":\"CCFBE2A5EADB4F50941FB879CCF1721C\",\"name\":\"DESC\",\"dataType\":\"Char\"}]},{\"name\":\"Subcategory\",\"id\":\"8D679D4F11D3E4981000E787EC6DE8A4\",\"type\":\"Attribute\",\"forms\":[{\"id\":\"CCFBE2A5EADB4F50941FB879CCF1721C\",\"name\":\"DESC\",\"dataType\":\"Char\"}]}],\"metrics\":[],\"thresholds\":[],\"sorting\":[]},\"data\":{\"paging\":{\"total\":24,\"current\":20,\"offset\":0,\"limit\":20,\"prev\":null,\"next\":null},\"root\":{\"isPartial\":true,\"children\":[{\"depth\":0,\"element\":{\"attributeIndex\":0,\"formValues\":{\"DESC\":\"Books\"},\"name\":\"Books\",\"id\":\"h1;8D679D3711D3E4981000E787EC6DE8A4\"},\"isPartial\":false,\"children\":[{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Art & Architecture\"},\"name\":\"Art & Architecture\",\"id\":\"h11;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Business\"},\"name\":\"Business\",\"id\":\"h12;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Literature\"},\"name\":\"Literature\",\"id\":\"h13;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Books - Miscellaneous\"},\"name\":\"Books - Miscellaneous\",\"id\":\"h14;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Science & Technology\"},\"name\":\"Science & Technology\",\"id\":\"h15;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Sports & Health\"},\"name\":\"Sports & Health\",\"id\":\"h16;8D679D4F11D3E4981000E787EC6DE8A4\"}}]},{\"depth\":0,\"element\":{\"attributeIndex\":0,\"formValues\":{\"DESC\":\"Electronics\"},\"name\":\"Electronics\",\"id\":\"h2;8D679D3711D3E4981000E787EC6DE8A4\"},\"isPartial\":false,\"children\":[{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Audio Equipment\"},\"name\":\"Audio Equipment\",\"id\":\"h21;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Cameras\"},\"name\":\"Cameras\",\"id\":\"h22;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Computers\"},\"name\":\"Computers\",\"id\":\"h23;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Electronics - Miscellaneous\"},\"name\":\"Electronics - Miscellaneous\",\"id\":\"h24;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"TV's\"},\"name\":\"TV's\",\"id\":\"h25;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Video Equipment\"},\"name\":\"Video Equipment\",\"id\":\"h26;8D679D4F11D3E4981000E787EC6DE8A4\"}}]},{\"depth\":0,\"element\":{\"attributeIndex\":0,\"formValues\":{\"DESC\":\"Movies\"},\"name\":\"Movies\",\"id\":\"h3;8D679D3711D3E4981000E787EC6DE8A4\"},\"isPartial\":false,\"children\":[{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Action\"},\"name\":\"Action\",\"id\":\"h31;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Comedy\"},\"name\":\"Comedy\",\"id\":\"h32;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Drama\"},\"name\":\"Drama\",\"id\":\"h33;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Horror\"},\"name\":\"Horror\",\"id\":\"h34;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Kids / Family\"},\"name\":\"Kids / Family\",\"id\":\"h35;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Special Interests\"},\"name\":\"Special Interests\",\"id\":\"h36;8D679D4F11D3E4981000E787EC6DE8A4\"}}]},{\"depth\":0,\"element\":{\"attributeIndex\":0,\"formValues\":{\"DESC\":\"Music\"},\"name\":\"Music\",\"id\":\"h4;8D679D3711D3E4981000E787EC6DE8A4\"},\"isPartial\":true,\"children\":[{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Alternative\"},\"name\":\"Alternative\",\"id\":\"h41;8D679D4F11D3E4981000E787EC6DE8A4\"}},{\"depth\":1,\"element\":{\"attributeIndex\":1,\"formValues\":{\"DESC\":\"Country\"},\"name\":\"Country\",\"id\":\"h42;8D679D4F11D3E4981000E787EC6DE8A4\"}}]}]}}}}";

        List<List<String>> gridData = null;
        try {
            gridData = JsonTreeDataTransformation.transformTreeJsonToGridData(originTreeJsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (List<String> row : gridData) {
            System.out.println(row);
        }

    }
}
