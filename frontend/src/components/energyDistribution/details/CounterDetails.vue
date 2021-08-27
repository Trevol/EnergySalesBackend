<template>
  <dx-tab-panel height="100%" :selected-index="0">

    <dx-item :title="`История показаний счетчика №${counterInfo.sn}`">
      <counter-readings-history :counter-info="counterInfo"/>
    </dx-item>

    <dx-item title="Граф потребления">
      <energy-consumption-graph :consumption-data="[1, 2, 3]"/>
    </dx-item>

  </dx-tab-panel>
</template>

<script>
import DxTabPanel, {DxItem} from 'devextreme-vue/tab-panel'
import CounterReadingsHistory from "@/components/energyDistribution/details/CounterReadingsHistory";
import EnergyConsumptionGraph from "@/components/energyDistribution/details/EnergyConsumptionGraph";

export default {
  name: "CounterDetails",
  components: {
    EnergyConsumptionGraph,
    CounterReadingsHistory,
    DxTabPanel, DxItem
  },
  props: {
    counterInfo: {
      type: Object
    }
  }
}
</script>