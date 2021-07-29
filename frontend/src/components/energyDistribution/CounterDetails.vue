<template>
  <div>
    <div>История показаний счетчика №{{ counterInfo.sn }}</div>
    <div>
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
        <dx-column data-field="reading.readingTime" caption="Когда" width="auto"/>
        <dx-column data-field="reading.user" caption="Кто" width="auto"/>
        <dx-column data-field="reading.comment" caption="Примечание" width="auto"/>

      </dx-data-grid>
    </div>
  </div>
</template>

<script>
import energyDistributionApi from "@/js/energyDistribition/api/EnergyDistributionApi";
import DxDataGrid, {DxColumn, DxPaging, DxSorting} from 'devextreme-vue/data-grid'

export default {
  name: "CounterDetails",
  components: {
    DxDataGrid, DxColumn, DxPaging, DxSorting
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
    //load details here
    //debounce loading - because component can be mounted several times (refresh grid data with opened details)
    this.consumptionDetails = await this.loadCounterConsumption()
  }
}
</script>