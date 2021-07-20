<template>
  <splitpanes class="default-theme">

    <pane key="1" min-size="5">
      <div class="box">
        <div class="row header toolbar">
          <dx-toolbar>
            <dx-toolbar-item :options="refreshOptions"
                             location="before"
                             widget="dxButton"/>
          </dx-toolbar>
        </div>
        <div class="row content">
          <data-grid :items="items"/>
        </div>
      </div>
    </pane>

    <pane key="2" min-size="0.5">
      <div v-for="item of items" :key="item.firstName">{{ item }}</div>
    </pane>

  </splitpanes>
</template>

<script>
import DataGrid from "@/components/DataGrid";
import {Pane, Splitpanes} from "splitpanes";
import 'splitpanes/dist/splitpanes.css'
import DxToolbar, {DxItem as DxToolbarItem} from 'devextreme-vue/toolbar';
import {randomInt} from "@/js/common/utils/utils";
import energyDistributionApi from "@/js/energyDistribition/api/EnergyDistributionApi";

export default {
  name: "EnergyDistributionPage",
  components: {
    DataGrid,
    Splitpanes,
    Pane,
    DxToolbar, DxToolbarItem
  },
  data() {
    return {
      items: [],
      months: [],
      selectedMonth: null,
      refreshOptions: {
        icon: 'refresh',
        onClick: async () => {
          this.items = await this.energyDistribution()
        }
      },
    }
  },
  methods: {
    async energyDistribution() {
      return await energyDistributionApi.energyDistribution(this.selectedMonth)
    }
  },
  async mounted() {
    this.months = monthsRangeToMonthsList(await energyDistributionApi.monthRange())
    this.items = await this.energyDistribution()
  }
}

function monthsRangeToMonthsList(monthRange){
  throw "Not implemented"
}
</script>

<style>
.box {
  display: flex;
  flex-flow: column;
  height: 100%;
}

.box .row {
  /*border: 1px dotted grey;*/
}

.box .row.header {
  flex: 0 1 auto;
  /* The above is shorthand for:
  flex-grow: 0,
  flex-shrink: 1,
  flex-basis: auto
  */
}

.box .row.content {
  flex: 1 1 auto;
  overflow-y: auto
}

.box .row.footer {
  flex: 0 1 auto;
}

.toolbar {
  padding-left: 5px;
  background-color: #fff;
}
</style>