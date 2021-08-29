<template>
  <energy-consumption-graph :consumption-by-months="consumptionByMonths"/>
</template>

<script>
import EnergyConsumptionGraph from "@/components/energyDistribution/details/EnergyConsumptionGraph";
import energyDistributionApi from "@/js/energyDistribition/api/EnergyDistributionApi";
import {toRaw} from "vue"

export default {
  name: "CounterConsumptionGraph",
  components: {EnergyConsumptionGraph},
  props: {
    counterInfo: {
      type: Object
    }
  },
  data() {
    return {
      consumptionByMonths: []
    }
  },
  async mounted() {
    this.consumptionByMonths = await energyDistributionApi.counterEnergyConsumptionByMonths(this.counterInfo.id)
  }
}
</script>