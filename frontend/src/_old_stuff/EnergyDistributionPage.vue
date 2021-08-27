<!--
<template>

  <div class="es-box">
    <div class="es-row header toolbar">

      <dx-toolbar style="padding: 0 0">

        <dx-toolbar-item
            location="before">
          <dx-select-box
              :items="months"
              displayExpr="display"
              v-model:value="selectedMonth"
              placeholder="Выбрать"/>
        </dx-toolbar-item>

        <dx-toolbar-item :options="refreshButtonOptions"
                         location="before"
                         widget="dxButton"/>

        <dx-toolbar-item :options="exportToExcelOptions"
                         location="before"
                         widget="dxButton"/>

      </dx-toolbar>

    </div>
    <div class="es-row content">
      <energy-distribution-sheet :energy-distribution-data="energyDistributionData"/>
    </div>
  </div>

</template>

<script>
import "../pages/EnergyDistributionPage.css"
import EnergyDistributionSheet from "@/_old_stuff/EnergyDistributionSheet";
import DxToolbar, {DxItem as DxToolbarItem} from 'devextreme-vue/toolbar';
import DxSelectBox from 'devextreme-vue/select-box'
import energyDistributionApi from "@/js/energyDistribition/api/EnergyDistributionApi";
import {uri} from "@/js/common/utils/urlUtils";
import {backendRootUri} from "@/js/appSettings";

export default {
  name: "EnergyDistributionPage",
  components: {
    EnergyDistributionSheet,
    DxToolbar, DxToolbarItem,
    DxSelectBox
  },
  data() {
    return {
      energyDistributionData: {},
      months: [],
      selectedMonth: null,
      refreshButtonOptions: {
        icon: 'refresh',
        onClick: async () => {
          this.energyDistributionData = await this.energyDistribution()
        }
      },
      exportToExcelOptions: {
        icon: 'far fa-file-excel',
        onClick: async () => {
          window.open(uri(backendRootUri, "download"), "_exportAsXls")
        }
      }
    }
  },
  watch: {
    async selectedMonth(newValue) {
      this.energyDistributionData = await this.energyDistribution()
    }
  },
  methods: {
    async energyDistribution() {
      return await energyDistributionApi.energyDistribution(this.selectedMonth)
    }
  },
  async mounted() {
    let months = (await energyDistributionApi.monthRange()).toMonthsList();
    this.selectedMonth = months[0]
    this.months = months
  }
}
</script>-->
