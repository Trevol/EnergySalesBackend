import createAxios from "@/js/common/backend_api/axiosApi";

class EnergyDistributionApi {
    constructor(backendApiUri) {
        this._api = createAxios(backendApiUri)
    }

    async monthRange() {
        return await this._api.get("month-range")
            .then(r => r.data)
    }
    async energyDistribution(monthOfYear){
        return await this._api.post("", monthOfYear)
            .then(r => r.data)
    }
}

const energyDistributionApi = new EnergyDistributionApi("http://0.0.0.0:8081/api/energy-distribution")

export default energyDistributionApi
