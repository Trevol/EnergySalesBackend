<template>
  <DxDataGrid
      :data-source="items"
      :columns="columns"
      :allow-column-resizing="true"
      :selection="{ mode: 'single' }"
      v-model:selected-row-keys="selectedItemKeys"
      key-expr="name"
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
      return this.energyDistributionData?.organizations ?? [];
    },
    columns() {
      return [
        {
          caption: "Организация",
          dataField: "name"
        }
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