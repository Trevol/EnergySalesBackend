<template>
  <DxDataGrid
      :data-source="items"
      :columns="columns"
      :show-borders="true"
      :show-row-lines="true"
      :allow-column-resizing="true"
      column-resizing-mode="widget"
      :selection="{ mode: 'single' }"
      v-model:selected-row-keys="selectedItemKeys"
      key-expr="id"
      :hover-state-enabled="true"
      height="100%"
  >
    <DxPaging :enabled="false"/>
    <DxSorting mode="none"/>
    <DxScrolling mode="virtual"/>

  </DxDataGrid>
</template>

<script>
import DxDataGrid, {DxColumn, DxPaging, DxSorting, DxScrolling} from 'devextreme-vue/data-grid'
import MonthOfYear from "@/js/energyDistribition/MonthOfYear";

export default {
  name: "EnergyDistributionSheet",
  components: {
    DxDataGrid, DxColumn, DxPaging, DxSorting, DxScrolling
  },
  props: {
    energyDistributionData: {
      type: Object,
      required: true
    }
  },
  computed: {
    items() {
      return this.energyDistributionData?.counters ?? [];
    },
    columns() {
      const monthOfYear = MonthOfYear.fromPlainObject(this.energyDistributionData?.month)
      return [
        {
          caption: "Организация",
          dataField: "organization.name",
          width: "auto"
        },

        {
          caption: "May 2021",
          alignment: 'center',
          columns: [
            {
              caption: "Пред. показ.",
              dataField: "consumptionByMonth.prevMonthReading.reading"
            },
            {
              caption: "Наст. показ.",
              dataField: "consumptionByMonth.monthReading.reading"
            },
            {
              caption: "Расход",
              dataField: "consumptionByMonth.consumption"
            }
          ]
        },

        {
          caption: "K",
          dataField: "K",
          width: "auto"
        },
        {
          caption: "№ счетчика",
          dataField: "sn",
          width: "auto"
        },
        {
          caption: "Примечание",
          dataField: "comment",
          width: "auto"
        },
      ]
    }
  },
  data() {
    return {
      selectedItemKeys: []
    }
  }
}
</script>