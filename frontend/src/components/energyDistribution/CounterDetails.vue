<template>
  <div>
    <div>{{ counterInfo.sn }}</div>
    <div>{{ extendedInfo }}</div>
  </div>
</template>

<script>
import _ from "lodash"
import energyDistributionApi from "@/js/energyDistribition/api/EnergyDistributionApi";

export default {
  name: "CounterDetails",
  props: {
    counterInfo: {
      type: Object
    }
  },
  data() {
    return {
      extendedInfo: null
    }
  },
  methods: {
    async loadCounterConsumption() {
      return await energyDistributionApi.counterEnergyConsumptionDetails(this.counterInfo.id)
    }
  },
  async mounted() {
    //load details here
    //debounce loading - because component can be mounted several times (refresh grid data with opened details)
    this.extendedInfo = await this.loadCounterConsumption()
  }
}
</script>