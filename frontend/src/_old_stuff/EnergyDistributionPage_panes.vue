<!--
<template>
  <splitpanes class="default-theme">

    <pane key="1" min-size="5">
      <div class="box">
        <div class="row header toolbar">

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
        <div class="row content">
          <energy-distribution-sheet :energy-distribution-data="energyDistributionData"/>
        </div>
      </div>
    </pane>

    <pane key="2" min-size="0" size="3">
    </pane>

  </splitpanes>
</template>

<script>
import "./EnergyDistributionPage.css"
import EnergyDistributionSheet from "@/components/energyDistribution/EnergyDistributionSheet";
import {Pane, Splitpanes} from "splitpanes";
import 'splitpanes/dist/splitpanes.css'
import DxToolbar, {DxItem as DxToolbarItem} from 'devextreme-vue/toolbar';
import DxSelectBox from 'devextreme-vue/select-box'
import energyDistributionApi from "@/js/energyDistribition/api/EnergyDistributionApi";
import {uri} from "@/js/common/utils/urlUtils";
import {backendRootUri} from "@/js/appSettings";

export default {
  name: "EnergyDistributionPage",
  components: {
    EnergyDistributionSheet,
    Splitpanes,
    Pane,
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
