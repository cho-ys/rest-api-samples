/**
 * Please repalce the originTreeJsonStr variable, it is an json str for report or cube 's raw data, and it is json structure as below:
 * originTreeJsonStr:
 {
  "id": "string",
  "name": "string",
  "instanceId": "string",
  "result": {
    "definition": {
      "attributes": [...],
      "metrics": [...],
      "thresholds": [],
      "sorting": [...]
    },
    "data": {
      "paging": {},
      "root": {
        "isPartial": "boolean",
        "children": [...]
      }
    }
  }
}
 */

//Please repalce the originTreeJsonStr variable
const originTreeJson = {
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "status": 1,
  "instanceId": "1AFF529E11E90A6942510080EF3576A7",
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
                          "dataType": "Char",
                          "baseFormType": "Text"
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
                          "dataType": "Char",
                          "baseFormType": "Text"
                      }
                  ]
              }
          ],
          "metrics": [
              {
                  "name": "Revenue",
                  "id": "4C05177011D3E877C000B3B2D86C964F",
                  "type": "Metric",
                  "min": 400870.8000000001,
                  "max": 400870.8000000001,
                  "numberFormatting": {
                      "category": 1,
                      "decimalPlaces": 0,
                      "thousandSeparator": true,
                      "currencySymbol": "$",
                      "currencyPosition": 0,
                      "formatString": "\"$\"#,##0",
                      "negativeType": 1
                  }
              }
          ],
          "thresholds": [],
          "sorting": []
      },
      "data": {
          "paging": {
              "total": 1,
              "current": 1,
              "offset": 0,
              "limit": 20,
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
                                      "DESC": "Business"
                                  },
                                  "name": "Business",
                                  "id": "h12;8D679D4F11D3E4981000E787EC6DE8A4"
                              },
                              "metrics": {
                                  "Revenue": {
                                      "rv": 400870.8000000001,
                                      "fv": "$400,871",
                                      "mi": 0
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

function transformingTreeJsonToGridData(originJson) {
  let gridData = []

  if (originJson && originJson.result && originJson.result.definition && originJson.result.data) {
    let data = originJson.result.data
    let definition = originJson.result.definition
    let attributes = definition.attributes
    let metrics = definition.metrics

    if ((!attributes || attributes.length === 0) && (!metrics || metrics.length === 0)) {
      return gridData
    }

    let attributeFormInfos = []
    let metricInfos = []
    //collect attributeForms info
    attributeFormInfos = attributes.flatMap(attribute => {
      return attribute.forms ? attribute.forms.map(form => attribute.name + '@' + form.name) : attribute.name
    })

    //collect metrics info
    metricInfos = metrics.map(metric => metric.name)

    const headerTittleRow = [...attributeFormInfos, metricInfos]
    gridData.push(headerTittleRow)


    let dataCellRow = []
    if (data.root) {
      deepVisitTree(data.root, gridData, dataCellRow)
    }

  }

  return gridData
}

/**
 * Algorithm: backtracking 
 * deep visit tree to construct grid data from tree json 
 * @param {*} node 
 * @param {*} gridData 
 * @param {*} row 
 */
function deepVisitTree(node, gridData, row) {
  if (!node) {
    const newRow = row.slice()
    gridData.push(newRow)
    return
  }

  let children = node.children
  let element = node.element
  let metrics = node.metrics

  let formNames = null
  if (element) {
    if (element.formValues) {
      //multi-form: add the attribute form values to row
      let formValueMap = element.formValues
      formNames = Object.keys(formValueMap)
      row = row.concat(formNames.map(formName => formValueMap[formName]))
    } else {
      row.push(element.name)
    }
  }


  if (metrics) { //if metric exists, need handle metric value
    //add the metric values to row
    for (let index in metrics) {
      row.push(metrics[index].rv)
    }
  }

  if (!children) {
    children = new Array()
    children.push(null)
  }
  for (let childindex in children) {
    deepVisitTree(children[childindex], gridData, row)
  }

  if (metrics) { //if metric exists, here need to pop metric value from row
    for (let index in metrics) {
      row.pop()
    }
  }

  if (element) {//if attribute forms exists, here need to pop attribute forms from row
    if (formNames) {
      formNames.forEach((formName, i) => {
        row.pop()
      })
    } else {
      row.pop()
    }
  }

}

/**
 * create table base on transformed cube/report grid data
 */
function autoCreateTable(gridData) {
  if (!gridData || gridData.length === 0) return

  let table = document.createElement("table")
  table.setAttribute("border", "1")
  table.setAttribute("background", "red")

  let line = gridData.length
  let list = gridData[0].length
  for (let i = 0 ; i < line; i++) {
    let tr = document.createElement("tr")
    for (let j = 0 ; j < list; j++) {
      let td = document.createElement("td")
      td.innerHTML = gridData[i][j]
      tr.appendChild(td)
    }
    table.appendChild(tr)
  }
  document.getElementById("app").appendChild(table)
}

function displayGridDataInTable(originTreeJson) {
  let gridData = transformingTreeJsonToGridData(originTreeJson)
  console.log('gird data is:', gridData)
  //show grid data in a table
  autoCreateTable(gridData)
}


displayGridDataInTable(originTreeJson) 