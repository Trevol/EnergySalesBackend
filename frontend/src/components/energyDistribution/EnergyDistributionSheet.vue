<template>
  <DxDataGrid
      :data-source="items"
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

    <DxColumn caption="Организация" data-field="name"></DxColumn>
    <!--    <DxColumn caption="Last Name" data-field="lastName"></DxColumn>-->

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
    items: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      selectedItemKeys: []
    }
  },
  methods: {
    onSelectionChanged({selectedRowsData}) {
      const data = selectedRowsData[0];
    }
  }
}
</script>