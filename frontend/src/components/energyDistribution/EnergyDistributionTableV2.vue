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

    <template v-for="topUnit in toplevelUnits" :key="topUnit.id">
      <tr class="parent-organization">
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
          <tr>
            <td v-if="i===0" class="fit-content" rowspan="2">{{ org.name }}</td>
            <td>{{ counter.consumptionByMonth.startingReading?.reading }}</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
        </template>

      </template>
    </template>
    <!--    <tr v-for="it in consumptionPerCounters" v-bind:key="it.id">
          <td class="fit-content">{{ it.organization.name }}</td>
          <td>{{ it.consumptionByMonth.startingReading?.reading }}</td>
          <td>{{ it.consumptionByMonth.endingReading?.reading }}</td>
          <td>{{ it.consumptionByMonth.readingDelta }}</td>
          <td>{{ it.consumptionByMonth.consumption }}</td>
          <td>{{ it.consumptionByMonth.continuousPowerFlow }}</td>
          <td class="fit-content">{{ it.K }}</td>
          <td class="fit-content">{{ it.sn }}</td>
          <td>{{ it.comment }}</td>
        </tr>-->

    </tbody>

  </table>
</template>

<script>
import "@/assets/bootstrap.min.css"
import "./EnergyDistributionTable.css"

export default {
  name: "EnergyDistributionTable",
  props: {
    energyDistributionData: {
      type: Object,
      required: true
    }
  },
  computed: {
    toplevelUnits() {
      return this.energyDistributionData?.toplevelUnits;
    }
  }
}
</script>