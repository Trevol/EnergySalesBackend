<template>
  <dx-select-box
      :items="items"
      :displayExpr="displayExpr"/>
</template>

<script>
import DxSelectBox from "devextreme-vue/select-box";

export default {
  name: "SelectBox",
  components: {
    DxSelectBox
  },
  props: {
    items: {
      type: Array,
      required: true,
    },
    displayExpr: {
      type:[String, Function]
    }
  },
  methods: {
    selectionChanged(e) {
      // this.selectedItem = e.selectedItem
    }
  }
}
</script>

<style scoped>

</style>