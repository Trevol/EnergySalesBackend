<template>
  <dx-tab-panel height="100%" :selected-index="0">

    <dx-item title="Граф потребления">
      <org-unit-consumption-graph :org-unit-info="orgUnitInfo"/>
    </dx-item>

  </dx-tab-panel>
</template>

<script>
import DxTabPanel, {DxItem} from 'devextreme-vue/tab-panel'
import EnergyConsumptionGraph from "@/components/energyDistribution/details/EnergyConsumptionGraph";
import OrgUnitConsumptionGraph from "@/components/energyDistribution/details/OrgUnitConsumptionGraph";

export default {
  name: "OrgUnitDetails",
  components: {
    OrgUnitConsumptionGraph,
    EnergyConsumptionGraph,
    DxTabPanel, DxItem
  },
  props: {
    orgUnitInfo: {
      type: Object
    }
  }
}
</script>