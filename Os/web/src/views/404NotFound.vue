<template>
  <h1>404 Not Found</h1>
</template>

<script>
export default {
  name: "NotFound",
  mounted() {
    this.reloadInterval = setInterval(() => {
      window.location.reload();
    }, 200);
  },
  beforeDestroy() {
    clearInterval(this.reloadInterval);
  }
}
</script>

<style scoped>

</style>
