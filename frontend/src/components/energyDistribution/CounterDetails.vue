<template>
  <div>{{ counterInfo.sn }}</div>
</template>

<script>
export default {
  name: "CounterDetails",
  props: {
    counterInfo: {
      type: Object
    }
  },
  mounted() {
  }
}
</script>