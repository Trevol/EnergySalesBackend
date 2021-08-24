<template>
  <div>
    <input v-model="dialogData">
    <button @click="showDialog=true">
      Show Dialog
    </button>
    <modal v-if="showDialog" width="50%" @close="showDialog=false">
      <template #header>
        <h6>Dialog Header</h6>
      </template>
      <template #body>
        <some-dialog-body :data="dialogData"/>
      </template>
    </modal>
  </div>
</template>

<script>
import Modal from "@/components/common/Modal";
import SomeDialogBody from "@/_experiments/experimentsTab/SomeDialogBody";

export default {
  name: "Experiments",
  components: {
    Modal, SomeDialogBody
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