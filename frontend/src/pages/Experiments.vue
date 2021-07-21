<template>
  <div>
    <dx-select-box
        :items="items"
        v-model:value="selectedItem"
    />
    <div>
      {{ selectedItem }}
    </div>
  </div>
</template>

<script>
import DxSelectBox from 'devextreme-vue/select-box'

export default {
  name: "Experiments",
  components: {
    DxSelectBox
  },
  data() {
    return {
      items: [1, 2, 3],
      selectedItem: 2
    }
  }
}
</script>