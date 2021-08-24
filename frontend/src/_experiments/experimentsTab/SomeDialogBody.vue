<template>
  {{ data }}
</template>

<script>
export default {
  name: "SomeDialogBody",
  props: {
    data: {
      type: String,
      required: true
    }
  }
}
</script>