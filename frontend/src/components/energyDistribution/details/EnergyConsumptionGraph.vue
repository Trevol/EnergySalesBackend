<template>

  <dx-chart class="consumption-graph" :data-source="dataSource">
    <dx-series
        argument-field="month"
        value-field="consumption"
        type="bar"
        color="#ffaa66"
        :show-in-legend="false"
    >
      <dx-label :visible="true">
      </dx-label>
    </dx-series>
  </dx-chart>

</template>

<script>
import {DxChart, DxSeries, DxLabel} from 'devextreme-vue/chart'

export default {
  name: "EnergyConsumptionGraph",
  components: {
    DxChart, DxSeries, DxLabel
  },
  props: {
    consumptionByMonths: {
      type: Array
    }
  },
  computed: {
    dataSource(){
      return this.consumptionByMonths.map(rawConsumption => (
          {
            month: displayMonth(rawConsumption.month),
            consumption: rawConsumption.consumption
          }
      ))
    }
  }
}
function displayMonth(monthOfYear){
  let monthLbl = monthOfYear.month.toString().padStart(2, '0');
  return `${monthLbl}.${monthOfYear.year}`
}
</script>

<style>
.consumption-graph {
  height: 100%;
}
</style>
