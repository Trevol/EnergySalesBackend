import createAxios from "@/js/common/backend_api/axiosApi";
import MonthOfYearRange from "@/js/energyDistribition/MonthOfYearRange";
import {backendRootUri} from "@/js/appSettings";
import {uri} from "@/js/common/utils/urlUtils";

class EnergyDistributionApi {
    constructor(backendApiUri) {
        this._api = createAxios(backendApiUri)
    }

    async monthRange() {
        return await this._api.get("month-range")
            .then(r => MonthOfYearRange.fromPlainObject(r.data))
    }

    async energyDistribution(monthOfYear) {
        //wrap value to {monthOfYear: monthOfYear} to correctly send null value
        return await this._api.post("", {monthOfYear})
            .then(r => r.data)
    }

    async energyDistributionV2(monthOfYear) {
        //wrap value to {monthOfYear: monthOfYear} to correctly send null value
        return await this._api.post("v2", {monthOfYear})
            .then(r => r.data)
    }

    async counterEnergyConsumptionDetails(counterId) {
        const conf = {
            params: {
                id: counterId
            }
        }
        return await this._api.get("counter-energy-consumption-details", conf).then(r => r.data)
    }

    async counterEnergyConsumptionByMonths(counterId) {
        const conf = {
            params: {
                counterId
            }
        }
        return await this._api.get("v2/counter-energy-consumption-by-months", conf).then(r => r.data)
    }

    async unitEnergyConsumptionByMonths(orgStructureUnitId) {
        const conf = {
            params: {
                orgStructureUnitId
            }
        }
        return await this._api.get("v2/unit-energy-consumption-by-months", conf).then(r => r.data)
    }
}

const energyDistributionApi = new EnergyDistributionApi(uri(backendRootUri, "/api/energy-distribution"))

export default energyDistributionApi
