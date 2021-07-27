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
      @cell-prepared="cellPrepared"
  >
    <DxPaging :enabled="false"/>
    <DxSorting mode="none"/>
    <DxScrolling mode="virtual"/>

    <template #organization-cell="{ data }">
      <div style="height: 100%; background-color: greenyellow">
        {{ data.text }}
      </div>

    </template>

  </DxDataGrid>
</template>

<script>
import DxDataGrid, {DxColumn, DxPaging, DxSorting, DxScrolling} from 'devextreme-vue/data-grid'
import MonthOfYear from "@/js/energyDistribition/MonthOfYear";
import {toRaw} from "vue"

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
  data() {
    return {
      selectedItemKeys: []
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
          name: "org",
          width: "auto"
        },
        {
          caption: monthOfYear?.display ?? "",
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
  methods: {
    cellPrepared(cellInfo) {
      if (cellInfo.rowType === "data" &&
          cellInfo.column.name === 'org' &&
          cellInfo.data.organization.numberOfCounters === 2
      ) {
        cellInfo.cellElement.classList.add('orgHasSeveralCounters');
      }
    }
  }
}
</script>

<style>
.orgHasSeveralCounters {
  background-color: greenyellow !important;
}
</style>