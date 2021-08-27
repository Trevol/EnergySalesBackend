<template>
  <dx-tab-panel height="100%" :selected-index="0">

    <dx-item :title="`История показаний счетчика №${counterInfo.sn}`">
      <counter-readings-history :counter-info="counterInfo"/>
    </dx-item>

    <dx-item title="Граф потребления">
      1423412341234
    </dx-item>

  </dx-tab-panel>
</template>

<script>
import DxTabPanel, {DxItem} from 'devextreme-vue/tab-panel'
import CounterReadingsHistory from "@/components/energyDistribution/details/CounterReadingsHistory";

export default {
  name: "CounterDetails",
  components: {
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