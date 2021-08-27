<template>
  <dx-tab-panel height="100%" :selected-index="0">

    <dx-item title="Распределение электроэнергии">
      <template #default>
        <energy-distribution-page/>
      </template>
    </dx-item>

<!--    <dx-item title="Эксперименты">
      <template #default>
        <Experiments/>
      </template>
    </dx-item>-->

  </dx-tab-panel>


</template>

<script>
import "@fortawesome/fontawesome-free/css/all.css"
import "./App.css"
import DxTabPanel, {DxItem} from 'devextreme-vue/tab-panel'
import EnergyDistributionPage from "@/components/energyDistribution/EnergyDistributionPage";
// import Experiments from "@/_experiments/experimentsTab/Experiments";

export default {
  components: {
    DxTabPanel, DxItem,
    EnergyDistributionPage,
    // Experiments
  }
}
</script>
