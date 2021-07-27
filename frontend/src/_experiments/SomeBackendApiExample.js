import createAxios from "@/js/common/backend_api/axiosApi";
import {backendRootUri} from "@/js/appSettings";
import {uri} from "@/js/common/utils/urlUtils";

class SomeBackendApi {
    constructor(backendApiUri) {
        this._api = createAxios(backendApiUri)
    }

    async getExample(n) {
        const conf = {
            params: {
                n
            }
        }
        return await this._api.get("data_items", conf)
            .then(r => r.data)
    }

    async postExample(dummyData) {
        return await this._api.post("dummy", dummyData)
            .then(r => r.data)
    }
}

const someBackendApi = new SomeBackendApi(uri(backendRootUri, "/api/energy-distribution"))

export default someBackendApi
