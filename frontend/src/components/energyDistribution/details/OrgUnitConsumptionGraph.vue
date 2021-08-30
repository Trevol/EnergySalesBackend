<template>
  <energy-consumption-graph :consumption-by-months="consumptionByMonths"/>
</template>

<script>
import EnergyConsumptionGraph from "@/components/energyDistribution/details/EnergyConsumptionGraph";
import energyDistributionApi from "@/js/energyDistribition/api/EnergyDistributionApi";

export default {
  name: "OrgUnitConsumptionGraph",
  components: {EnergyConsumptionGraph},
  props: {
    orgUnitInfo: {
      type: Object
    }
  },
  data() {
    return {
      consumptionByMonths: []
    }
  },
  async mounted() {
    this.consumptionByMonths = await energyDistributionApi.unitEnergyConsumptionByMonths(this.orgUnitInfo.id)
  }
}
</script>