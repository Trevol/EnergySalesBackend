<template>
  <dx-data-grid
      :data-source="consumptionDetails?.readingsWithConsumption"
      :show-borders="true"
      :show-row-lines="true"
      :allow-column-resizing="true"
      column-resizing-mode="widget"
      key-expr="reading.id"
      :hover-state-enabled="true">
    <DxPaging :enabled="false"/>
    <DxSorting mode="none"/>

    <dx-column data-field="reading.reading" caption="Показания" width="auto"/>
    <dx-column data-field="readingDelta" caption="Разница" width="auto"/>
    <dx-column data-field="consumption" caption="Потребление" width="auto"/>
    <dx-column data-field="continuousPowerFlow" caption="НПМ (квт)" width="auto"/>
    <dx-column data-field="reading.readingTime" caption="Когда" width="auto" data-type="dateTime"/>
    <dx-column data-field="reading.user" caption="Кто" width="auto"/>
    <dx-column data-field="reading.comment" caption="Примечание"/>

  </dx-data-grid>
</template>

<script>
import DxDataGrid, {DxColumn, DxPaging, DxSorting} from "devextreme-vue/data-grid";
import energyDistributionApi from "@/js/energyDistribition/api/EnergyDistributionApi";

export default {
  name: "CounterReadingsHistory",
  components:{
    DxDataGrid, DxColumn, DxPaging, DxSorting,
  },
  props: {
    counterInfo: {
      type: Object
    }
  },
  data() {
    return {
      consumptionDetails: null
    }
  },
  methods: {
    async loadCounterConsumption() {
      return await energyDistributionApi.counterEnergyConsumptionDetails(this.counterInfo.id)
    }
  },
  async mounted() {
    this.consumptionDetails = await this.loadCounterConsumption()
  }
}
</script>

<style scoped>

</style>