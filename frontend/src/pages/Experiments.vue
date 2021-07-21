<template>
  <div>

    <div>
      {{ selectedItemId || 'null' }}
    </div>
    <dx-select-box
        :items="items"
        displayExpr="name"
        valueExpr="id"
        v-model:value="selectedItemId"
        @selectionChanged="selectionChanged"
    />

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
      items: [new Item(1), new Item(2), new Item(3)],
      selectedItemId: 2
    }
  },
  methods: {
    selectionChanged(e) {
      console.log("selectionChanged!!!!", e.selectedItem.name)
    }
  }
}

class Item {
  constructor(id) {
    this.id = id
    this.name = `Name_${id}`
  }
}
</script>