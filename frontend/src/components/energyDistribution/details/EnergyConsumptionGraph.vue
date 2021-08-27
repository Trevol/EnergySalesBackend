<template>
  {{ consumptionData }}
</template>

<script>
export default {
  name: "EnergyConsumptionGraph",
  props: {
    consumptionData: {
      type: Array
    }
  },
  data() {
    return {
    }
  },
  async mounted() {
  }
}
</script>