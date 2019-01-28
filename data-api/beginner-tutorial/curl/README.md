## References   
### [Tutorial overview](../README.md)  
### [NodeJs implementation](../nodejs/README.md)  
### [Java implementation](../java/README.md)  

# Curl implementation
## 1. Login In
Authenticate a user with provided **username** and **password** through endpoint [**_POST /auth/login_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Authentication/postLogin) and return an **MicroStrategy auth Token** in the response header.

### Implement as below:
```
curl -X -i POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ 
   "username": "guest", 
   "password": "" 
   
 }' 'https://demo.microstrategy.com/MicroStrategyLibrary2/api/auth/login'
```
### Response code:
```
204
```
### Response header:
````
{
  "status": "204",
  "pragma": "no-cache",
  "date": "Tue, 06 Nov 2018 06:04:33 GMT",
  "cache-control": "no-cache, no-store, max-age=0, must-revalidate",
  "x-mstr-authtoken": "qjtu5qongu8ev62su8nnnm3bco",
  "Set-Cookie": "JSESSIONID=3AD87246016156A33D2501785CEAE3DF; Path=/MicroStrategyLibrary2; Secure; HttpOnly"
  "server": "MicroStrategy",
  "expires": "0",
  "content-type": null
}
````
### 2. Publish Cube 
Publish a cube with provided **cube Id** in a specific project with **project Id** through endpoint [**_POST /cubes/{cubeId}_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/publishCube) .
### Implement as below:
```
curl -X POST --cookie "JSESSIONID=3AD87246016156A33D2501785CEAE3DF" --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-MSTR-AuthToken: r4elvj3js0mlm3q8njiao3e7kq' --header 'X-MSTR-ProjectID: B7CA92F04B9FAE8D941C3E9B7E0CD754' 'https://demo.microstrategy.com/MicroStrategyLibrary2/api/cubes/8CCD8D9D4051A4C533C719A6590DEED8'
```
### Response code:
```
200
```
## 3. Retrieving cube definition
Get the definition of a specific cube with provided **cube id** through endpoint [**_GET /cubes/{cubeId}_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/getDefinition), the cube definition include attributes and metrics in the cube.
### Implement as below:
```
curl -X GET --cookie "JSESSIONID=3AD87246016156A33D2501785CEAE3DF" --header 'Accept: application/json' --header 'X-MSTR-AuthToken: i75h4210v19k8btgvm9t106m8k' --header 'X-MSTR-ProjectID: B7CA92F04B9FAE8D941C3E9B7E0CD754' 'https://demo.microstrategy.com/MicroStrategyLibrary2/api/cubes/8CCD8D9D4051A4C533C719A6590DEED8'
```
### Response code:
```
200
```
### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "result": {
    "definition": {
      "availableObjects": {
        "attributes": [
          {
            "name": "Region",
            "id": "8D679D4B11D3E4981000E787EC6DE8A4",
            "type": "Attribute",
            "forms": [
              {
                "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
                "name": "DESC",
                "dataType": "Char"
              },
              {
                "id": "45C11FA478E745FEA08D781CEA190FE5",
                "name": "ID",
                "dataType": "Real"
              }
            ]
          },
        ...
        ],
        "metrics": [
          {
            "name": "Revenue",
            "id": "4C05177011D3E877C000B3B2D86C964F",
            "type": "Metric"
          },
          ...
        ]
      }
    }
  }
}
```
## 4. Retrieving cube raw data 
### 4.1 limit & offset feature
Create a new instance of a specific cube with provide **cube id** through endpoint [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance) and return the cube **instance Id** and cube **raw data** in the response body. It can be set **limit** and **offset** query parameter to limit returned cube data.
#### Implement as below:
```
curl -X POST --cookie "JSESSIONID=3AD87246016156A33D2501785CEAE3DF" --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-MSTR-AuthToken: 42re87q5hs8eg82bc3pjdcjh2a' --header 'X-MSTR-ProjectID: B7CA92F04B9FAE8D941C3E9B7E0CD754' -d '{}' 'https://demo.microstrategy.com/MicroStrategyLibrary2/api/cubes/8CCD8D9D4051A4C533C719A6590D
```
#### Response code:
```
200
```
#### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "DEF9E30211E8E0DA5D2B0080EFF5FFFD",
  "result": {
    "definition": {
      "attributes": [
        {
          "name": "Call Center",
          "id": "8D679D3511D3E4981000E787EC6DE8A4",
          "type": "Attribute",
          "forms": [
            {
              "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
              "name": "DESC",
              "dataType": "Char"
            }
          ]
        },
        ...
      ],
      "metrics": [
        {
          "name": "Cost",
          "id": "7FD5B69611D5AC76C000D98A4CC5F24F",
          "type": "Metric",
          "min": 40.053,
          "max": 39475.0564,
          "numberFormatting": {
            "category": 1,
            "decimalPlaces": 0,
            "thousandSeparator": true,
            "currencySymbol": "$",
            "currencyPosition": 0,
            "formatString": "\"$\"#,##0",
            "negativeType": 1
          }
        },
        ...
      ],
      "thresholds": [],
      "sorting": []
    },
    "data": {
      "paging": {
        "total": 12960,
        "current": 10,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": true,
        "children": [
          {
            "depth": 0,
            "element": {
              "attributeIndex": 0,
              "formValues": {
                "DESC": "Atlanta"
              },
              "name": "Atlanta",
              "id": "h1;8D679D3511D3E4981000E787EC6DE8A4"
            },
            "isPartial": true,
            "children": [
              {
                "depth": 1,
                "element": {
                  "attributeIndex": 1,
                  "formValues": {
                    "DESC": "Books"
                  },
                  "name": "Books",
                  "id": "h1;8D679D3711D3E4981000E787EC6DE8A4"
                },
                "isPartial": true,
                "children": [
                  {
                    "depth": 2,
                    "element": {
                      "attributeIndex": 2,
                      "formValues": {
                        "DESC": "Jan 2014"
                      },
                      "name": "Jan 2014",
                      "id": "h201401;8D679D4411D3E4981000E787EC6DE8A4"
                    },
                    "isPartial": false,
                    "children": [...]
                  },
                  ...
                ]
              },
              ...
            ]
          },
          ...
        ]
      }
    }
  }
}
```
### 4.2 Retrieving cube data with requestObjects
Create a new instance of a specific cube with provided **cube id** through endpoint  [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance) and return the cube instance Id and cube raw data in the response body. It can be set **requestObjects** json data in the request body to limit returned cube data including specify attributes and metrics. Details see in official document for [RequestedObjects feature](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/RESTSDK/Content/topics/REST_API/REST_API_Filtering_RptsCubes_requestedObjects.htm).

Based on last step, the request body will be added new part: requestObjects  json data.
#### Implement as below:
```
curl -X POST --cookie "JSESSIONID=3AD87246016156A33D2501785CEAE3DF" --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-MSTR-AuthToken: vvcaqfrj90s7jlorobifmouv7d' --header 'X-MSTR-ProjectID: B7CA92F04B9FAE8D941C3E9B7E0CD754' -d '{ 
   "requestedObjects": { 
     "attributes": [ 
       { 
         "name": "Category", 
         "id": "8D679D3711D3E4981000E787EC6DE8A4" 
       }, 
       { 
         "name": "Subcategory", 
         "id": "8D679D4F11D3E4981000E787EC6DE8A4" 
       } 
     ], 
     "metrics": [ 
       { 
         "name": "Cost", 
         "id": "7FD5B69611D5AC76C000D98A4CC5F24F" 
       }, 
       { 
         "name": "Profit", 
         "id": "4C051DB611D3E877C000B3B2D86C964F" 
       } 
     ] 
   } 
 }' 'https://demo.microstrategy.com/MicroStrategyLibrary2/api/cubes/8CCD8D9D4051A4C533C719A6590DEED8/instances?offset=0&limit=10'
```
#### Response code:
```
200
```
#### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "DEF9E30211E8E0DA5D2B0080EFF5FFFD",
  "result": {
    "definition": {
      "attributes": [
        {
          "name": "Category",
          "id": "8D679D3711D3E4981000E787EC6DE8A4",
          "type": "Attribute",
          "forms": [
            {
              "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
              "name": "DESC",
              "dataType": "Char"
            }
          ]
        },
        {
          "name": "Subcategory",
          "id": "8D679D4F11D3E4981000E787EC6DE8A4",
          "type": "Attribute",
          "forms": [
            {
              "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
              "name": "DESC",
              "dataType": "Char"
            }
          ]
        }
      ],
      "metrics": [
        {
          "name": "Cost",
          "id": "7FD5B69611D5AC76C000D98A4CC5F24F",
          "type": "Metric",
          "min": 238242.4329999999,
          "max": 4181261.1674000043,
          "numberFormatting": {
            "category": 1,
            "decimalPlaces": 0,
            "thousandSeparator": true,
            "currencySymbol": "$",
            "currencyPosition": 0,
            "formatString": "\"$\"#,##0",
            "negativeType": 1
          }
        },
        {
          "name": "Profit",
          "id": "4C051DB611D3E877C000B3B2D86C964F",
          "type": "Metric",
          "min": 6708.9268000011,
          "max": 927202.3326000016,
          "numberFormatting": {
            "category": 1,
            "decimalPlaces": 0,
            "thousandSeparator": true,
            "currencySymbol": "$",
            "currencyPosition": 0,
            "formatString": "\"$\"#,##0;(\"$\"#,##0)",
            "negativeType": 3
          }
        }
      ],
      "thresholds": [],
      "sorting": []
    },
    "data": {
      "paging": {
        "total": 24,
        "current": 10,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": true,
        "children": [
          ...
        ]
      }
    }
  }
}
```
### 4.3 Retrieving cube data with sorting
Create a new instance of a specific cube with provided **cube id** through endpoint  [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance)  and return the cube **instance Id** and cube **raw data** in the response body. It can be set **sorting json data** in the request body to sort cube data. Details see in official document for [Sorting feature](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/RESTSDK/Content/topics/REST_API/REST_API_Sorting_data.htm).  

Based on last-step, the request body will be added sorting json data.
#### Implement as below:
```
curl -X POST --cookie "JSESSIONID=3AD87246016156A33D2501785CEAE3DF" --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-MSTR-AuthToken: i19lt2n39jk7miora6hpt48ors' --header 'X-MSTR-ProjectID: B7CA92F04B9FAE8D941C3E9B7E0CD754' -d '{ 
   "requestedObjects": { 
     "attributes": [ 
       { 
         "name": "Category", 
         "id": "8D679D3711D3E4981000E787EC6DE8A4" 
       }, 
       { 
         "name": "Subcategory", 
         "id": "8D679D4F11D3E4981000E787EC6DE8A4" 
       } 
     ], 
     "metrics": [ 
       { 
         "name": "Cost", 
         "id": "7FD5B69611D5AC76C000D98A4CC5F24F" 
       }, 
       { 
         "name": "Profit", 
         "id": "4C051DB611D3E877C000B3B2D86C964F" 
       } 
     ] 
   }, 
   "sorting": [ 
     { 
       "type": "form", 
       "order": "descending", 
       "attribute": { 
         "id": "8D679D3711D3E4981000E787EC6DE8A4", 
         "name": "Category" 
       }, 
       "form": { 
         "id": "CCFBE2A5EADB4F50941FB879CCF1721C", 
         "name": "DESC" 
       } 
     }, 
     { 
       "type": "metric", 
       "metric": { 
         "id": "7FD5B69611D5AC76C000D98A4CC5F24F", 
         "name": "Cost" 
       }, 
       "order": "descending" 
     } 
   ] 
 }' 'https://demo.microstrategy.com/MicroStrategyLibrary2/api/cubes/8CCD8D9D4051A4C533C719A6590DEED8/instances?offset=0&limit=10'
```
#### Response code:
```
200  
```
#### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "FE53EB7A11E8E1945D2B0080EF153DF9",
  "result": {
    "definition": {
      "attributes": [...],
      "metrics": [...],
      "thresholds": [],
      "sorting": [
        {
          "type": "form",
          "attribute": {
            "name": "Category",
            "id": "8D679D3711D3E4981000E787EC6DE8A4"
          },
          "form": {
            "name": "DESC",
            "id": "CCFBE2A5EADB4F50941FB879CCF1721C"
          },
          "order": "descending"
        },
        {
          "type": "metric",
          "metric": {
            "name": "Cost",
            "id": "7FD5B69611D5AC76C000D98A4CC5F24F"
          },
          "order": "descending"
        }
      ]
    },
    "data": {
      "paging": {
        "total": 24,
        "current": 10,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": true,
        "children": [...]
      }
    }
  }
}
```
### 4.4 Retrieving cube data with metricLimits
Create a new instance of a specific cube with provided **cube id** through endpoint  [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance) and return the cube **instance Id** and cube **raw data** in the response body. It can be set **metric limits** json data in the request body to filter cube data based on metric in the template level (grid level). Details see in official document for [Metric limits feature](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/RESTSDK/Content/topics/REST_API/REST_API_Filtering_RptsCubes_metricLimits.htm).

Based on last-step, the request body will be added new part for metric limits json data.
#### Implement as below:
```
curl -X POST --cookie "JSESSIONID=3AD87246016156A33D2501785CEAE3DF" --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-MSTR-AuthToken: i19lt2n39jk7miora6hpt48ors' --header 'X-MSTR-ProjectID: B7CA92F04B9FAE8D941C3E9B7E0CD754' -d '{ 
   "requestedObjects": { 
     "attributes": [ 
       { 
         "name": "Category", 
         "id": "8D679D3711D3E4981000E787EC6DE8A4" 
       }, 
       { 
         "name": "Subcategory", 
         "id": "8D679D4F11D3E4981000E787EC6DE8A4" 
       } 
     ], 
     "metrics": [ 
       { 
         "name": "Cost", 
         "id": "7FD5B69611D5AC76C000D98A4CC5F24F" 
       }, 
       { 
         "name": "Profit", 
         "id": "4C051DB611D3E877C000B3B2D86C964F" 
       } 
     ] 
   }, 
   "sorting": [ 
     { 
       "type": "form", 
       "order": "descending", 
       "attribute": { 
         "id": "8D679D3711D3E4981000E787EC6DE8A4", 
         "name": "Category" 
       }, 
       "form": { 
         "id": "CCFBE2A5EADB4F50941FB879CCF1721C", 
         "name": "DESC" 
       } 
     }, 
     { 
       "type": "metric", 
       "metric": { 
         "id": "7FD5B69611D5AC76C000D98A4CC5F24F", 
         "name": "Cost" 
       }, 
       "order": "descending" 
     } 
   ], 
   "metricLimits": { 
     "4C051DB611D3E877C000B3B2D86C964F": { 
       "operator": "Greater", 
       "operands": [ 
         { 
           "type": "metric", 
           "id": "4C051DB611D3E877C000B3B2D86C964F", 
           "name": "Profit" 
         }, 
         { 
           "type": "constant", 
           "value": "160000", 
           "dataType": "Real" 
         } 
       ] 
     } 
   } 
 }' 'https://demo.microstrategy.com/MicroStrategyLibrary2/api/cubes/8CCD8D9D4051A4C533C719A6590DEED8/instances?offset=0&limit=10'
```
#### Response code:
```
200  
```
#### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "E1A1B43411E8E1955D2B0080EF051FFD",
  "result": {
    "definition": {
      "attributes": [...],
      "metrics": [...],
      "thresholds": [],
      "sorting": [...]
    },
    "data": {
      "paging": {
        "total": 7,
        "current": 7,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": false,
        "children": [
          {
            "depth": 0,
            "element": {
              "attributeIndex": 0,
              "formValues": {
                "DESC": "Electronics"
              },
              "name": "Electronics",
              "id": "h2;8D679D3711D3E4981000E787EC6DE8A4"
            },
            "isPartial": false,
            "children": [
              {
                "depth": 1,
                "element": {
                  "attributeIndex": 1,
                  "formValues": {
                    "DESC": "Video Equipment"
                  },
                  "name": "Video Equipment",
                  "id": "h26;8D679D4F11D3E4981000E787EC6DE8A4"
                },
                "metrics": {
                  "Cost": {
                    "rv": 4181261.1674000043,
                    "fv": "$4,181,261",
                    "mi": 0
                  },
                  "Profit": {
                    "rv": 927202.3326000016,
                    "fv": "$927,202",
                    "mi": 1
                  }
                }
              },
              ...
            ]
          },
          ...
        ]
      }
    }
  }
}
```
### 4.5 Retrieving cube data with viewFilter
Create a new instance of a specific cube with provided **cube id** through endpoint  [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance) and return the cube **instance Id** and cube **raw data** in the response body. It can be set **view filter** json data in the request body to filter cube data based on metric or attribute in the dataset level. Details see in official document for [view filter feature](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/RESTSDK/Content/topics/REST_API/REST_API_Filtering_RptsCubes_ViewFilter.htm).

Based on last-step, the request body will be added new part for view filters json data.
#### Implement as below:
```
curl -X POST --cookie "JSESSIONID=3AD87246016156A33D2501785CEAE3DF" --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-MSTR-AuthToken: i19lt2n39jk7miora6hpt48ors' --header 'X-MSTR-ProjectID: B7CA92F04B9FAE8D941C3E9B7E0CD754' -d '{ 
   "requestedObjects": { 
     "attributes": [ 
       { 
         "name": "Category", 
         "id": "8D679D3711D3E4981000E787EC6DE8A4" 
       }, 
       { 
         "name": "Subcategory", 
         "id": "8D679D4F11D3E4981000E787EC6DE8A4" 
       } 
     ], 
     "metrics": [ 
       { 
         "name": "Cost", 
         "id": "7FD5B69611D5AC76C000D98A4CC5F24F" 
       }, 
       { 
         "name": "Profit", 
         "id": "4C051DB611D3E877C000B3B2D86C964F" 
       } 
     ] 
   }, 
   "sorting": [ 
     { 
       "type": "form", 
       "order": "descending", 
       "attribute": { 
         "id": "8D679D3711D3E4981000E787EC6DE8A4", 
         "name": "Category" 
       }, 
       "form": { 
         "id": "CCFBE2A5EADB4F50941FB879CCF1721C", 
         "name": "DESC" 
       } 
     }, 
     { 
       "type": "metric", 
       "metric": { 
         "id": "7FD5B69611D5AC76C000D98A4CC5F24F", 
         "name": "Cost" 
       }, 
       "order": "descending" 
     } 
   ], 
   "metricLimits": { 
     "4C051DB611D3E877C000B3B2D86C964F": { 
       "operator": "Greater", 
       "operands": [ 
         { 
           "type": "metric", 
           "id": "4C051DB611D3E877C000B3B2D86C964F", 
           "name": "Profit" 
         }, 
         { 
           "type": "constant", 
           "value": "160000", 
           "dataType": "Real" 
         } 
       ] 
     } 
   }, 
   "viewFilter": { 
     "operator": "And", 
     "operands": [ 
       { 
         "operator": "In", 
         "operands": [ 
           { 
             "type": "form", 
             "attribute": { 
               "id": "8D679D3711D3E4981000E787EC6DE8A4", 
               "name": "Category" 
             }, 
             "form": { 
               "id": "CCFBE2A5EADB4F50941FB879CCF1721C", 
               "name": "DESC" 
             } 
           }, 
           { 
             "type": "constants", 
             "dataType": "Char", 
             "values": [ 
               "Books", 
               "Music" 
             ] 
           } 
         ] 
       } 
     ] 
   } 
 }' 'https://demo.microstrategy.com/MicroStrategyLibrary2/api/cubes/8CCD8D9D4051A4C533C719A6590DEED8/instances?offset=0&limit=10'
```
#### Response code:
```
200
```
#### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "1F372B1611E8E197E12F0080EFD593A4",
  "result": {
    "definition": {
      "attributes": [...],
      "metrics": [...],
      "thresholds": [],
      "sorting": [...]
    },
    "data": {
      "paging": {
        "total": 1,
        "current": 1,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": false,
        "children": [
          {
            "depth": 0,
            "element": {
              "attributeIndex": 0,
              "formValues": {
                "DESC": "Books"
              },
              "name": "Books",
              "id": "h1;8D679D3711D3E4981000E787EC6DE8A4"
            },
            "isPartial": false,
            "children": [
              {
                "depth": 1,
                "element": {
                  "attributeIndex": 1,
                  "formValues": {
                    "DESC": "Science & Technology"
                  },
                  "name": "Science & Technology",
                  "id": "h15;8D679D4F11D3E4981000E787EC6DE8A4"
                },
                "metrics": {
                  "Cost": {
                    "rv": 627512.4050000004,
                    "fv": "$627,512",
                    "mi": 0
                  },
                  "Profit": {
                    "rv": 184274.5950000004,
                    "fv": "$184,275",
                    "mi": 1
                  }
                }
              }
            ]
          }
        ]
      }
    }
  }
}
```
## 5. Retrieving cube data with aboved created instanceId
Get the results of a previously **created instance id** of a specific cube with provided **cube id** through endpoint  [**_GET /cubes/{cubeId}/instances/{instanceId}_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/getReport)

Based on created instanceId from last-step response
### Implement as below:
```
curl -X GET --cookie "JSESSIONID=3AD87246016156A33D2501785CEAE3DF" --header 'Accept: application/json' --header 'X-MSTR-AuthToken: henc0q0prfdmqu0tmmme3jcigl' --header 'X-MSTR-ProjectID: B7CA92F04B9FAE8D941C3E9B7E0CD754' 'https://demo.microstrategy.com/MicroStrategyLibrary2/api/cubes/8CCD8D9D4051A4C533C719A6590DEED8/instances/1F372B1611E8E197E12F0080EFD593A4?offset=0&limit=10'
```
### Response code:
```
200
```
### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "1F372B1611E8E197E12F0080EFD593A4",
  "result": {
    "definition": {
      "attributes": [
        {
          "name": "Category",
          "id": "8D679D3711D3E4981000E787EC6DE8A4",
          "type": "Attribute",
          "forms": [
            {
              "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
              "name": "DESC",
              "dataType": "Char"
            }
          ]
        },
        {
          "name": "Subcategory",
          "id": "8D679D4F11D3E4981000E787EC6DE8A4",
          "type": "Attribute",
          "forms": [
            {
              "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
              "name": "DESC",
              "dataType": "Char"
            }
          ]
        }
      ],
      "metrics": [
        {
          "name": "Cost",
          "id": "7FD5B69611D5AC76C000D98A4CC5F24F",
          "type": "Metric",
          "min": 627512.4050000004,
          "max": 627512.4050000004,
          "numberFormatting": {
            "category": 1,
            "decimalPlaces": 0,
            "thousandSeparator": true,
            "currencySymbol": "$",
            "currencyPosition": 0,
            "formatString": "\"$\"#,##0",
            "negativeType": 1
          }
        },
        {
          "name": "Profit",
          "id": "4C051DB611D3E877C000B3B2D86C964F",
          "type": "Metric",
          "min": 184274.5950000004,
          "max": 184274.5950000004,
          "numberFormatting": {
            "category": 1,
            "decimalPlaces": 0,
            "thousandSeparator": true,
            "currencySymbol": "$",
            "currencyPosition": 0,
            "formatString": "\"$\"#,##0;(\"$\"#,##0)",
            "negativeType": 3
          }
        }
      ],
      "thresholds": [],
      "sorting": [
        {
          "type": "form",
          "attribute": {
            "name": "Category",
            "id": "8D679D3711D3E4981000E787EC6DE8A4"
          },
          "form": {
            "name": "DESC",
            "id": "CCFBE2A5EADB4F50941FB879CCF1721C"
          },
          "order": "descending"
        },
        {
          "type": "metric",
          "metric": {
            "name": "Cost",
            "id": "7FD5B69611D5AC76C000D98A4CC5F24F"
          },
          "order": "descending"
        }
      ]
    },
    "data": {
      "paging": {
        "total": 1,
        "current": 1,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": false,
        "children": [
          {
            "depth": 0,
            "element": {
              "attributeIndex": 0,
              "formValues": {
                "DESC": "Books"
              },
              "name": "Books",
              "id": "h1;8D679D3711D3E4981000E787EC6DE8A4"
            },
            "isPartial": false,
            "children": [
              {
                "depth": 1,
                "element": {
                  "attributeIndex": 1,
                  "formValues": {
                    "DESC": "Science & Technology"
                  },
                  "name": "Science & Technology",
                  "id": "h15;8D679D4F11D3E4981000E787EC6DE8A4"
                },
                "metrics": {
                  "Cost": {
                    "rv": 627512.4050000004,
                    "fv": "$627,512",
                    "mi": 0
                  },
                  "Profit": {
                    "rv": 184274.5950000004,
                    "fv": "$184,275",
                    "mi": 1
                  }
                }
              }
            ]
          }
        ]
      }
    }
  }
}
```
## 6. Logout 
Close all existing sessions for the authenticated user through endpoint [**_POST /auth/logout_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Authentication/postLogout) .Logout with the **used authToken**.
### Implement as below:
```
curl -X POST --cookie "JSESSIONID=3AD87246016156A33D2501785CEAE3DF" --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-MSTR-AuthToken: henc0q0prfdmqu0tmmme3jcigl' 'https://demo.microstrategy.com/MicroStrategyLibrary2/api/auth/logout'
```
### Response code:
```
204
```
### Response header:
```
{
  "status": "204",
  "pragma": "no-cache",
  "date": "Tue, 06 Nov 2018 06:08:33 GMT",
  "cache-control": "no-cache, no-store, max-age=0, must-revalidate",
  "server": "MicroStrategy",
  "expires": "0",
  "content-type": null
}
```
