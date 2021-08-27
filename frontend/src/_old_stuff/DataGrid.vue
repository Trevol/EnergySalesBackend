<!--
<template>
  <DxDataGrid
      :data-source="items"
      :allow-column-resizing="true"
      :selection="{ mode: 'single' }"
      v-model:selected-row-keys="selectedItemKeys"
      key-expr="firstName"
      :hover-state-enabled="true"
      height="100%"
  >
    <DxPaging :pageSize="75" :enabled="false"/>
    <DxSorting mode="none"/>
    <DxScrolling mode="virtual"/>
    <DxColumn caption="First Name" data-field="firstName"></DxColumn>
    <DxColumn caption="Last Name" data-field="lastName"></DxColumn>
  </DxDataGrid>
</template>

<script>
import DxDataGrid, {DxColumn, DxPaging, DxSorting, DxScrolling} from 'devextreme-vue/data-grid'

export default {
  name: "DataGrid",
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
      selectedItemKeys: ["First name 10"]
    }
  },
  methods: {
    onSelectionChanged({selectedRowsData}) {
      const data = selectedRowsData[0];

      this.showEmployeeInfo = !!data;
      this.selectedRowNotes = data && data.Notes;
      this.selectedRowPicture = data && data.Picture;
    }
  }
}
</script>

<style scoped>

</style>-->
