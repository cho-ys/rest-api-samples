require('isomorphic-fetch');
const fs = require('fs');
const REQUESTED_OBJECTS = require('./json/requestedObjects.json')
const SORTING = require('./json/sorting.json')
const METRIC_LIMITS = require('./json/metricLimits.json')
const VIEW_FILTER = require('./json/viewFilter.json')
const USER_INFO = require('./json/userConfig.json')

const BASE_URL = "https://demo.microstrategy.com/MicroStrategyLibrary2/api"
const OBJECT_ID = '8CCD8D9D4051A4C533C719A6590DEED8'
const PROJECT_ID = 'B7CA92F04B9FAE8D941C3E9B7E0CD754'

const CONTENT_TYPE = 'application/json'
const ACCEPT = 'application/json'
const DEFAULT_OFFSET = 0
const DEFAULT_LIMIT = 10

const buildLoginInfo = function () {
    return {
        url: '/auth/login',
        requestInfo: {
            method: 'POST',
            headers: { 'Content-Type': CONTENT_TYPE, 'Accept': ACCEPT },
            body: JSON.stringify(USER_INFO)
        }
    }
}

const buildPublishCubeInfo = function (headers, objectId) {
    return {
        url: `/cubes/${objectId}`,
        requestInfo: {
            method: 'POST',
            headers
        }
    }
}

const buildGetCubeDefinitionInfo = function (headers, objectId) {
    return {
        url: `/cubes/${objectId}`,
        requestInfo: {
            headers: headers
        }
    }
}

const buildPostCubeDataInfo = function (headers, objectId, requestBody, offset = DEFAULT_OFFSET, limit = DEFAULT_LIMIT) {
    return {
        url: `/cubes/${objectId}/instances?offset=${offset}&limit=${limit}`,
        requestInfo: {
            method: 'POST',
            headers,
            body: requestBody
        }
    }
}

const buildGetCubeDataInfo = function (headers, objectId, instanceId, offset = DEFAULT_OFFSET, limit = DEFAULT_LIMIT) {
    return {
        url: `/cubes/${objectId}/instances/${instanceId}?offset=${offset}&limit=${limit}`,
        requestInfo: {
            headers,
            body: '{}'
        }
    }
}

const buildLogoutInfo = function () {
    return {
        url: `/auth/logout`,
        requestInfo: {
            method: 'POST',
            headers: { 'Content-Type': CONTENT_TYPE, 'Accept': ACCEPT }
        }
    }
}

const fetchJsonResultByUrl = function (baseUrl) {
    return async (request) => {
        const result = await fetch(baseUrl + request.url, request.requestInfo).then(e => e.json())
        return result
    }
}

const fetchFullResultByUrl = function (baseUrl) {
    return async (request) => {
        const result = await fetch(baseUrl + request.url, request.requestInfo)
        return result
    }
}

const callREST = async function () {
    const fetchJsonResult = fetchJsonResultByUrl(BASE_URL)
    const fetchFullResult = fetchFullResultByUrl(BASE_URL)
    const loginResult = await fetchFullResult(buildLoginInfo())
    const fileContent = []

    const headerInfo = {
        'Content-Type': CONTENT_TYPE,
        'Accept': ACCEPT,
        'X-MSTR-AuthToken': loginResult.headers.get('x-mstr-authtoken'),
        'X-MSTR-ProjectID': PROJECT_ID,
        'Cookie': loginResult.headers._headers['set-cookie'],
    }
    const loginText = "log in result:" + "\n" + loginResult.status
    console.log(loginText)
    fileContent.push(loginText)

    const publishCubeResult = await fetchFullResult(buildPublishCubeInfo(headerInfo, OBJECT_ID))
    const cubePublishText = "publish cube result:" + "\n" + publishCubeResult.status
    console.log(cubePublishText)
    fileContent.push(cubePublishText)

    const getCubeDefinitionResult = await fetchJsonResult(buildGetCubeDefinitionInfo(headerInfo, OBJECT_ID))
    const cubeDefinitionText = "get cube definition result:" + "\n" + JSON.stringify(getCubeDefinitionResult)
    console.log(cubeDefinitionText)
    fileContent.push(cubeDefinitionText)

    const getCubeDataResult = await fetchJsonResult(buildPostCubeDataInfo(headerInfo, OBJECT_ID, '{}'))
    const cubeAllDataText = "get cube all data result:" + "\n" + JSON.stringify(getCubeDataResult)
    console.log(cubeAllDataText)
    fileContent.push(cubeAllDataText)

    const getRequestObjectsResult = await fetchJsonResult(buildPostCubeDataInfo(headerInfo, OBJECT_ID, JSON.stringify(REQUESTED_OBJECTS)))
    const cubeRequsetObjectsText = "get cube data with request objects result:" + "\n" + JSON.stringify(getRequestObjectsResult)
    console.log(cubeRequsetObjectsText)
    fileContent.push(cubeRequsetObjectsText)

    const getSortingResult = await fetchJsonResult(buildPostCubeDataInfo(headerInfo, OBJECT_ID, JSON.stringify(Object.assign(REQUESTED_OBJECTS, SORTING))))
    const cubeSortingText = "get cube data with request objects and sorting result:" + "\n" + JSON.stringify(getSortingResult)
    console.log(cubeSortingText)
    fileContent.push(cubeSortingText)

    const getMetricLimitsResult = await fetchJsonResult(buildPostCubeDataInfo(headerInfo, OBJECT_ID, JSON.stringify(Object.assign(REQUESTED_OBJECTS, SORTING, METRIC_LIMITS))))
    const cubeMetricLimitsText = "get cube data with request objects, sorting and metric limits result:" + "\n" + JSON.stringify(getMetricLimitsResult)
    console.log(cubeMetricLimitsText)
    fileContent.push(cubeMetricLimitsText)

    const getViewFilterResult = await fetchJsonResult(buildPostCubeDataInfo(headerInfo, OBJECT_ID, JSON.stringify(Object.assign(REQUESTED_OBJECTS, SORTING, METRIC_LIMITS, VIEW_FILTER))))
    const cubeViewFilterText = "get cube data with request objects, sorting, metric limits and view filter result:" + "\n" + JSON.stringify(getViewFilterResult)
    console.log(cubeViewFilterText)
    fileContent.push(cubeViewFilterText)
    const instanceId = getViewFilterResult.instanceId

    const getCubeInstanceDataResult = await fetchJsonResult(buildGetCubeDataInfo(headerInfo, OBJECT_ID, instanceId))
    const cubeInstanceText = "get cube data with by cube instanc " + instanceId + " result:" + "\n" + JSON.stringify(getCubeInstanceDataResult)
    console.log(cubeInstanceText)
    fileContent.push(cubeInstanceText)

    const logoutResult = await fetchFullResult(buildLogoutInfo())
    const logoutText = "log out result:" + "\n" + logoutResult.status
    console.log(logoutText)
    fileContent.push(logoutText)

    fs.writeFile("response", fileContent.join("\n"), function (err) {
            if (err) {
                return console.log(err);
            }
        });
}

callREST()



