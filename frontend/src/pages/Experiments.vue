<template>
  <div>
    <div>
      Selected Item: {{ selectedItem || 'null' }}
    </div>

    <dx-select-box
        :items="items"
        displayExpr="name22"
        v-model:value="selectedItem"
    />

  </div>
</template>

<script>
import SelectBox from "@/components/common/SelectBox";
import DxSelectBox from "devextreme-vue/select-box";

class Item {
  constructor(id) {
    this.id = id
    this.name = `Name_${id}`
  }

  name22() {
    return `Name##${this.id}`
  }
}

let items = [new Item(1), new Item(2), new Item(3)];

export default {
  name: "Experiments",
  components: {
    SelectBox,
    DxSelectBox
  },
  data() {
    return {
      items: items,
      selectedItem: items[-1]
    }
  }
}


</script>