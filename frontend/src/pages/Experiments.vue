<template>
  <div>
    <input v-model="dialogData">
    <button @click="showDialog=true">
      Show Dialog
    </button>
    <modal v-if="showDialog" width="500px" @close="showDialog=false">
      <template #header>
        <h3>Dialog Header</h3>
      </template>
      <template #body>
        {{ dialogData }}
      </template>
    </modal>
  </div>
</template>

<script>
import Modal from "@/components/common/Modal";

export default {
  name: "Experiments",
  components: {
    Modal
  },
  data() {
    return {
      showDialog: false,
      dialogData: "this dialog data"
    }
  },
  methods: {}
}


</script>