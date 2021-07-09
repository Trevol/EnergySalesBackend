import createAxios from "@/js/backend_api/axiosApi";

class BackendApi {
    constructor(backendApiUri) {
        this._api = createAxios(backendApiUri)
    }

    async getDataItems(n) {
        const conf = {
            params: {
                n
            }
        }
        return await this._api.get("data_items", conf)
            .then(r => r.data)
    }

    async postDummyData(dummyData) {
        return await this._api.post("dummy", dummyData)
            .then(r => r.data)
    }
}

const backendApi = new BackendApi("http://0.0.0.0:8081/api")

export default backendApi
