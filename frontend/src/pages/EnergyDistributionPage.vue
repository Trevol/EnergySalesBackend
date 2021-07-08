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
import notify from 'devextreme/ui/notify';
import {makeItems, makeItem} from "@/js/dataItems";
import {randomInt} from "@/js/utils";

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
      items: makeItems(randomInt(2, 40)),
      refreshOptions: {
        icon: 'refresh',
        onClick: () => {
          // this.items[1] = makeItem(randomInt(0, 99))
          this.items = makeItems(randomInt(2, 40))
        }
      },
    }
  }
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