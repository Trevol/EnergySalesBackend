<template>
  <table class="ed-table table table-bordered table-hover table-sm">

    <thead>
    <tr>
      <th scope="col" class="fit-content">Организация</th>
      <th scope="col" class="fit-content">Пред. показ.</th>
      <th scope="col" class="fit-content">Наст. показ.</th>
      <th scope="col">Разница</th>
      <th scope="col">Расход</th>
      <th scope="col">НПМ(квт)</th>
      <th scope="col" class="fit-content">K</th>
      <th scope="col" class="fit-content">№сч-ка</th>
      <th scope="col">Примечание</th>
    </tr>
    </thead>

    <tbody>

    <template v-for="(topUnit, j) in toplevelUnits" :key="topUnit.id">
      <tr v-if="j!==0" class="delimiter">
        <td colspan="9">d</td>
      </tr>
      <tr class="parent-organization" @click.alt="selectOrgUnit(topUnit)" @dblclick="selectOrgUnit(topUnit)">
        <td class="fit-content">{{ topUnit.name }}</td>
        <td></td>
        <td></td>
        <td></td>
        <td>{{ topUnit.total }}</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>

      <template v-for="org in topUnit.organizations" :key="org.id">
        <template v-for="(counter, i) in org.counters" :key="counter.id">
          <tr @click.alt="selectCounter(counter)" @dblclick="selectCounter(counter)">
            <td v-if="i===0" class="fit-content organization-name" :rowspan="org.counters.length">{{ org.name }}</td>
            <td>{{ counter.consumptionByMonth.startingReading?.reading }}</td>
            <td>{{ counter.consumptionByMonth.endingReading?.reading }}</td>
            <td>{{ counter.consumptionByMonth.readingDelta }}</td>
            <td>{{ counter.consumptionByMonth.consumption }}</td>
            <td>{{ counter.consumptionByMonth.continuousPowerFlow }}</td>
            <td>{{ counter.K }}</td>
            <td>{{ counter.sn }}</td>
            <td>{{ counter.comment }}</td>
          </tr>
        </template>

      </template>
    </template>

    </tbody>

  </table>

  <modal v-if="selectedCounter !== null" width="70%" height="500px" @close="selectCounter(null)">
    <template #body>
      <counter-details :counter-info="selectedCounter"/>
    </template>
  </modal>

  <modal v-if="selectedOrgUnit !== null" width="70%" height="500px" @close="selectOrgUnit(null)">
    <template #header>
      <h6>{{ selectedOrgUnit.name }}</h6>
    </template>
    <template #body>
      <org-unit-details :org-unit-info="selectedOrgUnit"/>
    </template>
  </modal>

</template>

<script>
import "@/assets/bootstrap.min.css"
import "./EnergyDistributionTable.css"
import Modal from "@/components/common/Modal";
import CounterDetails from "@/components/energyDistribution/details/CounterDetails";
import OrgUnitDetails from "@/components/energyDistribution/details/OrgUnitDetails";

export default {
  name: "EnergyDistributionTable",
  components: {
    OrgUnitDetails,
    Modal, CounterDetails
  },
  props: {
    energyDistributionData: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      selectedCounter: null,
      selectedOrgUnit: null
    }
  },
  methods: {
    selectCounter(counter) {
      this.selectedCounter = counter
    },
    selectOrgUnit(orgUnit) {
      this.selectedOrgUnit = orgUnit
    }
  },
  computed: {
    toplevelUnits() {
      return this.energyDistributionData?.toplevelUnits;
    }
  }
}
</script>