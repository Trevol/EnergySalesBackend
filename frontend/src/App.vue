<template>
  <DxTabPanel height="100%" :selected-index="0">

    <dx-item title="Распределение электроэнергии">
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

    <dx-item title="Эксперименты">
      <template #default>
        <Experiments/>
      </template>
    </dx-item>

  </DxTabPanel>


</template>

<script>
import "@fortawesome/fontawesome-free/css/all.css"
import "./App.css"
import DxTabPanel, {DxItem} from 'devextreme-vue/tab-panel'
import EnergyDistributionPage from "@/pages/EnergyDistributionPage";
import Experiments from "@/pages/Experiments";

export default {
  components: {
    DxTabPanel, DxItem, EnergyDistributionPage, Experiments
  }
}
</script>
