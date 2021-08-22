<template>
  <DxTabPanel height="100%" :selected-index="2">

    <dx-item title="Распределение электроэнергии">
      <template #default>
        <energy-distribution-page/>
      </template>
    </dx-item>

    <dx-item title="Распределение электроэнергии v2">
      <template #default>
        <energy-distribution-page2/>
      </template>
    </dx-item>

    <dx-item title="Распределение электроэнергии v3">
      <template #default>
        <energy-distribution-page3/>
      </template>
    </dx-item>

<!--    <dx-item title="Организации">
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
    </dx-item>-->

  </DxTabPanel>


</template>

<script>
import "@fortawesome/fontawesome-free/css/all.css"
import "./App.css"
import DxTabPanel, {DxItem} from 'devextreme-vue/tab-panel'
import EnergyDistributionPage from "@/pages/EnergyDistributionPage";
import EnergyDistributionPage2 from "@/pages/EnergyDistributionPage2";
import EnergyDistributionPage3 from "@/pages/EnergyDistributionPage3";
import Experiments from "@/pages/Experiments";

export default {
  components: {
    DxTabPanel, DxItem, EnergyDistributionPage, EnergyDistributionPage2, EnergyDistributionPage3, Experiments
  }
}
</script>
