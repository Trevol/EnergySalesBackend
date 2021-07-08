<template>
  <DxTabPanel height="100%">

    <dx-item title="Распределение">
      <template #default>
        <energy-distribution-page/>
      </template>
    </dx-item>

    <dx-item title="Организации">
      <template #default>
        Тут организации и счетчики. Редактирование. Печать QR
      </template>
    </dx-item>

    <dx-item title="Настройки">
      <template #default>
        Тут всякое
      </template>
    </dx-item>

  </DxTabPanel>


</template>

<script>
import DxTabPanel, {DxItem} from 'devextreme-vue/tab-panel'
import EnergyDistributionPage from "@/pages/EnergyDistributionPage";


export default {
  components: {
    DxTabPanel, DxItem, EnergyDistributionPage
  }
}
</script>

<style>
html, body, #app {
  width: 100%;
  height: 100%;
  padding: 0;
  margin: 0;
}
</style>
