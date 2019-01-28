require('isomorphic-fetch');
const fs = require('fs');

const BASE_URL = "https://demo.microstrategy.com/MicroStrategyLibrary2/api"
const OBJECT_ID = '8CCD8D9D4051A4C533C719A6590DEED8'
const PROJECT_ID = 'B7CA92F04B9FAE8D941C3E9B7E0CD754'

const CONTENT_TYPE = 'application/json'
const ACCEPT = 'application/json'
const DEFAULT_OFFSET = 0
const DEFAULT_LIMIT = 10

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
    /**
    * 1. logging in
    */

    /**
     * 2. publishing cube (Optional step if cube is already published)
     */

    /**
     * 3. Retrieving cube definition
     */

    /**
     * 4. Retrieving cube raw data
     */

    /**
     * 5. Retrieving cube data with instanceId
     */

    /**
     * 6. logging out
     */
}

callREST()



