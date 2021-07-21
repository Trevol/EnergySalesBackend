import createAxios from "@/js/common/backend_api/axiosApi";
import MonthOfYearRange from "@/js/energyDistribition/MonthOfYearRange";

class EnergyDistributionApi {
    constructor(backendApiUri) {
        this._api = createAxios(backendApiUri)
    }

    async monthRange() {
        return await this._api.get("month-range")
            .then(r => MonthOfYearRange.fromJson(r.data))
    }

    async energyDistribution(monthOfYear) {
        //wrap value to {monthOfYear: monthOfYear} to correctly send null value
        return await this._api.post("", {monthOfYear})
            .then(r => r.data)
    }
}

const energyDistributionApi = new EnergyDistributionApi("http://0.0.0.0:8081/api/energy-distribution")

export default energyDistributionApi
