<template>
  <splitpanes class="default-theme">

    <pane key="1" min-size="5">
      <div class="box">
        <div class="row header toolbar">
          <dx-toolbar>
            <dx-toolbar-item :options="refreshOptions"
                             location="before"
                             widget="dxButton"/>
            <dx-toolbar-item
                location="before"
            >
              <dx-select-box
                  :items="months"
                  displayExpr="display"
                  v-model:value="selectedMonth"
                  placeholder="Выбрать"
              />
            </dx-toolbar-item>

            <dx-toolbar-item>
              <div>{{selectedMonth}}</div>
            </dx-toolbar-item>
          </dx-toolbar>
        </div>
        <div class="row content">
          <data-grid :items="data.organizations || []"/>
        </div>
      </div>
    </pane>

    <pane key="2" min-size="0.5">
      <div v-for="org of data.organizations" :key="org.name">{{ org.name }}</div>
    </pane>

  </splitpanes>
</template>

<script>
import DataGrid from "@/components/energyDistribution/EnergyDistributionSheet";
import {Pane, Splitpanes} from "splitpanes";
import 'splitpanes/dist/splitpanes.css'
import DxToolbar, {DxItem as DxToolbarItem} from 'devextreme-vue/toolbar';
import DxSelectBox from 'devextreme-vue/select-box'
import energyDistributionApi from "@/js/energyDistribition/api/EnergyDistributionApi";

export default {
  name: "EnergyDistributionPage",
  components: {
    DataGrid,
    Splitpanes,
    Pane,
    DxToolbar, DxToolbarItem,
    DxSelectBox
  },
  data() {
    return {
      data: {},
      months: [],
      selectedMonth: null,
      refreshOptions: {
        icon: 'refresh',
        onClick: async () => {
          this.data = await this.energyDistribution()
        }
      }
    }
  },
  methods: {
    monthDisplay(monthOfYear) {
      return monthOfYear ? `${monthOfYear.month.toString().padStart(2, '0')}.${monthOfYear.year}` : null
    },
    monthValue(monthOfYear) {
      return monthOfYear ? `${monthOfYear.year}:${monthOfYear.month}`: "null"
    },
    async energyDistribution() {
      return await energyDistributionApi.energyDistribution(this.selectedMonth)
    }
  },
  async mounted() {
    let months = (await energyDistributionApi.monthRange()).toMonthsList();
    this.selectedMonth = null //months[0]
    this.months = months
    this.data = await this.energyDistribution()
  }
}

function monthsRangeToMonthsList(monthRange) {
  return [
    {month: 7, year: 2021},
    {month: 6, year: 2021}
  ]
  // return monthRange
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