<template>
  <div>
    <div>
      <button @click="addItem">Add item</button>
      <input v-model="itemIndexToChange"/>
      <button @click="changeItem">Change item</button>
      <button @click="removeItem">Remove item</button>
    </div>

    <div v-for="item of items" :key="item.id">{{ item.name22() }}</div>
  </div>
</template>

<script>
import SelectBox from "@/components/common/SelectBox";
import DxSelectBox from "devextreme-vue/select-box";
import {v4 as uuidv4} from 'uuid';
import {ref, reactive, shallowReactive} from "vue"

class Item {
  constructor(id) {
    this.id = id
    this.name = `Name_${id}`
  }

  name22() {
    return this.name//`Name##${this.id}`
  }
}

let initialItems = [new Item(1), new Item(2), new Item(3)];

export default {
  name: "Experiments",
  components: {
    SelectBox,
    DxSelectBox
  },
  setup() {
    const items = shallowReactive(initialItems)
    const itemIndexToChange = ref("")
    return {
      items,
      itemIndexToChange
    }
  },
  methods: {
    addItem() {
      const lastItem = this.items[this.items.length - 1]
      this.items.push(new Item((lastItem?.id ?? 0) + 1))
    },
    changeItem() {
      let itemIndexToChange = parseInt(this.itemIndexToChange)
      if (Number.isNaN(itemIndexToChange)) {
        return
      }
      itemIndexToChange--
      let item = this.items[itemIndexToChange];
      if (!item) {
        return
      }
      item.name = `Name_${item.id}_${uuidv4()}`
    },
    removeItem() {
      let itemIndexToChange = parseInt(this.itemIndexToChange)
      if (Number.isNaN(itemIndexToChange)) {
        return
      }
      itemIndexToChange--
      this.items.splice(itemIndexToChange, 1);
      this.itemIndexToChange = ""
    }
  }
}


</script>